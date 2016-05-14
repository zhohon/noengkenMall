package com.jfinalshop.controller.weixin;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.WeixinUserInfoInterceptor;

/**
 * Created by jack on 2015/12/26.
 * 首页
 */
@ControllerBind(controllerKey = "/weixin/mall")
public class MallIndexController extends BaseWeixinController {

    @Clear
    @Before({WeixinUserInfoInterceptor.class})
    public void index() {
        render("/mall/index.html");
    }

}
