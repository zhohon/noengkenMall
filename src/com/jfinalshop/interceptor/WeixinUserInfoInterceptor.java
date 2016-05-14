package com.jfinalshop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.util.RequestUtil;
import com.jfinalshop.util.SystemConfigUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class WeixinUserInfoInterceptor implements Interceptor {
    Log logger = Log.getLog(this.getClass());

    @Override
    public void intercept(Invocation ai) {
        logger.info("Before invoking " + ai.getActionKey());
        ApiController controller = (ApiController) ai.getController();
        controller.setAttr("systemConfig", SystemConfigUtil.getSystemConfig());
        controller.setAttr("imgPath", PropKit.get("qiniu.url"));
        ApiResult userInfo = null;
        Object userStr = controller.getSessionAttr("userInfo");
        if (userStr != null) {
            userInfo = new ApiResult(userStr.toString());
        }
        String code = controller.getPara("code");
        if (userInfo == null && code != null) {
            SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(controller.getApiConfig().getAppId(), controller.getApiConfig().getAppSecret(), code);
            if (snsAccessToken.getErrorCode() == null) {
                userInfo = SnsApi.getUserInfo(snsAccessToken.getAccessToken(), snsAccessToken.getOpenid());
            } else if (snsAccessToken.getErrorCode() == 40029) {
                //token 过期，再试一次
                snsAccessToken.getRefresh_token();
                userInfo = SnsApi.getUserInfo(snsAccessToken.getAccessToken(), snsAccessToken.getOpenid());
            } else {
                throw new RuntimeException(snsAccessToken.getJson());
            }
            if (userInfo.getErrorCode() == null) {
                updateUserInfo(userInfo, ai);
            } else {
                throw new RuntimeException(userInfo.getJson());
            }
        }
        if (userInfo == null && ApiConfigKit.isDevMode()) {
            userInfo = new ApiResult(PropKit.use("dev/userInfo.properties", "UTF-8").get("userInfo"));
        }
        if (userInfo != null) {
            controller.setSessionAttr("userInfo", userInfo.getJson());
            controller.setAttr("userInfo", userInfo);
            Long cartItemCount = CartItem.dao.getCount(userInfo.getStr("openid"));
            controller.setAttr("cartItemCount", cartItemCount);
        }
        ai.invoke();
        logger.info("After invoking " + ai.getActionKey());
    }

    private void updateUserInfo(ApiResult userInfo, Invocation ai) {
        Controller controller = ai.getController();
        String openid = userInfo.get("openid");
        Member member = Member.dao.findById(openid);
        if (member == null) {
            member = new Member();
            member.set("createDate", new Date());
            member.set("deposit", new BigDecimal("0"));
            member.set("email", "");
            member.set("id", openid);
            member.set("isAccountEnabled", 0);
            member.set("isAccountLocked", 0);
            member.set("lockedDate", null);
            member.set("loginDate", new Date());
            member.set("loginFailureCount", 0);
            member.set("loginIp", RequestUtil.getRemoteHostIp(controller.getRequest()));
            member.set("memberRank_id", MemberRank.dao.getDefaultMemberRank().getStr("id"));
            member.set("password", UUID.randomUUID().toString().replace("-", ""));
            member.set("passwordRecoverKey", null);
            member.set("point", 0);
            member.set("registerIp", RequestUtil.getRemoteHostIp(controller.getRequest()));
            member.set("safeAnswer", null);
            member.set("safeQuestion", null);
            member.set("username", userInfo.get("nickname"));
            member.save();
        } else {
            member.set("loginIp", RequestUtil.getRemoteHostIp(controller.getRequest()));
            member.set("modifyDate", new Date());
            member.update();
        }
    }

}
