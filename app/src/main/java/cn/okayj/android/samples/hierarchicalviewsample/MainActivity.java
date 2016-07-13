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

package cn.okayj.android.samples.hierarchicalviewsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import cn.okayj.android.samples.hierarchicalviewsample.entry.Group;
import cn.okayj.android.samples.hierarchicalviewsample.model.ChildGroupNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.GroupNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.ItemNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.RootNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.TreeBuilder;
import cn.okayj.android.samples.hierarchicalviewsample.ui.DividerItemDecoration;
import cn.okayj.util.DataNode;
import cn.okayj.util.NodeFlatIndex;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";

    List<Group> groupList;
    RootNode rootNode;
    NodeFlatIndex.VisibleFlatIndex index;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,R.drawable.divider));
        recyclerView.setAdapter(new Adapter());
    }

    private void init(){
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(getAssets().open("data.js"));
            br = new BufferedReader(isr);
            groupList = new Gson().fromJson(br,new TypeToken<List<Group>>(){}.getType());
        }catch (JsonIOException e) {
            Log.d(LOG_TAG,e.getMessage());
        }catch (Exception e){
            Log.d(LOG_TAG,e.getMessage());
        }finally {
            if(br != null){
                try {
                    br.close();
                }catch (Exception e){

                }
            }
        }

        if(groupList != null){
            rootNode = TreeBuilder.buildRootNode(groupList);
            NodeFlatIndex nodeFlatIndex = rootNode.getFlatIndex();
            nodeFlatIndex.ignoreRoot(true);
            index = nodeFlatIndex.getVisibleIndex();
        }
    }

    private class Adapter extends RecyclerView.Adapter<BaseViewHolder> {
        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
            switch (viewType){
                case ItemViewHolder.LAYOUT:
                    return new ItemViewHolder(view);
                case ChildGroupViewHolder.LAYOUT:
                    return new ChildGroupViewHolder(view);
                case GroupViewHolder.LAYOUT:
                    return new GroupViewHolder(view);
                default:
                    throw new IllegalStateException("unchecked view type");
            }
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.setData(index.get(position));
        }

        @Override
        public int getItemCount() {
            return index == null ? 0 : index.size();
        }

        @Override
        public int getItemViewType(int position) {
            DataNode node = index.get(position);
            if(node instanceof ItemNode){
                return ItemViewHolder.LAYOUT;
            }else if(node instanceof ChildGroupNode){
                return ChildGroupViewHolder.LAYOUT;
            }else if(node instanceof GroupNode){
                return GroupViewHolder.LAYOUT;
            }else {
                throw new IllegalStateException("unchecked data type");
            }
        }
    }

    private abstract class BaseViewHolder<D> extends RecyclerView.ViewHolder {
        private D data;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public BaseViewHolder setData(Object d){
            data = (D)d;
            onDataSet(data);
            return this;
        }

        public D getData(){
            return data;
        }

        protected abstract void onDataSet(D d);

    }

    private class GroupViewHolder extends BaseViewHolder<GroupNode>{
        public static final int LAYOUT = R.layout.item_group;

        private TextView mTextView;

        public GroupViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataNode node = getData();
                    node.setIsFolded(!node.isFold());
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onDataSet(GroupNode node) {
            mTextView.setText(node.getSource().getContent());
        }
    }

    private class ChildGroupViewHolder extends BaseViewHolder<ChildGroupNode>{
        public static final int LAYOUT = R.layout.item_child_group;


        private TextView mTextView;

        public ChildGroupViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataNode node = getData();
                    node.setIsFolded(!node.isFold());
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        protected void onDataSet(ChildGroupNode childGroupNode) {
            mTextView.setText(childGroupNode.getSource().getContent());
        }
    }

    private class ItemViewHolder extends BaseViewHolder<ItemNode>{
        public static final int LAYOUT = R.layout.item_item;

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.content);
        }

        @Override
        protected void onDataSet(ItemNode itemNode) {
            mTextView.setText(itemNode.getSource().getContent());
        }
    }
}
