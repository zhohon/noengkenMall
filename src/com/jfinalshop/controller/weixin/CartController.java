package com.jfinalshop.controller.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Packet;
import com.jfinalshop.model.PacketProductMap;
import com.jfinalshop.util.CommonUtil;
import com.jfinalshop.util.I18nUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jack.Zhou on 2/2/2016.
 */
@ControllerBind(controllerKey = "/weixin/cart")
public class CartController extends BaseWeixinController<CartItem> {

    Log log = Log.getLog(CartController.class);

    public void index() {
        render("/mall/cart/index.html");
    }

    public void add() {
        ApiResult user = getAttr("userInfo");
        String packetId = getPara("packetId");
        int quantity = getParaToInt("quantity", 1);
        try {
            String openid = user.getStr("openid");
            List<CartItem> oldItems = CartItem.dao.findExist(openid, packetId);
            if (oldItems != null) {
                List<PacketProductMap> items = PacketProductMap.dao.getProductsByPacketId(packetId);
                for (PacketProductMap packetProductMap : items) {
                    CartItem cartItem = new CartItem();
                    CartItem oldItem = findOldItem(oldItems, packetProductMap.get("product_id"));
                    cartItem.set("member_id", openid);
                    cartItem.set("packet_id", packetId);
                    cartItem.set("product_id", packetProductMap.get("product_id"));
                    if (oldItem != null) {
                        cartItem.set("quantity", packetProductMap.getInt("num") * quantity + oldItem.getInt("quantity"));
                        cartItem.set("id", oldItem.getStr("id"));
                        cartItem.set("modifyDate", new Date());
                        cartItem.update();
                    } else {
                        cartItem.set("quantity", packetProductMap.getInt("num") * quantity);
                        cartItem.set("id", CommonUtil.getUUID());
                        cartItem.set("createDate", new Date());
                        cartItem.save();
                    }
                }
            } else {
                List<PacketProductMap> items = PacketProductMap.dao.getProductsByPacketId(packetId);
                for (PacketProductMap packetProductMap : items) {
                    CartItem cartItem = new CartItem();
                    cartItem.set("member_id", openid);
                    cartItem.set("packet_id", packetId);
                    cartItem.set("product_id", packetProductMap.get("product_id"));
                    cartItem.set("quantity", packetProductMap.getInt("num") * quantity);
                    cartItem.set("id", CommonUtil.getUUID());
                    cartItem.set("createDate", new Date());
                    cartItem.save();
                }
            }
            setAttr("cartItemCount", CartItem.dao.getCount(openid));
        } catch (Exception e) {
            ajaxJsonErrorMessage("error:" + e.getMessage());
            return;
        }
        ajaxJsonSuccessMessage(I18nUtil.convert("Info.Cart.AddSuccess", this));
    }

    private CartItem findOldItem(List<CartItem> oldItems, Object product_id) {
        for (CartItem item : oldItems) {
            if (product_id.equals(item.getStr("product_id"))) {
                return item;
            }
        }
        return null;
    }

    protected Page<Packet> findAllAvailable(Integer pageNumber, Integer pageSize) {
        ApiResult user = getAttr("userInfo");
        String openid = user.get("openid");
        return Packet.dao.findByMemberId(openid, pageNumber, pageSize);
    }

    public void delete() {
        ApiResult user = getAttr("userInfo");
        String packets = getPara("ids");
        List<String> removedPackets = new ArrayList<String>();
        try {
            String openid = user.getStr("openid");
            String[] packetIds = new String[0];
            if (packets != null) {
                packetIds = packets.split(",");
            }
            for (String packetId : packetIds) {
                CartItem cart = CartItem.dao.findByPacketId(packetId);
                if (cart == null) {
                    String errorMsg = I18nUtil.convert("Error.Cart.DataRemoved", this);
                    log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                    ajaxJsonErrorMessage(errorMsg);
                    return;
                }
                if (!openid.equals(cart.getStr("member_id"))) {
                    String errorMsg = I18nUtil.convert("Error.Cart.Delete", this);
                    log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                    ajaxJsonErrorMessage(errorMsg);
                    return;
                }
                cart.delete();
                removedPackets.add(packetId);
            }
            setAttr("cartItemCount", CartItem.dao.getCount(openid));
        } catch (Exception e) {
            ajaxJsonErrorMessage("error:" + e.getMessage());
            return;
        }
        ajaxJsonSuccessMessage(JSON.toJSONString(removedPackets));
    }

    public void edit() {
        ApiResult user = getAttr("userInfo");
        String packetId = getPara("id");
        long quantity = getParaToLong("quantity");
        String openid = user.getStr("openid");
        try {
            CartItem cart = CartItem.dao.findByPacketId(packetId);
            if (cart == null) {
                String errorMsg = I18nUtil.convert("Error.Cart.DataRemoved", this);
                log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                ajaxJsonErrorMessage(errorMsg);
                return;
            }
            if (!openid.equals(cart.getStr("member_id"))) {
                String errorMsg = I18nUtil.convert("Error.Cart.UserNOTValid", this);
                log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                ajaxJsonErrorMessage(errorMsg);
                return;
            }
            cart.set("modifyDate", new Date());
            cart.set("quantity", quantity);
            cart.update();
            setAttr("cartItemCount", CartItem.dao.getCount(openid));
        } catch (Exception e) {
            String errorMsg = I18nUtil.convert("Error.Cart.Edit", this);
            log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
            ajaxJsonErrorMessage(errorMsg);
            return;
        }
        ajaxJsonSuccessMessage(I18nUtil.convert("Info.Cart.EditSuccess", this));
    }

    @Before(Tx.class)
    public void postOrder() {
        ApiResult user = getAttr("userInfo");
        List<String> cartIds = new ArrayList<String>();
        JSONArray packets = JSON.parseArray(getPara("packets"));
        String openid = user.getStr("openid");
        String packetId = "";
        try {
            for (int i = 0; i < packets.size(); i++) {
                JSONObject packet = packets.getJSONObject(i);
                packetId = packet.getString("packetId");
                CartItem cart = CartItem.dao.findByPacketId(packetId);
                if (cart == null) {
                    String errorMsg = I18nUtil.convert("Error.Cart.UserNOTValid", this);
                    log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                    ajaxJsonErrorMessage(errorMsg);
                    return;
                }
                if (!openid.equals(cart.getStr("member_id"))) {
                    String errorMsg = I18nUtil.convert("Error.Cart.UserNOTValid", this);
                    log.warn(errorMsg + I18nUtil.convert("Error.Cart.LogInfo", this, openid, packetId));
                    ajaxJsonErrorMessage(errorMsg);
                    return;
                }
                cart.set("quantity", packet.getString("quantity"));
                cart.set("modifyDate", new Date());
                cart.update();
                cartIds.add(cart.getStr("id"));
            }
        } catch (Exception e) {
            String errorMsg = I18nUtil.convert("Error.Cart.UserNOTValid", this);
            log.warn(errorMsg + I18nUtil.convert("Error.Cart.PostOrder", this, openid, packetId));
            ajaxJsonErrorMessage(errorMsg);
        }
        setAttr("cartIds", cartIds);
        ajaxJsonSuccessMessage(I18nUtil.convert("Info.Cart.CreateSuccess", this));
    }
}
