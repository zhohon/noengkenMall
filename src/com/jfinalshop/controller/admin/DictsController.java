package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.cache.ClearCache;
import com.jfinal.plugin.cache.J2CacheName;
import com.jfinalshop.model.Dicts;
import com.jfinalshop.util.MetaInfoUtil;

/**
 * Created by Jack.Zhou on 2/16/2016.
 */
public class DictsController extends BaseAdminController<Dicts> {
    @Before({ClearCache.class,Tx.class})
    @J2CacheName(Dicts.class)
    public void refresh() {
        String result = "刷新数据字典失败";
        try {
            String tableName = getPara("table");
            result = MetaInfoUtil.refreshDictAndDisplay(PropKit.get("db.configName"), tableName);
        } catch (Exception e) {
            ajaxJsonErrorMessage("刷新数据字典失败:" + e.getMessage());
            return;
        }
        ajaxJsonSuccessMessage(result);
    }
}
