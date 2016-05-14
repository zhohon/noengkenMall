$(function () {
    'use strict';
    //封装：本js内部方法用 receiver 标识，以下方法只在receiver范围有效，
    //引用时,调用$.receiver.function
    $.receiver = {
        //跳转到新增收货人页面
        addReceiver: function () {
        	if($("span.invalid").length>0){
        		$.alert('请检查数据', function () {
					$($("span.invalid")[0]).parent().find("input").focus();
				});
				return;
        	}
            var url = getCtxPath() + '/weixin/receiver/save?';
            $("input").each(function () {
                url += encodeURIComponent(this.name) + "=" + encodeURIComponent(this.value) + "&";
            });
            window.location.href = url;
        },
        //修改check状态
		changeDefault: function (e) {
			var checkBox=$(e.target);
			if (checkBox.is(":checked")) {
				checkBox.val(true);
			}else{
				checkBox.val(false);
			}
		},
		validForm:function () {
			$.validate.require($("input[name='receiver.address']"), "地址必填");
			$.validate.require($("input[name='receiver.name']"), "姓名必填");
			$.validate.mobile($("input[name='receiver.mobile']"), "手机格式不正确");
			$.validate.phone($("input[name='receiver.phone']"), "固定电话格式不正确");
			$.validate.zipCode($("input[name='receiver.zipCode']"), "邮编格式六位");
		}
    }

    $(document).on("pageInit", "#page-receiver-input", function (e, id, page) {
        page.find("#city-picker").cityPicker({
            toolbarTemplate: '<header class="bar bar-nav">\
			<button class="button button-link pull-right close-picker">确定</button>\
			<h1 class="title">选择收货地址</h1>\
			</header>'
        });
        page.find("#receiver-add").on("click", $.receiver.addReceiver);
        page.find("#checkIsDefault").on("change", $.receiver.changeDefault);
        $.receiver.validForm();
    });
    $.init();
});
