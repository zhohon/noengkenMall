$(function () {
    'use strict';
    //封装：本js内部方法用 receiver 标识，以下方法只在receiver范围有效，
    //引用时,调用$.receiver.function
    $.receiver = {
        //展示编辑功能
        showEdit: function () {
            $(".receiver-box").each(function () {
                $(this).show().on("change",function(){
                	var url = getCtxPath() + "/weixin/receiver/editReceiver?id=" + encodeURIComponent(this.id);
					window.location.href = url;
                });
                $(this).find("input").prop('checked', false);
            });
        },
        //展示编辑功能
		create: function () {
			var url = getCtxPath() + "/weixin/receiver/newReceiver";
			window.location.href = url;
		}
    }

    $(document).on("pageInit", "#page-receiver", function (e, id, page) {
		page.find("#receiver-edit").on("click", $.receiver.showEdit);
		page.find("#receiver-create").on("click", $.receiver.create);
	});
    $.init();
});
