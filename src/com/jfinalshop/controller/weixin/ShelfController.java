package com.jfinalshop.controller.weixin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.model.Packet;
import com.jfinalshop.model.Shelf;
import com.jfinalshop.util.ImageUtil;


/**
 * Created by dextrys on 2016/1/8.
 */
@ControllerBind(controllerKey = "/weixin/shelf")
public class ShelfController extends BaseWeixinController<Shelf> {

    @Override
    protected Page<Shelf> findAllAvailable(Integer pageNumber, Integer pageSize) {
        return Shelf.dao.findAllAvailable(pageNumber, pageSize);
    }

    public void detail() {
        Shelf shelf = Shelf.dao.findById(getPara("id"));
        String imageText = shelf.getStr("imageText");
        shelf.set("imageText",ImageUtil.convertToWeChatSize(imageText));
        setAttr("shelf", shelf);
        render("/mall/shelf/index.html");
    }

    public void getPackets() {
        String shelfId = getPara("shelf_id");
        if (shelfId == null) {
            renderErrorMessage("货架号不能为空");
            return;
        }
        Page<Packet> pager = Packet.dao.findProductByShelfId(getParaToInt("pageNumber", 1), getParaToInt("pageSize", 10), shelfId);
        renderJson(pager);
    }
}
