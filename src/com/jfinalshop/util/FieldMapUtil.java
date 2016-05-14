package com.jfinalshop.util;

import com.jfinal.plugin.activerecord.*;
import com.jfinalshop.model.Dicts;

import java.util.List;

/**
 * Created by Jack.Zhou on 2/18/2016.
 */
public class FieldMapUtil {
    public static Model map(Model obj) {
        Model model = (Model) obj;
        Table table = TableMapping.me().getTable(DbKit.getUsefulClass(model.getClass()));
        String tableName = table.getName();
        List<Dicts> dicts = Dicts.dao.findByTableName(tableName);
        for (Dicts dict : dicts) {
            String field = dict.getStr("field");
            String value = dict.getStr("dict_value");
            String name = dict.getStr("dict_name");
            String realValue = model.get(field).toString();
            if (realValue != null && realValue.equals(value)) {
                model.set(field, name);
            }
        }
        return obj;
    }

    public static List<?> map(List<?> list) {
        for (Object m : list) {
            if (m instanceof Model) {
                m = map((Model) m);
            }
        }
        return list;
    }
    public static Page<?> map(Page<?> page) {
        for (Object m : page.getList()) {
            if (m instanceof Model) {
                m = map((Model) m);
            }
        }
        return page;
    }
}
