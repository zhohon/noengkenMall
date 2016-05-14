package com.jfinalshop.service;

import com.jfinal.plugin.cache.J2CacheName;
import com.jfinalshop.model.Menu;

import java.util.List;


public class MenuService {

    /**
     * 树形菜单
     *
     * @return List<Menu>
     */
    @J2CacheName(Menu.class)
    public List<Menu> getMenuTree() {
        return Menu.dao.getTreeList(false);
    }
}
