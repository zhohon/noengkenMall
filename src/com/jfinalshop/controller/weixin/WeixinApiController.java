package com.jfinalshop.controller.weixin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinalshop.service.weixin.MenuExtApi;
import com.jfinalshop.util.LowcaseEncode;
import com.jfinalshop.util.RequestUtil;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

@ControllerBind(controllerKey = "/weixin/api")
public class WeixinApiController extends ApiController {

    public static final String oauth2Url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope=snsapi_userinfo&state={2}#wechat_redirect";
    public static final String findNearByUrl = "weixin/lbs/list";
    public static final String mallIndexUrl = "weixin/mall/index";

    /**
     * 如果要支持多公众账号，只需要在此返回各个公众号对应的  ApiConfig 对象即可
     * 可以通过在请求 url 中挂参数来动态从数据库中获取 ApiConfig 属性值
     */
    public ApiConfig getApiConfig() {
        ApiConfig ac = new ApiConfig();

        // 配置微信 API 相关常量
        ac.setToken(PropKit.get("token"));
        ac.setAppId(PropKit.get("appId"));
        ac.setAppSecret(PropKit.get("appSecret"));

        /**
         *  是否对消息进行加密，对应于微信平台的消息加解密方式：
         *  1：true进行加密且必须配置 encodingAesKey
         *  2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
        return ac;
    }

    /**
     * 获取公众号菜单
     */
    public void getMenu() {
        ApiResult apiResult = MenuApi.getMenu();
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getErrorMsg());
    }

    /**
     * 创建菜单
     */
    public void createMenu() {
        JSONObject jsonObject = new JSONObject();
        JSONArray buttons = new JSONArray();
        jsonObject.put("button", buttons);
//        JSONObject nearByButton = new JSONObject();
//        nearByButton.put("name", "周边用户");
//        nearByButton.put("url", createOath2Url(findNearByUrl, this));
//        nearByButton.put("type", "view");
//        buttons.add(nearByButton);
        JSONObject mallButton = new JSONObject();
        mallButton.put("name", "微商城");
        mallButton.put("url", createOath2Url(mallIndexUrl, this));
        mallButton.put("type", "view");
        buttons.add(mallButton);
        ApiResult apiResult = MenuApi.createMenu(jsonObject.toJSONString());
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getJson());
    }

    public static String createOath2Url(String url, ApiController controller) {
        if (url == null) {
            return null;
        }
        if (!url.toLowerCase().startsWith("http")) {
            url = RequestUtil.getFullUrl(controller.getRequest(), url);
        }
        try {
            return MessageFormat.format(oauth2Url, controller.getApiConfig().getAppId(), LowcaseEncode.encode(url, "UTF-8"), "123");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("create oauth2Url error:", e);
        }
    }

    public static String getMallIndexUrl(ApiController controller) {
        return createOath2Url(mallIndexUrl, controller);
    }


    /**
     * 清空公众号菜单
     */
    public void deleteMenu() {
        ApiResult apiResult = MenuExtApi.deleteMenu();
        if (apiResult.isSucceed())
            renderText(apiResult.getJson());
        else
            renderText(apiResult.getErrorMsg());
    }

    /**
     * 获取公众号关注用户
     */
    public void getFollowers() {
        ApiResult apiResult = UserApi.getFollows();
        renderText(apiResult.getJson());
    }

    /**
     * 获取用户信息
     */
    public void getUserInfo() {
        ApiResult apiResult = UserApi.getUserInfo("ohbweuNYB_heu_buiBWZtwgi4xzU");
        renderText(apiResult.getJson());
    }

    /**
     * 发送模板消息
     */
    public void sendMsg() {
        String str = " {\n" +
                "           \"touser\":\"ohbweuNYB_heu_buiBWZtwgi4xzU\",\n" +
                "           \"template_id\":\"9SIa8ph1403NEM3qk3z9-go-p4kBMeh-HGepQZVdA7w\",\n" +
                "           \"url\":\"http://www.sina.com\",\n" +
                "           \"topcolor\":\"#FF0000\",\n" +
                "           \"data\":{\n" +
                "                   \"first\": {\n" +
                "                       \"value\":\"恭喜你购买成功！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"keyword1\":{\n" +
                "                       \"value\":\"去哪儿网发的酒店红包（1个）\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"keyword2\":{\n" +
                "                       \"value\":\"1元\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"remark\":{\n" +
                "                       \"value\":\"欢迎再次购买！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   }\n" +
                "           }\n" +
                "       }";
        ApiResult apiResult = TemplateMsgApi.send(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取参数二维码
     */
    public void getQrcode() {
        String str = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
        ApiResult apiResult = QrcodeApi.create(str);
        renderText(apiResult.getJson());

//        String str = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"123\"}}}";
//        ApiResult apiResult = QrcodeApi.create(str);
//        renderText(apiResult.getJson());
    }

    /**
     * 长链接转成短链接
     */
    public void getShorturl() {
        String str = "{\"action\":\"long2short\"," +
                "\"long_url\":\"http://wap.koudaitong.com/v2/showcase/goods?alias=128wi9shh&spm=h56083&redirect_count=1\"}";
        ApiResult apiResult = ShorturlApi.getShorturl(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取客服聊天记录
     */
    public void getRecord() {
        String str = "{\n" +
                "    \"endtime\" : 987654321,\n" +
                "    \"pageindex\" : 1,\n" +
                "    \"pagesize\" : 10,\n" +
                "    \"starttime\" : 123456789\n" +
                " }";
        ApiResult apiResult = CustomServiceApi.getRecord(str);
        renderText(apiResult.getJson());
    }

    /**
     * 获取微信服务器IP地址
     */
    public void getCallbackIp() {
        ApiResult apiResult = CallbackIpApi.getCallbackIp();
        renderText(apiResult.getJson());
    }
}

