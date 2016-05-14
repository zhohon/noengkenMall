package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.util.CommonUtil;
import com.jfinalshop.util.SystemConfigUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 实体类 - 购物车项
 */
public class CartItem extends Model<CartItem> {

    private static final long serialVersionUID = -2752867902533206652L;
    public static final CartItem dao = new CartItem();

    // 商品
    public Product getProduct() {
        return Product.dao.findById(getStr("product_id"));
    }

    // 用户
    public Member getMember() {
        return Member.dao.findById(getStr("member_id"));
    }

    //重写save
    public boolean save(CartItem cartItem) {
        cartItem.set("id", CommonUtil.getUUID());
        cartItem.set("createDate", new Date());
        return cartItem.save();
    }

    // 获取优惠价格，若member对象为null则返回原价格
    public BigDecimal getPreferentialPrice() {
        Member member = getMember();
        Product product = getProduct();
        if (member != null) {
            BigDecimal preferentialPrice = product.getBigDecimal("price").multiply(new BigDecimal(member.getMemberRank().getDouble("preferentialScale").toString()).divide(new BigDecimal("100")));
            return SystemConfigUtil.getPriceScaleBigDecimal(preferentialPrice);
        } else {
            return product.getBigDecimal("price");
        }
    }

    // 获取小计价格
    public BigDecimal getSubtotalPrice() {
        BigDecimal subtotalPrice = getPreferentialPrice().multiply(new BigDecimal(getInt("quantity").toString()));
        return SystemConfigUtil.getOrderScaleBigDecimal(subtotalPrice);
    }

    public Long getCount(String openid) {
        String select = "SELECT COUNT(DISTINCT packet_id) AS quantity from cartitem where member_id=? and is_order_created=0";
        return Db.queryLong(select, openid);
    }

    public List<CartItem> findExist(String openid, String cartItemId) {
        String select = "SELECT * from cartitem where member_id=? and packet_id=? and is_order_created=0";
        return dao.find(select, openid, cartItemId);
    }

    public CartItem findByPacketId(String packetId) {
        String select = "SELECT * from cartitem where packet_id=? and is_order_created=0";
        return dao.findFirst(select, packetId);
    }
}
