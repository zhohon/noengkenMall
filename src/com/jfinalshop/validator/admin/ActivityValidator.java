package com.jfinalshop.validator.admin;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ActivityValidator extends Validator{

	@Override
	protected void validate(Controller c) {
		c.getFile();
		validateRequiredString("activity.name", "errorMessages", "活动名称不允许为空!");
	}

	@Override
	protected void handleError(Controller c) {
		c.render("/admin/error.html");
	}

}
