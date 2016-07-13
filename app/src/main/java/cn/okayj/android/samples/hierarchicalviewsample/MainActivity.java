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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cn.okayj.android.samples.hierarchicalviewsample.entry.Cart;
import cn.okayj.android.samples.hierarchicalviewsample.entry.Goods;
import cn.okayj.android.samples.hierarchicalviewsample.model.CartNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.GoodsFooterNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.GoodsNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.ShopNode;
import cn.okayj.android.samples.hierarchicalviewsample.model.TreeBuilder;
import cn.okayj.android.samples.hierarchicalviewsample.ui.DividerItemDecoration;
import cn.okayj.util.DataNode;
import cn.okayj.util.NodeFlatIndex;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    Cart cart;//购物车数据(树形)

    CartNode cartNode;//购物车根节点

    NodeFlatIndex.VisibleFlatIndex index;//购物车的可见索引


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,R.drawable.divider));
        recyclerView.setAdapter(new Adapter());
    }

    /**
     * 初始化,将json数据转成对象
     */
    private void init(){
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(getAssets().open("shop_cart.js"));
            br = new BufferedReader(isr);
            cart = new Gson().fromJson(br,Cart.class);
        }catch (JsonIOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                }catch (Exception e){

                }
            }
        }

        if(cart != null){
            cartNode = TreeBuilder.buildCartNode(cart);
            NodeFlatIndex nodeFlatIndex = cartNode.getFlatIndex();
            nodeFlatIndex.ignoreRoot(true);
            index = nodeFlatIndex.getVisibleIndex();
        }
    }

    /**
     * 列表item 的 base view holder
     * @param <S>
     */
    private abstract class BaseViewHolder<S> extends RecyclerView.ViewHolder{
        private S s;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 设置 holder 数据
         * @param s
         * @return
         */
        public BaseViewHolder setData(DataNode s){
            this.s = (S)s;
            onDataSet((S)s);
            return this;
        }

        /**
         * 后去holder 数据
         * @return
         */
        public S getData(){
            return s;
        }

        /**
         * 子类在该方法中更新视图
         * @param s
         */
        protected abstract void onDataSet(S s);
    }

    /**
     * 店铺
     */
    private class ShopViewHolder extends BaseViewHolder<ShopNode> {
        public static final int LAYOUT = R.layout.item_shop;

        public TextView mTitleTextView;

        public ImageView mArrowView;

        public ShopViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mArrowView = (ImageView) itemView.findViewById(R.id.arrow);
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
        public void onDataSet(ShopNode shopNode) {
            mTitleTextView.setText(shopNode.getSource().getName());
            if(shopNode.isFold()){
                mArrowView.getDrawable().setLevel(0);
            }else {
                mArrowView.getDrawable().setLevel(1);
            }
        }
    }

    /**
     * 商品
     */
    private class GoodViewHolder extends BaseViewHolder<GoodsNode>{
        public static final int LAYOUT = R.layout.item_goods;

        public TextView mTitleTextView;

        public TextView mPriceView;

        public TextView mNumberView;

        public Button mButton;

        public View mFooterView;

        public GoodsFooterViewHolder mFooterViewHolder;

        public GoodViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mPriceView = (TextView) itemView.findViewById(R.id.price);
            mNumberView = (TextView) itemView.findViewById(R.id.number);
            mButton = (Button) itemView.findViewById(R.id.button);
            mFooterView = itemView.findViewById(R.id.footer);
            mFooterViewHolder = new GoodsFooterViewHolder(mFooterView);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataNode node = getData();
                    node.setIsFolded(!node.isFold());
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onDataSet(GoodsNode goodsNode) {
            mTitleTextView.setText(goodsNode.getSource().getName());
            mPriceView.setText(goodsNode.getSource().getPrice()+"元");
            mNumberView.setText(goodsNode.getSource().getNumber()+"");

            if(goodsNode.getParentNode() instanceof GoodsNode){
                itemView.getBackground().setLevel(1);
            }else {
                itemView.getBackground().setLevel(0);
            }
            if(goodsNode.isFold()){
                mButton.setText("展开/expand");
            }else {
                mButton.setText("收起/collapse");
            }
            if(goodsNode.getSource().getGoods().size() > 0){
                mButton.setVisibility(View.VISIBLE);
            }else {
                mButton.setVisibility(View.GONE);
            }

            mFooterViewHolder.setData(goodsNode);
            if(goodsNode.getSource().getGoods().size() > 0 && goodsNode.isFold()){
                mFooterViewHolder.itemView.setVisibility(View.VISIBLE);
            }else {
                mFooterViewHolder.itemView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 组合商品的footer
     */
    private class GoodsFooterViewHolder extends BaseViewHolder<DataNode>{
        public static final int LAYOUT = R.layout.item_goods_footer;

        private TextView mNumberView;

        public GoodsFooterViewHolder(View itemView) {
            super(itemView);
            mNumberView = (TextView) itemView.findViewById(R.id.number);
        }

        @Override
        protected void onDataSet(DataNode node) {
            mNumberView.setText(((Goods)node.getSource()).getGoods().size()+"");
        }
    }

    private class Adapter extends RecyclerView.Adapter<BaseViewHolder>{
        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
            switch (viewType){
                case GoodViewHolder.LAYOUT:
                    return new GoodViewHolder(view);
                case ShopViewHolder.LAYOUT:
                    return new ShopViewHolder(view);
                case GoodsFooterViewHolder.LAYOUT:
                    return new GoodsFooterViewHolder(view);
                default:
                    throw new IllegalStateException();
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
            /*
            直接将res id 作为 view type
             */
            DataNode node = index.get(position);
            if(node instanceof ShopNode){
                return ShopViewHolder.LAYOUT;
            }else if(node instanceof GoodsNode){
                return GoodViewHolder.LAYOUT;
            }else if(node instanceof GoodsFooterNode){
                return GoodsFooterViewHolder.LAYOUT;
            }else {
                throw new IllegalStateException();
            }
        }
    }
}
