package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.cache.ClearCache;
import com.jfinal.plugin.cache.J2CacheName;
import com.jfinalshop.model.Activity;
import com.jfinalshop.model.ActivityType;
import com.jfinalshop.model.Shelf;
import com.jfinalshop.validator.admin.ActivityValidator;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by dextrys on 2016/1/8.
 */
public class ActivityController extends BaseAdminController<Activity> {
    private Activity activity = new Activity();

    public void list() {
        setAttr("activityTypeList", setActivityType());
        findByPageAndTime();
        render("/admin/activity_list.html");
    }

    public void add() {
        setAttr("activityTypeList", setActivityType());
        render("/admin/activity_input.html");
    }

    @Before({ClearCache.class, ActivityValidator.class})
    @J2CacheName(Activity.class)
    public void save() {
        Activity activity = getModel(Activity.class);
        String startTime1 = getPara("startTime1");
        String endTime1 = getPara("endTime1");
        String[] pattern = new String[]{"yyyy-MM", "yyyyMM", "yyyy/MM",
                "yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMddHHmmss",
                "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"};
        try {
            Date startTime = DateUtils.parseDate(startTime1, pattern);
            Date endTime = DateUtils.parseDate(endTime1, pattern);
            activity.set("startTime", startTime);
            activity.set("endTime", endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        activity.set("bannerUrl", handleUpload(FileType.IMAGE));
        activity.save(activity);
        redirect("/activity/list");
    }

    public void edit() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            setAttr("activity", Activity.dao.findById(id));
        }
        setAttr("activityTypeList", setActivityType());
        render("/admin/activity_input.html");
    }

    private List<ActivityType> setActivityType() {
        List<ActivityType> activityList = new ArrayList();
        ActivityType activityType = new ActivityType("1", "类型1");
        activityList.add(activityType);
        return activityList;
    }

    @Before({ClearCache.class, ActivityValidator.class})
    @J2CacheName(Activity.class)
    public void update() {
        Activity activity = getModel(Activity.class);
        String startTime1 = getPara("startTime1");
        String endTime1 = getPara("endTime1");
        String[] pattern = new String[]{"yyyy-MM", "yyyyMM", "yyyy/MM",
                "yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMddHHmmss",
                "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm"};
        try {
            Date startTime = DateUtils.parseDate(startTime1, pattern);
            Date endTime = DateUtils.parseDate(endTime1, pattern);
            activity.set("startTime", startTime);
            activity.set("endTime", endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setAttr("activityTypeList", setActivityType());
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            activity.set("bannerUrl", imgUrl);
        }
        updated(activity);
        redirect("/activity/list");
    }

    @Before(ClearCache.class)
    @J2CacheName(Activity.class)
    public void delete() {
        String[] ids = getParaValues("ids");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                Shelf.dao.updateState(id);
                if (Activity.dao.delete(id)) {
                    ajaxJsonSuccessMessage("删除成功！");
                } else {
                    ajaxJsonErrorMessage("删除失败！");
                }
            }
        } else {
            ajaxJsonErrorMessage("id为空未选中，删除失败！");
        }
    }

    public void lookShelf() {
        setAttr("shelfActivityList", Activity.dao.getAll());
        String activityid = getPara("id");
        Page<Shelf> pager = getModel(Shelf.class).paginate(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), "select * ", "from shelf where activity_id = ?", activityid);
        setAttr("pager", pager);
        render("/admin/lookShelf_list.html");
    }
}
