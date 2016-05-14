package com.jfinalshop.model;


import cn.dreampie.web.model.Model;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jack.Zhou on 2016/1/8.
 */
public abstract class TreeModel<M extends Model> extends Model<M> {
    private int level;
    private long id;
    private long parentId;
    private List<M> children=new ArrayList<M>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getId() {
        return this.getLong("id");
    }

    public long getParentId() {
        return this.getLong("parent_id");
    }

    public List<M> getChildren() {
        return children;
    }

    public void setChildren(List<M> list) {
        this.children = list;
    }

    public void addChild(M child) {
        this.children.add(child);
    }

}
