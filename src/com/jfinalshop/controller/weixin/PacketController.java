package com.jfinalshop.controller.weixin;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.model.Packet;

import java.util.List;


/**
 * Created by dextrys on 2016/1/8.
 */
@ControllerBind(controllerKey = "/weixin/packet")
public class PacketController extends BaseWeixinController<Packet> {
    Log log = Log.getLog(PacketController.class);

    public void detail() {
        Packet product = Packet.dao.findById(getPara("id"));
        setAttr("packet", product);
        render("/mall/packet/index.html");
    }


    public void getPacketImageText() {
        Packet product = Packet.dao.findById(getPara("id"));
        renderJson(product);
    }

    public void getProductParam() {
        JSONArray packets = Packet.dao.getPacketProdParam(getPara("id"));
        renderJson(packets);
    }

    /**
     * 返回热销商品列表
     */
    public void listHot() {
        Integer lastIndex = getParaToInt("lastIndex", 0);
        Integer pageSize = getParaToInt("pageSize", 10);
        Integer pageNumber = 1 + lastIndex / pageSize + (lastIndex % pageSize == 0 ? 0 : 1);
        try {
            Page<?> pager = findHotItem(pageNumber, pageSize);
            if (pager == null) {
                ajaxJsonErrorMessage("此方法未实行分页查询:" + this.getClass());
                return;
            } else if (pager.getList().size() > 0) {
                List<?> list = pager.getList();
                List<?> addList = list.subList(lastIndex % pageSize, list.size());
                pager = new Page(addList, pageNumber, pageSize, pager.getTotalPage(), pager.getTotalRow());
            }
            renderJson(pager);
        } catch (Exception e) {
            log.error("分页查询失败", e);
            ajaxJsonErrorMessage("error:" + e.getMessage());
        }
    }

    protected Page<Packet> findHotItem(Integer pageNumber, Integer pageSize) {
        return Packet.dao.findHotPackets(pageNumber, pageSize);
    }

}
