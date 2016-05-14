package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.util.CommonUtil;

import java.util.Date;
import java.util.List;

/**
 * Created by dextrys on 2016/1/8.
 */
public class Activity extends Model<Activity>{

  public static final Activity dao = Duang.duang(new Activity());
  public void save(Activity activity) {
    activity.set("id", CommonUtil.getUUID());
    activity.set("createDate", new Date());
    activity.save();
  }
  public boolean delete(String id){
    int count = Db.update("update activity set isDelete = 1  where id = ?", id);
    return count >=0 ? true:false;
  }
  public List<Activity> getAll(){
    return dao.find("select * from activity");
  }

    /**
     *
     * @return 返回所有可用活动
     */
    public Page findAllAvailable(int pageNumber,int pageSize){
      String select = "select a.* ";
      String sqlExceptSelect = "from activity a join shelf s on a.id = s.activity_id where a.isDelete = 0 and ? BETWEEN a.startTime and a.endTime";
      sqlExceptSelect += " order by s.createDate desc ";
      Page<Activity> pager = dao.paginate(pageNumber, pageSize, select, sqlExceptSelect, new Date());
      return pager;
    }
}
