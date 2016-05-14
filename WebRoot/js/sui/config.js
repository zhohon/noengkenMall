/**
 * 存放对 msui 的 config，需在 zepto 之后 msui 之前加载
 *
 * Created by bd on 15/12/21.
 */
//参数配置区域
$.config = {
    pageSize: 10,
    maxItems: 100,
    loadInterval: 1000
};

//公共方法区域
$.mall = {
    //创建单个卡片
    createCard: function (header, imageHtml, footer, url) {
        return '<div class="card">' +
            '<div class="card-header">' + header + '</div>' +
            '<div class="card-content">' +
            '<a href="' + url + '" external>' +
            '<div class="card-content-inner">' + imageHtml + '</div>' +
            '</a>' +
            '</div>' +
            '<div class="card-footer">' + footer + '</div>' +
            '</div>';
    },
    addCart: function (e, id, page) {
        page.find("#nav-page-addCart").on("click", function (e) {
            //从服务端获取列表
            $.getJSON(getCtxPath() + '/weixin/cart/add?packetId=' + $('#packet-id').val() + '&quantity=' + 1, function (data) {
                console.log(data);
				if (data.status === 'error') {
					$.alert( data.message);
				}else{
					$.toast( data.message, 2000);
                    $("#nav-page-cart label").empty().append("(" + data.cartItemCount + ")");
                    $("label[name='cartItemCount']").empty().append("(" + data.cartItemCount + ")");
                }
            });
        });
    },
    image: function (url, type) {
        var qiniuUrl = getImgPath();
        if (url.match(qiniuUrl)) {
            return url + '-' + type;
        }
    }
};

//默认执行区域
$(function () {
    'use strict';
    $(document).on("pageInit", function () {
        if ($('.page.page-current').length != 0) {
            var currentId = $('.page.page-current')[0].id;
            var navId = '.page.page-current #nav-' + currentId;
            $(navId).addClass('active');
        }
    });
    // Extend the default Number object with a formatMoney() method:
    // usage: someVar.formatMoney(decimalPlaces, symbol, thousandsSeparator, decimalSeparator)
    // defaults: (2, "￥", ",", ".")
    Number.prototype.formatMoney = function (places, symbol, thousand, decimal) {
        places = !isNaN(places = Math.abs(places)) ? places : 2;
        symbol = symbol !== undefined ? symbol : "￥";
        thousand = thousand || "";
        decimal = decimal || ".";
        var number = this,
            negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
        return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
    };
});

//验证
$.validate = {
    require: function (input, errMsg) {
		$(input).on("input", function () {
			var value = $.trim(this.value);
			$(this).parent().find("span").remove();
			var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
			if (value == "") {
				tipCon.html(errMsg);
				$(this).focus();
			} else {
				tipCon.remove();
			}
		});
    },
    mobile: function (input, errMsg) {
        $(input).on("input", function () {
        	$(this).parent().find("span").remove();
			var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
            var value = $.trim(this.value);
            if (!/^(0|86|17951)?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[0-9])[0-9]{8}$/.test(value)) {
                tipCon.html(errMsg);
                $(this).focus();
            } else {
                tipCon.remove();
            }
        });
    },
    phone: function (input, errMsg) {
		$(input).on("input", function () {
			$(this).parent().find("span").remove();
			var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
			var value = $.trim(this.value);
			if (!/^0\d{2,3}-?\d{7,8}$/.test(value)) {
				tipCon.html(errMsg);
				$(this).focus();
			} else {
				tipCon.remove();
			}
		});
	},
    email: function (input, errMsg) {
		$(input).on("input", function () {
        	$(this).parent().find("span").remove();
			var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
            var value = $.trim(this.value);
             if (!/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(value)) {
                tipCon.html(errMsg);
                $(this).focus();
            } else {
                tipCon.remove();
            }
        });
    },
    zipCode: function (input, errMsg) {
		$(input).on("input", function () {
			$(this).parent().find("span").remove();
			var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
			var value = $.trim(this.value);
			 if (!/^[1-9][0-9]{5}$/.test(value)) {
				tipCon.html(errMsg);
				$(this).focus();
			} else {
				tipCon.remove();
			}
		});
	},
    compare: function (input1, input2, errMsg) {
        input1 = $(input1);
        input2 = $(input2);
        input2.on("input", function () {
        	$(this).parent().find("span").remove();
            var tipCon = $(this).after("<span class='invalid'></span>").parent().find("span");
            var value1 = $.trim(input1[0].value);
            var value2 = $.trim(this.value);
            if (value1 !== value2) {
                tipCon.html(errMsg);
            } else {
                tipCon.remove();
            }
        });
    }
};
