package com.ourygo.lib.duelassistant.listener;

/**
 * Create By feihua  On 2021/9/29
 * 解析房间监听
 */
public interface OnDeRoomListener {
    void onDeRoom(String host,int port,String password,String exception);
}
