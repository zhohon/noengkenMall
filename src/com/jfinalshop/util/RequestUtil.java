package com.jfinalshop.util;

import javax.servlet.http.HttpServletRequest;
import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * Created by Jack.Zhou on 1/19/2016.
 */
public class RequestUtil {

    public static String getBaseUrl(HttpServletRequest req) {
        StringBuffer url = new StringBuffer();
        String scheme = req.getScheme();
        int port = req.getServerPort();
        String contextPath = req.getContextPath();

        //String		servletPath = req.getServletPath ();
        //String		pathInfo = req.getPathInfo ();

        url.append(scheme);        // http, https
        url.append("://");
        url.append(req.getServerName());
        if ((scheme.equals("http") && !(port == 80 || port == 8000))
                || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(req.getServerPort());
        }
        //if (servletPath != null)
        //    url.append (servletPath);
        //if (pathInfo != null)
        //    url.append (pathInfo);
        url.append(contextPath);

        return url.toString();
    }

    public static String getFullUrl(HttpServletRequest req, String url) {
        if (url == null) {
            return getBaseUrl(req);
        }
        return getBaseUrl(req) + (url.startsWith("/") ? url : "/" + url);
    }

    public static String getRemoteHostIp(HttpServletRequest request) {

        String type = "X-Real-Ip";
        String ip = request.getHeader(type);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            type = "X-Forwarded-For";
            ip = request.getHeader(type);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            type = "Proxy-Client-IP";
            ip = request.getHeader(type);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            type = "WL-Proxy-Client-IP-Client-IP";
            ip = request.getHeader(type);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            type = "origin";
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : type + ":" + ip;
    }

}
