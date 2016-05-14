package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.cache.ClearCache;
import com.jfinal.plugin.cache.J2CacheName;
import com.jfinalshop.model.*;
import com.jfinalshop.validator.admin.ShelfValidator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dextrys on 2016/1/8.
 */
public class ShelfController extends BaseAdminController<Shelf> {

    public void list() {
        setAttr("shelfTypeList", setShelfType());
        setAttr("shelfActivityList", getActivity());
        findByPageAndTime();
        render("/admin/shelf_list.html");
    }

    public void add() {
        setAttr("shelfTypeList", setShelfType());
        setAttr("shelfActivityList", getActivity());
        render("/admin/shelf_input.html");
    }

    @Before({ShelfValidator.class, ClearCache.class})
    @J2CacheName(Shelf.class)
    public void save() {
        Shelf shelf = getModel(Shelf.class);
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            shelf.set("img", imgUrl);
        }
        saved(shelf);
        redirect("/shelf/list");
    }

    public void edit() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            setAttr("shelf", Shelf.dao.findById(id));
        }
        setAttr("shelfTypeList", setShelfType());
        setAttr("shelfActivityList", getActivity());
        render("/admin/shelf_input.html");
    }

    private List<ShelfType> setShelfType() {
        List<ShelfType> shelfList = new ArrayList<ShelfType>();
        ShelfType shelfType = new ShelfType("1", "类型1");
        shelfList.add(shelfType);
        return shelfList;
    }

    @Before({ShelfValidator.class, ClearCache.class})
    @J2CacheName(Shelf.class)
    public void update() {
        Shelf shelf = getModel(Shelf.class);
        setAttr("shelfTypeList", setShelfType());
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            shelf.set("img", imgUrl);
        }
        updated(shelf);
        redirect("/shelf/list");
    }

    @Before({ClearCache.class, Tx.class})
    @J2CacheName(Shelf.class)
    public void delete() {
        String[] ids = getParaValues("ids");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                Packet.dao.updateState(id);
                if (Shelf.dao.delete(id)) {
                    ajaxJsonSuccessMessage("删除成功！");
                } else {
                    ajaxJsonErrorMessage("删除失败！");
                }
            }
        } else {
            ajaxJsonErrorMessage("id为空未选中，删除失败！");
        }
    }

    public List<Activity> getActivity() {
        return Activity.dao.getAll();
    }

    public void todeletePacket() {
        String shelfid = getPara("shelfid");
        Page<Packet> pager = getModel(Packet.class).paginate(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), "select * ", "from packet where shelf_id = ?", shelfid);
        setAttr("pager", pager);
        setAttr("addPacket", 1);

        setAttr("shelfid", shelfid);
        render("/admin/shelfPacket_list.html");
    }
    @Before( Tx.class)
    public void deletePacket() {
        String[] ids = getParaValues("ids");
        String shelfid = getPara("shelfid");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                if (Packet.dao.updateState(id, null)) {
                    ajaxJsonSuccessMessage("操作成功！");
                } else {
                    ajaxJsonErrorMessage("操作失败！");
                }
            }
            Shelf.dao.updatePacketNum(-ids.length,shelfid);
        } else {
            ajaxJsonErrorMessage("id为空未选中，操作失败！");
        }
    }

    public void toaddPacket() {
        String shelfid = getPara("shelfid");
        Page<Packet> pager = getModel(Packet.class).paginate(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), "select * ", "from packet where shelf_id is null");
        setAttr("pager", pager);
        setAttr("addPacket", 0);
        setAttr("shelfid", shelfid);
        render("/admin/shelfPacket_list.html");
    }
    @Before( Tx.class)
    public void addPacket() {
        String[] ids = getParaValues("ids");
        String shelfid = getPara("shelfid");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                if (Packet.dao.updateState(id, shelfid)) {
                    ajaxJsonSuccessMessage("操作成功！");
                } else {
                    ajaxJsonErrorMessage("操作失败！");
                }
            }
            Shelf.dao.updatePacketNum(ids.length,shelfid);
        } else {
            ajaxJsonErrorMessage("id为空未选中，操作失败！");
        }
    }
}
