package com.jfinalshop.controller.weixin;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinalshop.bean.SystemConfig;
import com.jfinalshop.controller.IBaseController;
import com.jfinalshop.interceptor.WeixinUserInfoInterceptor;
import com.jfinalshop.util.SystemConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 后台类 - 管理中心基类
 * 继承此类的子类具备基本的CRUD
 */
@Before(WeixinUserInfoInterceptor.class)
public class BaseWeixinController<M extends Model<M>> extends ApiController implements IBaseController {

    private static final Log log = Log.getLog(BaseWeixinController.class);
    public static final String STATUS = "status";
    public static final String WARN = "warn";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";

    @Override
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


    protected String redirectionUrl = "redirectionUrl";// 操作提示后的跳转URL,为null则返回前一页


    // 获取系统配置信息
    public SystemConfig getSystemConfig() {
        return SystemConfigUtil.getSystemConfig();
    }

    public void renderSuccessMessage(String message, String url) {
        setAttr(MESSAGE, message);
        setAttr(redirectionUrl, getRequest().getContextPath() + url);
        render("/mall/success.html");
    }

    @Clear
    public void renderErrorMessage(String message) {
        setAttr("errorMessages", message);
        setAttr("systemConfig", SystemConfigUtil.getSystemConfig());
        render("/mall/error.html");
    }

    // 输出JSON成功消息，返回null
    public void ajaxJsonSuccessMessage(String message) {
        setAttr(STATUS, SUCCESS);
        setAttr(MESSAGE, message);
        renderJson();
    }

    // 输出JSON错误消息，返回null
    public void ajaxJsonErrorMessage(String message) {
        setAttr(STATUS, ERROR);
        setAttr(MESSAGE, message);
        renderJson();
    }

    // 输出JSON警告消息，返回null
    public void ajaxJsonWarnMessage(String message) {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put(STATUS, WARN);
        jsonMap.put(MESSAGE, message);
        renderJson(jsonMap);
    }

    public void addActionError(String error) {
        JSONArray array = new JSONArray();
        if (!StringUtils.isEmpty(error)) {
            array.add(error);
        }
        String oldMsg = getAttr("errorMessages");
        if (!StringUtils.isEmpty(oldMsg)) {
            array.add(getAttr("errorMessages"));
        }
        setAttr("errorMessages", array);
        render("/mall/error.html");
    }


    public void listAvailable() {
        Integer lastIndex = getParaToInt("lastIndex", 0);
        Integer pageSize = getParaToInt("pageSize", 10);
        Integer pageNumber = 1 + lastIndex / pageSize + (lastIndex % pageSize == 0 ? 0 : 1);
        try {
            Page<?> pager = findAllAvailable(pageNumber, pageSize);
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

    protected Page<?> findAllAvailable(Integer pageNumber, Integer pageSize) {
        return null;
    }

}
