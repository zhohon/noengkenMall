package com.jfinalshop.ext.beetl;

import com.jfinalshop.util.SystemConfigUtil;
import org.beetl.core.Format;

import java.text.DecimalFormat;

public class OrderUnitCurrencyFormat implements Format {

	/**
	 * 获取订单价格货币格式字符串（包含货币单位）
	 * 
	 */
	@Override
	public Object format(Object data, String pattern) {
		if (data == null) {
			return null;
		}
		DecimalFormat df = null;
		pattern = SystemConfigUtil.getOrderUnitCurrencyFormat();
		if (pattern == null) {
			df = new DecimalFormat();
		} else {
			df = new DecimalFormat(pattern);
		}
		return df.format(data);
	}

}
