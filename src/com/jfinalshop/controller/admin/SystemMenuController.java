package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.cache.J2CacheName;
import com.jfinal.plugin.cache.ClearCache;
import com.jfinalshop.interceptor.AdminInterceptor;
import com.jfinalshop.model.Menu;

import java.util.List;

/**
 * 后台类 - 系统菜单管理
 */
@Before(AdminInterceptor.class)
public class SystemMenuController extends BaseAdminController<Menu> {
    Menu dao = Menu.dao;

    // 添加
    public void add() {
        setAttr("menuTreeList", dao.getTreeList(false));
        render("/admin/system_menu_input.html");
    }

    // 编辑
    public void edit() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            setAttr("menu", dao.findById(id));
        }
        setAttr("menuTreeList", dao.getTreeList(false));
        render("/admin/system_menu_input.html");
    }

    // 列表
    public void list() {
        setAttr("menuTreeList", dao.getTreeList(true));
        render("/admin/system_menu.html");
    }

    // 保存
    @Before(ClearCache.class)
    @J2CacheName(Menu.class)
    public void save() {
        Menu menu = getModel(Menu.class);
        menu.save();
        setAttr("menuTreeList", dao.getTreeList(false));
        render("/admin/system_menu.html");
    }


    // 更新
    @Before(ClearCache.class)
    @J2CacheName(Menu.class)
    public void update() {
        Menu menu = getModel(Menu.class);
        updated(menu);
        setAttr("menuTreeList", dao.getTreeList(false));
        render("/admin/system_menu.html");
    }

    // 删除
    @Before(ClearCache.class)
    @J2CacheName(Menu.class)
    public void delete() {
        String id = getPara("id", "");
        if (StrKit.notBlank(id)) {
            Menu menu = dao.findById(id);
            dao.getTreeList(false);
            // 是否存在下级
            List<Menu> menuChildren = menu.getChildren();
            if (menuChildren != null && menuChildren.size() > 0) {
                ajaxJsonErrorMessage("此菜单存在下级菜单，删除失败!");
                return;
            }
            menu.delete();
            ajaxJsonSuccessMessage("删除成功！");
        }
    }

}
