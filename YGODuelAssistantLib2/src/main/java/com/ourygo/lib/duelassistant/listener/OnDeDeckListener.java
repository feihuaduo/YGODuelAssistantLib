package com.ourygo.lib.duelassistant.listener;

import android.net.Uri;

import java.util.List;

/**
 * Create By feihua  On 2022/11/19
 * 解析卡组监听
 */
public interface OnDeDeckListener {
    void onDeDeck(Uri uri, List<Integer> mainList, List<Integer> exList, List<Integer> sideList, boolean isCompleteDeck, String exception);
}
