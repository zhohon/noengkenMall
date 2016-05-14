package com.jfinalshop.controller.weixin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.model.Activity;


/**
 * Created by dextrys on 2016/1/8.
 */
@ControllerBind(controllerKey = "/weixin/activity")
public class ActivityController extends BaseWeixinController<Activity> {

    @Override
    protected Page<Activity> findAllAvailable(Integer pageNumber, Integer pageSize) {
        return Activity.dao.findAllAvailable(pageNumber, pageSize);
    }

    public void detail() {
        Activity activity = Activity.dao.findById(getPara("id"));
        setAttr("activity", activity);
        render("/mall/activity/index.html");
    }

}
