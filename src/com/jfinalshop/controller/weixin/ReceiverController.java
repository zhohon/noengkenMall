package com.jfinalshop.controller.weixin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.validator.shop.ReceiverValidator;

import java.util.List;

/**
 * Created by Jack.Zhou on 2/2/2016.
 */
@ControllerBind(controllerKey = "/weixin/receiver")
public class ReceiverController extends BaseWeixinController<OrderItem> {

    Log log = Log.getLog(ReceiverController.class);

    public void index() {
        ApiResult user = getAttr("userInfo");
        try {
            String openid = user.getStr("openid");
            List<Receiver> receiverList = Receiver.dao.getReceiverList(openid);
            setAttr("receiverList", receiverList);
            setAttr("receiverId",getPara("receiverId",""));
        } catch (Exception e) {
            log.error("get receiver error", e);
            throw new RuntimeException("get receiver error", e);
        }
        render("/mall/receiver/index.html");
    }

    public void newReceiver() {
        render("/mall/receiver-input/index.html");
    }

    public void editReceiver() {
        String id = getPara("id");
        Receiver receiver = Receiver.dao.findById(id);
        setAttr("receiver", receiver);
        render("/mall/receiver-input/index.html");
    }

    // 收货地址添加
    @Before({ReceiverValidator.class, Tx.class})
    public void save() {
        ApiResult user = getAttr("userInfo");
        try {
            String openid = user.getStr("openid");
            List<Receiver> receiverList = Receiver.dao.getReceiverList(openid);
            Receiver receiver = getModel(Receiver.class);
            String receiverId = receiver.getStr("id");
            Receiver old = Receiver.dao.findById(receiverId);
            if(old!=null){//修改
                String areaNames = receiver.getStr("areaPath");
                String areaPath = Area.dao.getAreaPath(areaNames);
                receiver.set("areaPath", areaPath);
                receiver.set("member_id", openid);
                receiver.update(receiver);
            }else{//新增
                if (receiverList != null && Receiver.MAX_RECEIVER_COUNT != null && receiverList.size() >= Receiver.MAX_RECEIVER_COUNT) {
                    addActionError("只允许添加最多" + Receiver.MAX_RECEIVER_COUNT + "项收货地址!");
                    return;
                }
                String areaNames = receiver.getStr("areaPath");
                String areaPath = Area.dao.getAreaPath(areaNames);
                receiver.set("areaPath", areaPath);
                receiver.set("member_id", openid);
                receiver.save(receiver);
            }
            redirect("/weixin/receiver/index?receiverId="+receiver.getStr("id"));
        } catch (Exception e) {
            log.error("get receiver error", e);
            throw new RuntimeException("get receiver error", e);
        }
    }

}
