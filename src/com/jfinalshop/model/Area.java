package com.jfinalshop.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 实体类 - 地区
 */
public class Area extends Model<Area> {

    private static final long serialVersionUID = 4432853203007019067L;

    public static final Area dao = new Area();

    public static final String PATH_SEPARATOR = ",";// 树路径分隔符

    /**
     * 获取所有顶级地区集合;
     *
     * @return 所有顶级地区集合
     */
    public List<Area> getRootAreaList() {
        String sql = "select * from area  where parent_id is null";
        return dao.find(sql);
    }

    /**
     * 根据Area对象获取所有子类集合，若无子类则返回null;
     *
     * @return 子类集合
     */
    public List<Area> getChildrenAreaList(Area area) {
        String sql = "select * from  area where id != ? and area.path like ?";
        return dao.find(sql, area.getStr("id"), area.getStr("path") + "%");
    }

    public Area getParent() {
        String parent_id = getStr("parent_id");
        return dao.findById(parent_id);
    }

    public List<Area> getChildren(String id) {
        String sql = "select * from area t where id != ? and t.path like ?";
        return dao.find(sql, id, id + "%");
    }

    /**
     * 判断地区Path字符串是否正确;
     */
    public Boolean isAreaPath(String areaPath) {
        Area area = dao.findFirst("select * from area where path like ?", areaPath + "%");
        if (area == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据地区路径获取完整地区字符串，若地区路径错误则返回null
     *
     * @param areaPath 地区路径
     */
    public String getAreaString(String areaPath) {
        if (!isAreaPath(areaPath)) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        String[] ids = areaPath.split(Area.PATH_SEPARATOR);
        for (String id : ids) {
            Area area = dao.findById(id);
            stringBuffer.append(area.getStr("name")).append(" ");
        }
        return stringBuffer.toString();
    }

    /**
     * 根据地区字符串获取完整地区路径
     *
     * @param areaNames 地区字符
     */
    @Before(Tx.class)
    public String getAreaPath(String areaNames) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isEmpty(areaNames)) {
            throw new RuntimeException("area cant be empty");
        }
        String parentId = "";
        for (String area : areaNames.split(" ")) {
            String path = findAreaPath(area);
            if (StringUtils.isEmpty(path)) {
                Area newArea = new Area();
                String id = CommonUtil.getUUID();
                newArea.set("id", id);
                newArea.set("name", area);
                newArea.set("path", builder.toString() + id);
                newArea.set("parent_id", parentId);
                newArea.set("createDate", new Date());
                newArea.save();
                parentId = id;
            } else {
                parentId = path;
            }
            builder.append(parentId).append(",");
        }
        if (builder.length() > 1 && builder.lastIndexOf(",") == builder.length() - 1) {
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString();
    }

    /**
     * 获取地区路径根据地区Path字符;
     *
     * @param areaName 地区字符
     */
    public String findAreaPath(String areaName) {
        return Db.queryStr("select id from area where name = ?", areaName);
    }

}
