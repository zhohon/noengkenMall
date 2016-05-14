package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * 实体类 - 字段数据库定义
 */
public class DisplayMapping extends Model<DisplayMapping> {

    public static final DisplayMapping dao = Duang.duang(DisplayMapping.class);

    public DisplayMapping findDisplay(String model, String field) {
        return dao.findFirst("select * from displaymapping where model=? and field=?",
                model, field);
    }
}
