$(function () {
    'use strict';
    //封装：本js内部方法用 cart 标识，以下方法只在cart范围有效，
    //引用时,调用$.cart.function
    $.cart = {
        deleteEnabled: true,
        editEnabled: true,
        createCartItem: function (name, value, imageHtml, price, quantity, url) {
            var cartItem = '<li>'
                + '<div class="card">'
                + '<div class="card-header"><button style="visibility:hidden"></button><button class="button" name="edit">编辑</button></div>'
                + '<div class="card-content">'
                + '<label class="label-checkbox item-content">'
                + '<input type="checkbox" name="cartItem" value="' + value + '">'
                + '<div class="item-media">'
                + '<i class="icon icon-form-checkbox">'
                + '</i>'
                + '</div>'
                + '<a href="' + url + '" external>'
                + '<div class="item-media">'
                + '<i class="icon" >'
                + imageHtml
                + '</i>'
                + '</div>'
                + '</a>'
                + '<div class="item-inner">'
                + '<div class="item-text">' + name + '</div>'
                + '<div class="item-title-row">'
                + '<div class="item-subtitle" name="unitPrice">' + price.formatMoney() + '</div>'
                + '<div class="item-after">x<label name="quantity">' + quantity + '</label></div>'
                + '</div>'
                + '</div>'
                + '</div>'
                + '</label>'
                + '</div>'
                + '</div>'
                + '</li>';
            return cartItem;
        },
        //结算标签数量计算
        checkCount: function () {
            var checkedCount = $("input[name='cartItem']:checked").length;
            var checkedCountLabel = $("#checkedCount");
            checkedCountLabel.empty();
            if (checkedCount > 0) {
                checkedCountLabel.append("(" + checkedCount + ")");
            }
            var totalPrice=0;
            $("input[name='cartItem']:checked").each(function(){
				var quantity=$(this).parent().find("label[name='quantity']").html();
				var unitPrice=$(this).parent().find("div[name='unitPrice']").html().substring(1);
				totalPrice+=quantity*unitPrice;
				totalPrice=totalPrice;
			})
			$("#totalPrice").html(totalPrice.formatMoney());
        },
        //新增商品包
        appendPacketItem: function (data, targetObj) {
            // 生成新条目的HTML
            var html = '';
            for (var i = 0; i < data.list.length; i++) {
                var name = data.list[i].name;
                var imgArray = JSON.parse(data.list[i].img);
                var imageHtml = '<img src="' + $.mall.image(getImgPath() + imgArray[i].sourceProductImagePath, 'ss') + '" >';
                var price = parseFloat(data.list[i].price);
                var quantity = parseInt(data.list[i].quantity);
                var value = data.list[i].id;
                var url = getCtxPath() + "/weixin/packet/detail?id=" + data.list[i].id;
                html += $.cart.createCartItem(name, value, imageHtml, price, quantity, url);
            }
            // 添加新条目
            targetObj.append(html);
            $("input[type='checkbox']").on("change", function (e) {
				if ($("input[name=cartItem]:checked").size()===$("input[name=cartItem]").size()) {
					$("input[name=checkAll]").prop('checked', true);
				}else{
					$("input[name=checkAll]").prop('checked', false);
				}
				$.cart.checkCount();
            });
            $("button[name='edit']").on("click", $.cart.openEdit);
        },
        //全选
        checkAll: function (page) {
            page.find("input[name='checkAll']").on("change", function (e) {
                e.preventDefault();
                if (page.find("input[name=checkAll]").is(":checked")) {
                    page.find('input[type=checkbox]').prop('checked', true);
                } else {
                    page.find('input[type=checkbox]').prop('checked', false);
                }
                $.cart.checkCount();
            });
        },
        addPacketItems: function (pageSize, lastIndex) {
            //从服务端获取列表
            $.getJSON(getCtxPath() + '/weixin/cart/listAvailable?pageSize=' + pageSize + '&lastIndex=' + lastIndex, function (data) {
                console.log(data);
				if (data.status === 'error') {
					$.alert( data.message);
				}
                $.cart.appendPacketItem(data, $('#page-cart .list-block ul'));
            });
        },
        //删除购物车中明细
        deleteCartItems: function () {
            if (!$.cart.deleteEnabled || $("input[name='cartItem']:checked").length === 0) {
                return;
            }
            var ids = [];
            $("input[name='cartItem']:checked").each(function () {
                ids.push($(this).val());
            });
            //从服务端获取列表
            $.getJSON(getCtxPath() + '/weixin/cart/delete?ids=' + ids, function (data) {
                $.cart.deleteEnabled = true;
                console.log(data);
				if (data.status === 'error') {
					$.alert( data.message);
				}
                $(JSON.parse(data.message)).each(function () {
                    $("input[value='" + this + "']").parent().parent().parent().remove();
                });
                $("label[name='cartItemCount']").each(function () {
                	if(data.cartItemCount==0){
                		$(this).empty();
                		$.router.load('#page-cart-empty');
                	}else{
                    	$(this).empty().append("(" + data.cartItemCount + ")");
                    }
                });
                $.cart.checkCount();
            });
        },
        //打开修改购物车中商品数量
        openEdit: function (e) {
            if (!$.cart.editEnabled) {
                return;
            }
            $.cart.editEnabled = false;
            var label = $(e.target).parent().parent().find("label[name='quantity']");
            var length=label.html.length;
            if(length<5){//5位数
            	length=5;
            }
            var quantity = parseInt(label.html());
            var values=[],col = [];
            for(var i=0;i<length;i++){
            	values.push(parseInt(quantity%10));
            	quantity=quantity/10;
            }
            var displayQuantity=values.reverse().toString().replace(/,/gm,' ');
            var start = 0;
            for (var i = start; i <= 10; i++) {
                col.push(i);
            }
            label = $(e.target).parent().parent().find(".item-title-row");
            $(e.target).parent().parent().find(".item-inner").append(
                "<div class='item-title-row' id='editQuantity'><input type='hidden' placeholder='选择数量' value='" + displayQuantity + "' id='picker'/></div>");
            $("#picker").picker({
                toolbarTemplate: '<header class="bar bar-nav">\
              <button class="button button-link pull-left">按钮</button>\
              <button class="button button-link pull-right close-picker">确定</button>\
              <h1 class="title">数量</h1>\
              </header>',
                cols: [
                	{
						textAlign: 'center',
						values: col
					},
					{
						textAlign: 'center',
						values: col
					},
					{
						textAlign: 'center',
						values: col
					},
					{
						textAlign: 'center',
						values: col
					},
					{
						textAlign: 'center',
						values: col
					}
                ],
                onClose: $.cart.editCartItem
            });
            $("#picker").picker("open");
        },
        //修改购物车中商品数量
        editCartItem: function (picker) {
            var edit=$('#editQuantity');
            var quantity = parseInt(picker.value.toString().replace(/,/gm,''));
            if(quantity===0){
            	quantity=1;
            }
			var packetId=edit.parent().parent().find("input[name='cartItem']").val();
			$.cart.editRemoteItem(packetId,quantity);
			var label = edit.parent().find("label[name='quantity']");
			label.empty().append(quantity);
            $.cart.editEnabled = true;
            edit.remove();
        },
        //修改购物车中商品数量
        editRemoteItem: function (packetId,quantity) {
            //更新服务器购物车
			$.getJSON(getCtxPath() + '/weixin/cart/edit?id=' + packetId+'&quantity='+quantity, function (data) {
				if (data.status === 'error') {
					$.alert( data.message);
				}else{
					$.toast( data.message, 2000);
				}
			});
			$.cart.checkCount();
        },
        //创建订单
		createOrder: function () {
			var checkedBox=$("input[name='cartItem']:checked");
			if(checkedBox.length===0){
				$.alert("您没有选中商品", '请选择商品!');
				return;
			}
			var postData=[];
			checkedBox.each(function () {
				var packetId=$(this).val();
				var quantity=$(this).parent().find("label[name='quantity']").html();
				postData.push({"packetId":packetId,"quantity":quantity});
			});
			$.ajax({
				type: 'POST',
				async:false,
				url: getCtxPath() + '/weixin/cart/postOrder',
				data: {packets:JSON.stringify(postData)},
				success: function(data){
					if (data.status === 'error') {
						$.alert(data.message);
					}else{
						$.toast( data.message, 2000);
						window.location.href = getCtxPath() + '/weixin/order/index?cartIds='+data.cartIds;
					}
				},
				error: function(xhr, type){
					$.alert('创建订单失败!');
				}
            })
		}
    }

    //无限滚动，购物车加载
    $(document).on("pageInit", "#page-cart", function (e, id, page) {
        var loading = false;
        // 每次加载添加多少条目
        var pageSize = $.config.pageSize;
        // 最多可加载的条目
        var maxItems = $.config.maxItems;
        var lastIndex = page.find("input[name='cartItem']").length;
        $.cart.addPacketItems(pageSize, lastIndex);
        page.on("infinite", function () {
            // 如果正在加载，则退出
            if (loading)
                return;
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
                    page.find('.infinite-scroll-preloader').remove();
                    return;
                }
                var currentId = $('.tab.active')[0].id;
                // 更新最后加载的序号
                lastIndex = page.find('input[name="cartItem"]').length;
                if(lastIndex==0){
					$(this).empty();
					$.router.load('#page-cart-empty');
				}
                $.cart.addPacketItems(pageSize, lastIndex);
                $.refreshScroller();
            }, $.config.loadInterval);
        });
        page.find("#nav-page-delete").on("click", $.cart.deleteCartItems);
        page.find("#nav-page-order").on("click", $.cart.createOrder);
        $.cart.checkAll(page);
        page.find("input[name='checkAll']").prop('checked', false);
    });
    $.init();
});
