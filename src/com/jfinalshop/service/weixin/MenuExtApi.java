/**
 * Copyright (c) 2011-2014, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinalshop.service.weixin;

import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * menu api
 */
public class MenuExtApi extends MenuApi {

    private static String deleteMune = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";
    private static String createAdditionalMenu = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=";

    /**
     * 删除所有自定义菜单
     */
    public static ApiResult deleteMenu() {
        String jsonResult = HttpUtils.get(deleteMune + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}


