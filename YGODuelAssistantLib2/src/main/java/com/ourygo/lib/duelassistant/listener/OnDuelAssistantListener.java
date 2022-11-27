package com.ourygo.lib.duelassistant.listener;

import android.net.Uri;

import java.util.List;

public interface OnDuelAssistantListener {
    void onJoinRoom(String host,int port,String password,int id);
    void onCardQuery(String key,int id);
    boolean isListenerEffective();

    void onSaveDeck(Uri uri, List<Integer> mainList, List<Integer> exList, List<Integer> sideList, boolean isCompleteDeck, String exception,int id);

}