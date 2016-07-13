/*
 * Copyright 2016 Kaijie Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.okayj.android.samples.hierarchicalviewsample.model;

import java.util.List;

import cn.okayj.android.samples.hierarchicalviewsample.entry.ChildGroup;
import cn.okayj.android.samples.hierarchicalviewsample.entry.Group;
import cn.okayj.android.samples.hierarchicalviewsample.entry.Item;

/**
 * Created by Jack on 16/7/12.
 */
public class TreeBuilder {
    public static ItemNode buildItemNode(Item item){
        ItemNode node = new ItemNode();
        node.setSource(item);
        return node;
    }

    public static ChildGroupNode buildChildGroupNode(ChildGroup childGroup){
        ChildGroupNode node = new ChildGroupNode();
        node.setSource(childGroup);
        for (Item item : childGroup.getItemList()){
            node.addChildNode(buildItemNode(item));
        }

        return node;
    }

    public static GroupNode buildGroupNode(Group group){
        GroupNode node = new GroupNode();
        node.setSource(group);
        for (ChildGroup childGroup : group.getChildGroupList()){
            node.addChildNode(buildChildGroupNode(childGroup));
        }

        return node;
    }

    public static RootNode buildRootNode(List<Group> groupList){
        RootNode node = new RootNode();
        node.setSource(groupList);
        for (Group group : groupList){
            node.addChildNode(buildGroupNode(group));
        }

        return node;
    }
}
