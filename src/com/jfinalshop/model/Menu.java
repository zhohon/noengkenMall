package com.jfinalshop.model;

import com.jfinal.aop.Duang;
import com.jfinalshop.util.TreeUtil;

import java.util.List;


public class Menu extends TreeModel<Menu> {

    private static final long serialVersionUID = -5360005768283462038L;

    public static final Menu dao = Duang.duang(new Menu());

    public List<Menu> getTreeList(boolean withNodes) {
        List<Menu> allMenu = dao.findAll();
        return new TreeUtil().buildTreeList(allMenu,withNodes);
    }


}
