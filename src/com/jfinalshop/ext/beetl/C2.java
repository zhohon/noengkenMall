package com.jfinalshop.ext.beetl;

import com.jfinalshop.model.DisplayMapping;
import org.beetl.core.Context;
import org.beetl.core.Function;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * key convert to dictionary value
 */
public class C2 implements Function {

    public Object call(Object[] obj, Context context) {
        String result = "";
        if (obj == null || obj.length == 0) {
            return result;
        }
        try {
            String[] temp = obj[0].toString().split("\\.");
            String model = temp[0];
            String field = temp[1];
            DisplayMapping display = DisplayMapping.dao.findDisplay(model.toLowerCase(), field.toLowerCase());
            result = display.get("display");
        } catch (Exception e) {
            result = obj[0] == null ? "" : obj[0].toString();
        }
        return result;
    }

}
