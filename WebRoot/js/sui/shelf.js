$(function () {
    'use strict';
    //封装：本js内部方法用 shelf 标识，以下方法只在shelf范围有效，
    //引用时,调用$.shelf.function
    $.shelf = {
        //拼接热销商品
        appendItem: function (data, targetObj) {
            // 生成新条目的HTML
            var html = '';
            for (var i = 0; i < data.list.length; i++) {
                var header = data.list[i].name;
                var imgArray = JSON.parse(data.list[i].img);
                var imageHtml = '<img src="' + $.mall.image(getImgPath() + imgArray[0].sourceProductImagePath, 's') + '" width="100%">';
                var url = getCtxPath() + "/weixin/packet/detail?id=" + data.list[i].id;
                var footer = '价格:' + data.list[i].price;
                html += $.mall.createCard(header, imageHtml, footer, url);
            }
            // 添加新条目
            targetObj.append(html);
        },
        addItems: function (pageSize, pageNumber) {
            //从服务端获取列表
            $.getJSON(getCtxPath() + '/weixin/shelf/getPackets?shelf_id=' + $('#shelf-id')[0].value + '&pageSize=' + pageSize + '&pageNumber=' + pageNumber, function (data) {
                console.log(data);
                $.shelf.appendItem(data, $('.list-container'));
            });
        }
    };

    //无限滚动
    $(document).on("pageInit", "#page-shelf", function (e, id, page) {
        var loading = false;
        // 每次加载添加多少条目
        var pageSize = $.config.pageSize;
        // 最多可加载的条目
        var maxItems = $.config.maxItems;
        $.shelf.addItems(pageSize, 1);
        var lastIndex = $('.list-container .card').length;
        page.on('infinite', function () {
            // 如果正在加载，则退出
            if (loading) return;
            // 设置flag
            loading = true;
            // 模拟1s的加载过程
            setTimeout(function () {
                // 重置加载flag
                loading = false;
                if (lastIndex >= maxItems) {
                    // 加载完毕，则注销无限加载事件，以防不必要的加载
                    $.detachInfiniteScroll($('.infinite-scroll'));
                    // 删除加载提示符
                    $('.infinite-scroll-preloader').remove();
                    return;
                }
                // 更新最后加载的序号
                lastIndex = $('.list-container .card').length;
                var pageNumber = Math.ceil(lastIndex / pageSize) + 1;
                $.shelf.addItems(pageSize, pageNumber);
                $.refreshScroller();
            }, $.config.loadInterval);
        });
    });

    $.init();

});
