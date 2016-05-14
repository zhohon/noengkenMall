package com.jfinalshop.ext.beetl;

import com.jfinalshop.model.Area;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * area path convert to string
 */
public class AreaPath implements Function {

    public Object call(Object[] obj, Context context) {
        String result = "";
        if (obj == null || obj.length == 0) {
            return result;
        }
        try {
            String areaPath = obj[0].toString();
            result = Area.dao.getAreaString(areaPath);
        } catch (Exception e) {
            result = obj[0] == null ? "" : obj[0].toString();
        }
        return result;
    }

}
