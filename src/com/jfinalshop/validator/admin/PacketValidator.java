package com.jfinalshop.validator.admin;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class PacketValidator extends Validator{

	@Override
	protected void validate(Controller c) {
        validateRequiredString("packet.products", "errorMessages", "商品不允许为空!");
        validateRequiredString("packet.shelf_id", "errorMessages", "货架不能为空!");
	}

	@Override
	protected void handleError(Controller c) {
		c.render("/admin/error.html");
	}

}
