package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * 实体类 - 数据字典
 */
public class Dicts extends Model<Dicts> {

    public static final Dicts dao = Duang.duang(Dicts.class);

    public List<Dicts> getAll() {
        return dao.find("select * from dicts");
    }

    public List<Dicts> findByTableName(String tableName) {
        return dao.find("select * from dicts where model=?", tableName);
    }
}
