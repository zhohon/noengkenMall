package com.jfinalshop.controller.weixin;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.model.Product;


/**
 * Created by dextrys on 2016/1/8.
 */
@ControllerBind(controllerKey = "/weixin/product")
public class ProductController extends BaseWeixinController<Product> {

    @Override
    protected Page<Product> findAllAvailable(Integer pageNumber, Integer pageSize) {
        return Product.dao.getHotProductList(Product.MAX_HOT_PRODUCT_LIST_COUNT, pageNumber, pageSize);
    }

    public void detail() {
        Product product = Product.dao.findById(getPara("id"));
        setAttr("product", product);
        render("/mall/product/index.html");
    }

}
