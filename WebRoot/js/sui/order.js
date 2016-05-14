$(function () {
    'use strict';
    //封装：本js内部方法用 order 标识，以下方法只在order范围有效，
    //引用时,调用$.order.function
    $.order = {
    	changeDelivery:function(){
			var deliveryFee=$('option').not(function(){ return !this.selected }).val();
			var totalPrice=parseFloat(deliveryFee)+parseFloat($("#productsPrice").html());
			$("#deliveryFee").html(deliveryFee);
			$("#totalPrice").html(totalPrice.formatMoney());
    	}
    }

    $(document).on("pageInit", "#page-order", function (e, id, page) {
    	page.find("#deliveryType").on("change",$.order.changeDelivery);
    });
    $.init();
});
