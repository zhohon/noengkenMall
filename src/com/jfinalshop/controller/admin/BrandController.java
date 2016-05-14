package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.validator.admin.BrandValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 后台类 - 品牌
 */
public class BrandController extends BaseAdminController<Brand> {

    private Brand brand;

    // 列表
    public void list() {
        findByPage();
        render("/admin/brand_list.html");
    }

    // 添加
    public void add() {
        render("/admin/brand_input.html");
    }

    // 编辑
    public void edit() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            setAttr("brand", Brand.dao.findById(id));
        }
        render("/admin/brand_input.html");
    }

    // 保存
    @Before(BrandValidator.class)
    public void save() {
        brand = getModel(Brand.class);
        String imgUrl = handleUpload(FileType.IMAGE);
        if (imgUrl != null) {
            brand.set("img", imgUrl);
        }
        saved(brand);
        redirect("/brand/list");
    }

    // 更新
    @Before(BrandValidator.class)
    public void update() {
        brand = getModel(Brand.class);
        String logo = handleUpload(FileType.IMAGE);
        if (StringUtils.isNotEmpty(logo)) {
            brand.set("logo", logo);
        }
        updated(brand);
        redirect("/brand/list");
    }

    // 删除
    public void delete() {
        String[] ids = getParaValues("ids");
        if (ids != null && ids.length > 0) {
            for (String id : ids) {
                // 充值记录
                List<Product> productList = Brand.dao.findById(id).getProductList();
                if (productList != null && productList.size() > 0) {
                    ajaxJsonErrorMessage("品牌在产品列表中存在，删除失败！");
                    return;
                }
                if (Brand.dao.delete(id)) {
                    ajaxJsonSuccessMessage("删除成功！");
                } else {
                    ajaxJsonErrorMessage("删除失败！");
                }
            }
        } else {
            ajaxJsonErrorMessage("id为空未选中，删除失败！");
        }
    }
}
