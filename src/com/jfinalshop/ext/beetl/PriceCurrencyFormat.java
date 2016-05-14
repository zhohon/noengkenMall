package com.jfinalshop.ext.beetl;

import com.jfinalshop.util.SystemConfigUtil;
import org.beetl.core.Format;

import java.text.DecimalFormat;

public class PriceCurrencyFormat implements Format {

	/**
	 * 获取商品价格货币格式字符串
	 * 
	 */
	@Override
	public Object format(Object data, String pattern) {
		if (data == null) {
			return null;
		}
		DecimalFormat df = null;
		pattern = SystemConfigUtil.getPriceCurrencyFormat();
		if (pattern == null) {
			df = new DecimalFormat();
		} else {
			df = new DecimalFormat(pattern);
		}
		return df.format(data);
	}

}
