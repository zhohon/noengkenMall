package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinalshop.util.CommonUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by dextrys on 2016/1/12.
 */
public class PacketProductMap extends Model<PacketProductMap> {

    public static final PacketProductMap dao = Duang.duang(new PacketProductMap());

    public void save(PacketProductMap packetProductMap) {
        packetProductMap.set("id", CommonUtil.getUUID());
        packetProductMap.set("createDate", new Date());
        packetProductMap.save();
    }

    public boolean delete(String id) {
        PacketProductMap shelf = dao.findById(id);
        return shelf.delete();
    }

    public boolean deleteMap(String packetid) {
        int count = Db.update("update packetproductmap set isDelete = 1  where packet_id = ?", packetid);
        return count >= 0 ? true : false;
    }

    public List<PacketProductMap> getAll() {
        return dao.find("select * from shelf");
    }

    public List<PacketProductMap> getProductsByPacketId(String packetId) {
        return dao.find("select * from packetproductmap p where p.packet_id=?", packetId);
    }


    public Product.WeightUnit getWeightUnit() {
        return Product.WeightUnit.values()[getInt("weightUnit")];
    }
}
