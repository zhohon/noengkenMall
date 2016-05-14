$(function () {
    'use strict';
    //封装：本js内部方法用 packet 标识，以下方法只在packet范围有效，
    //引用时,调用$.packet.function
    $.packet = {
        //加载商品详情
        loadProductDetail: function () {
            var packet_id = $("#packet-id").val();
            $.getJSON(getCtxPath() + '/weixin/packet/getPacketImageText?id=' + packet_id, function (data) {
				if (data.status === 'error') {
					$.alert( data.message);
				}
                $.packet.appendProductDetail(data, $('#tabProductDetail .content-block'));
            });
        },
        //将商品详情拼接到对应位置
        appendProductDetail: function (data, targetObj) {
            var html = data['imageText'];
            targetObj.empty().append(html);
        },
        //加载商品参数
        loadProductParams: function () {
            var packet_id = $("#packet-id").val();
            $.getJSON(getCtxPath() + '/weixin/packet/getProductParam?id=' + packet_id, function (data) {
                //console.log('loadProductParams');
                //console.log(data);
				if (data.status === 'error') {
					$.alert( data.message);
				}
                $.packet.appendProductParam(data, $('#tabProductParam .content-block'));
            });
        },
        //拼接商品参数
        appendProductParam: function (data, targetObj) {
            var html = '';
            for (var i = 0; i < data.length; i++) {
                var productName = data[i].productName;
                html += '<div class="content-block-title"><h3>' + productName + '</h3></div>';
                html += '<div class="list-block"><ul>';
                for (var j = 0; j < data[i].paramList.length; j++) {
                    var param = data[i].paramList[j].param;
                    var value = data[i].paramList[j].value;
                    html += ' <li class="item-content"><div class="item-inner"> <div class="item-title">' + param
                        + '</div><div class="item-after">' + value + '</div></div></li>';
                }
                html += '</ul></div>';
            }
            targetObj.empty().append(html);
        },
        //加载幻灯片
        loadSwiper: function () {
            $.getJSON(getCtxPath() + '/weixin/packet/getPacketImageText?id=' + $("#packet-id").val(), function (data) {
				if (data.status === 'error') {
					$.alert( data.message);
				}
                $.packet.appendSwiperImage(data, $('#page-packet-main .swiper-wrapper'));
            });
        },
        //拼接幻灯片
        appendSwiperImage: function (data, targetObj) {
            var html = '';
            var imgArray = JSON.parse(data.img);
            for (var i = 0; i < imgArray.length; i++) {
                html += ' <div class="swiper-slide photo-browser-slide"  width="100%">';
                html += '<img src="' + $.mall.image(getImgPath() + imgArray[i].sourceProductImagePath, 'o') + '" alt="">';
                html += '</div>';
            }
            targetObj.empty().append(html);
            $(".swiper-container").swiper({
                speed: 400,
                spaceBetween: 10,
                autoplay: 5000
            });
        }
    }

    //无限滚动，切换到详细页面
    $(document).on("pageInit", "#page-packet-main", function (e, id, page) {
        $.mall.addCart(e, id, page);
        $.packet.loadSwiper();
        var loading = false;
        page.on('infinite', function () {
            // 如果正在加载，则退出
            if (loading)
                return;
            // 设置flag
            loading = true;
            // 模拟1s的加载过程
            setTimeout(function () {
                // 重置加载flag
                loading = false;
                $.popup("#page-packet-popup");
            }, $.config.loadInterval);
        });
        $.packet.loadProductDetail();
        $.packet.loadProductParams();
    });

    $.init();
});
