package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.util.CommonUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by dextrys on 2016/1/12.
 */
public class Shelf extends Model<Shelf> {

    public static final Shelf dao = Duang.duang(new Shelf());

    public void save(Shelf shelf) {
        shelf.set("id", CommonUtil.getUUID());
        shelf.set("createDate", new Date());
        shelf.save();
    }

    public boolean delete(String id) {
        int count = Db.update("update shelf set isDelete = 1  where id = ?", id);
        return count >=0 ? true:false;
    }
    public List<Shelf> getAll() {
        return dao.find("select * from shelf");
    }

    public boolean updateState(String activityid){
        int count = Db.update("update shelf set activity_id = null  where activity_id = ?", activityid);
        return count >=0 ?true:false;
    }

    public boolean updatePacketNum(int number,String id){
        int count = Db.update("update shelf set packetNum = packetNum+?  where id = ?", number,id);
        return count >=0 ?true:false;
    }
    /**
     *
     * @return  返回所有当前可用的货架。失效的或者关联不到活动的不在返回列表中
     */
    public Page findAllAvailable(int pageNumber,int pageSize){
        String select = "select s.id,s.name,s.description,s.img,s.channelCode,s.maxBuy ";
        String sqlExceptSelect = "from shelf s  right join activity a on s.activity_id = a.id " +
            "where s.isDelete = 0 and a.isDelete = 0 and s.packetNum > 0  and ? between a.startTime and a.endTime";
        sqlExceptSelect += " order by s.createDate asc ";
        Page<Shelf> pager = dao.paginate(pageNumber,pageSize, select, sqlExceptSelect,new Date());
        return pager;
    }
}
