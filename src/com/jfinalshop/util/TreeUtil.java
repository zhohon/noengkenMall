package com.jfinalshop.util;

import com.jfinalshop.model.TreeModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack.Zhou on 2016/1/8.
 */
public class TreeUtil<M extends TreeModel> {

    public List<M> buildTreeList(List<M> input, boolean withNodes) {
        Map<Long, M> parents = new LinkedHashMap<Long, M>();
        int depth = 0;
        List<M> result = new ArrayList<M>(input);
        M currentItem;
        while (result.size() > 0 && (currentItem = result.remove(0)) != null) {
            Long parent_id = currentItem.getLong("parent_id");
            List<M> children = parents.get(parent_id) == null ? null : parents.get(parent_id).getChildren();
            if (parent_id == 0) {
                children = children == null ? new ArrayList<M>() : children;
                currentItem.setLevel(0);
                children.add(currentItem);
                parents.put(currentItem.getId(), currentItem);
            } else if (children != null) {
                Integer parentLevel = parents.get(parent_id).getLevel();
                int currentLevel = parentLevel + 1;
                currentItem.setLevel(currentLevel);
                if (currentLevel > depth) {
                    depth = currentLevel;
                }
                parents.get(parent_id).addChild(currentItem);
                parents.put(currentItem.getId(), currentItem);
            } else if (children == null) {
                result.add(currentItem);
            }
        }
        for (M node : parents.values()) {
            if (node.getLevel() == 0) {
                result.add(node);
                if (withNodes) {
                    result.addAll(node.getChildren());
                }
            }
        }
        return result;
    }
}
