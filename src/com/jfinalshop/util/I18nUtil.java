package com.jfinalshop.util;

import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;

/**
 * Created by Jack.Zhou on 3/18/2016.
 */
public class I18nUtil {
    private static final String localeParaName = "_locale";

    public static String convert(String code, Controller c, Object... arguments) {
        String locale = c.getRequest().getLocale().toString();
        if (StrKit.notBlank(locale)) {    // change locale, write cookie
            c.setCookie(localeParaName, locale, Const.DEFAULT_I18N_MAX_AGE_OF_COOKIE);
        } else {                            // get locale from cookie and use the default locale if it is null
            locale = c.getCookie(localeParaName);
        }
        Res res = I18n.use("i18n", locale);
        if (res == null) {
            res = I18n.use("i18n", "zh_CN");
        }
        return res.format(code, arguments);
    }
}
