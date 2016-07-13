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

package cn.okayj.android.samples.hierarchicalviewsample.entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 16/7/13.
 */
public class Group {
    private String content;
    private List<ChildGroup> childGroupList = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChildGroup> getChildGroupList() {
        return childGroupList;
    }

    public void setChildGroupList(List<ChildGroup> childGroupList) {
        this.childGroupList = childGroupList;
    }
}
