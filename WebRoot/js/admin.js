//公共方法区域
$.admin = {
	resizeFrame:function() {
		var main = $(window.parent.document).find("#mainFrame");
		var newHeight = $(document).height()+200;
		main.height(newHeight);
	}
}