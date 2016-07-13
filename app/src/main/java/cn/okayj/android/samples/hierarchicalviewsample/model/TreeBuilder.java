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

import cn.okayj.android.samples.hierarchicalviewsample.entry.Cart;
import cn.okayj.android.samples.hierarchicalviewsample.entry.Goods;
import cn.okayj.android.samples.hierarchicalviewsample.entry.Shop;

/**
 * Created by Jack on 16/7/12.
 */
public class TreeBuilder {
    public static GoodsNode buildGoodsNode(Goods goods){
        GoodsNode node = new GoodsNode();
        node.setSource(goods);
        for (Goods child : goods.getGoods()){
            node.addChildNode(buildGoodsNode(child));
        }
        if(goods.getGoods().size() > 0){
            node.addFooterNode(buildGoodsFooterNode(goods));
        }

        return node;
    }

    public static ShopNode buildShopNode(Shop shop){
        ShopNode node = new ShopNode();
        node.setSource(shop);
        for (Goods goods : shop.getGoods()){
            node.addChildNode(buildGoodsNode(goods));
        }

        return node;
    }

    public static GoodsFooterNode buildGoodsFooterNode(Goods goods){
        GoodsFooterNode node = new GoodsFooterNode();
        node.setSource(goods);
        return node;
    }

    public static CartNode buildCartNode(Cart cart){
        CartNode node = new CartNode();
        node.setSource(cart);
        for (Shop shop : cart.getShopList()){
            node.addChildNode(buildShopNode(shop));
        }

        return node;
    }
}
