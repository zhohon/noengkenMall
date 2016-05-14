package com.jfinalshop.controller.admin;

import com.jfinal.core.JFinal;
import com.jfinalshop.model.Admin;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台Action类 - 文件上传
 */
public class UploadController extends BaseAdminController<Admin> {

    // 图片文件上传
    public void image() {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("url", handleUpload(FileType.IMAGE));
        jsonMap.put(STATUS, SUCCESS);
        renderJson(jsonMap);
    }

    // 媒体文件上传
    public void media() {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("url", handleUpload(FileType.MEDIA));
        jsonMap.put(STATUS, SUCCESS);
        renderJson(jsonMap);
    }

    // 其它文件上传
    public void file() throws Exception {
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("url", handleUpload(FileType.OTHER));
        jsonMap.put(STATUS, SUCCESS);
        ajaxJsonErrorMessage("文件大小超出限制!");
    }
}
