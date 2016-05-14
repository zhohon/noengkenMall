package com.jfinalshop.controller.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinalshop.model.*;
import com.jfinalshop.util.ArithUtil;
import com.jfinalshop.util.SystemConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack.Zhou on 2/2/2016.
 */
@ControllerBind(controllerKey = "/weixin/order")
public class OrderController extends BaseWeixinController<OrderItem> {

    Log log = Log.getLog(OrderController.class);

    public void index() {
        ApiResult user = getAttr("userInfo");
        String openid = user.getStr("openid");
        String cartIds = getPara("cartIds");
        if (StringUtils.isEmpty(cartIds)) {
            cartIds = getSessionAttr("cartIds");
        }
        setSessionAttr("cartIds", cartIds);
        String receiverId = getPara("receiverId");
        Receiver receiver = null;
        try {
            if (StringUtils.isEmpty(receiverId)) {
                receiver = Receiver.dao.getDefaultReceiverByMemberId(openid);
            } else {
                receiver = Receiver.dao.findById(receiverId);
            }
            setAttr("receiver", receiver);
            //order details
            JSONArray orderItems = new JSONArray();
            BigDecimal totalPrice = new BigDecimal(0);
            Double totalWeightGram = 0D;
            for (String cartId : cartIds.split(",")) {
                JSONObject item = new JSONObject();
                CartItem cartItem = CartItem.dao.findById(cartId);
                if (cartItem.getBoolean("is_order_created")) {
                    setAttr("receiver", null);
                    continue;
                }
                int quantity = cartItem.getInt("quantity");
                String packetId = cartItem.getStr("packet_id");
                Packet packet = Packet.dao.findById(packetId);
                item.put("quantity", quantity);
                item.put("packet", packet);
                item.put("brand", Brand.dao.findById(packet.get("brand_id")));
                List<Product> products = Product.dao.getProductsByPacketId(packetId);
                for (Product product : products) {
                    Double weightGram = DeliveryType.toWeightGram(product.getDouble("weight"), product.getWeightUnit());
                    totalWeightGram = ArithUtil.add(totalWeightGram, ArithUtil.mul(weightGram, quantity));
                }
                String img = packet.getStr("img");
                if (StringUtils.isNotEmpty(img)) {
                    JSONArray images = JSON.parseArray(img);
                    img = images.getJSONObject(0).getString("smallProductImagePath");
                }
                BigDecimal price = packet.getBigDecimal("price");
                item.put("img", img);
                item.put("products", products);
                orderItems.add(item);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(quantity)));
            }
            setAttr("orderItems", orderItems);
            setAttr("totalPrice", SystemConfigUtil.getOrderScaleBigDecimal(totalPrice));
            setDeliveryType(totalWeightGram);
            setWeight(totalWeightGram);
        } catch (Exception e) {
            log.error("get receiver error", e);
        }
        render("/mall/order/index.html");
    }

    private void setWeight(Double totalWeightGram) {
        if (totalWeightGram < 1000) {
            setAttr("productWeight", totalWeightGram);
            setAttr("productWeightUnit", Product.WeightUnit.valueOf(Product.WeightUnit.g.name()));
        } else if (totalWeightGram >= 1000 && totalWeightGram < 1000000) {
            setAttr("productWeight", totalWeightGram / 1000);
            setAttr("productWeightUnit", Product.WeightUnit.valueOf(Product.WeightUnit.kg.name()));
        } else if (totalWeightGram >= 1000000) {
            setAttr("productWeight", totalWeightGram / 1000000);
            setAttr("productWeightUnit", Product.WeightUnit.valueOf(Product.WeightUnit.t.name()));
        }
    }

    private void setDeliveryType(Double totalWeightGram) {
        List<DeliveryType> deliveryTypes = DeliveryType.dao.getAll();
        List<Record> types = new ArrayList<Record>();
        for (DeliveryType deliveryType : deliveryTypes) {
            BigDecimal deliveryFee = deliveryType.getDeliveryFee(totalWeightGram);
            types.add(deliveryType.toRecord().set("deliveryFee", deliveryFee));
        }
        setAttr("allDeliveryType", types);// 获取所有配送方式
    }

    public void buy() {
        ApiResult user = getAttr("userInfo");
        String packetId = getPara("packetId");
        String receiverId = getPara("receiverId");
        String openid = user.getStr("openid");
        try {
            //receiver
            Receiver receiver = null;
            if (StringUtils.isEmpty(receiverId)) {
                receiver = Receiver.dao.getDefaultReceiverByMemberId(openid);
            } else {
                receiver = Receiver.dao.findById(receiverId);
            }
            setAttr("receiver", receiver);
            //order details
            JSONArray orderItems = new JSONArray();
            JSONObject item = new JSONObject();
            Packet packet = Packet.dao.findById(packetId);
            Double totalWeightGram = 0D;
            item.put("quantity", 1);
            item.put("packet", packet);
            item.put("brand", Brand.dao.findById(packet.get("brand_id")));
            List<PacketProductMap> products = PacketProductMap.dao.getProductsByPacketId(packetId);
            for (PacketProductMap product : products) {
                Double weightGram = DeliveryType.toWeightGram(product.getDouble("weight"), product.getWeightUnit());
                totalWeightGram = ArithUtil.add(totalWeightGram, ArithUtil.mul(weightGram, product.getInt("num")));
            }
            String img = packet.getStr("img");
            if (StringUtils.isNotEmpty(img)) {
                JSONArray images = JSON.parseArray(img);
                img = images.getJSONObject(0).getString("smallProductImagePath");
            }
            item.put("img", img);
            item.put("products", products);
            orderItems.add(item);
            setAttr("orderItems", orderItems);
            setAttr("totalPrice", packet.getBigDecimal("price"));
            setDeliveryType(totalWeightGram);
            setWeight(totalWeightGram);
        } catch (Exception e) {
            log.error("get receiver error", e);
        }
        render("/mall/order/index.html");
    }
}
