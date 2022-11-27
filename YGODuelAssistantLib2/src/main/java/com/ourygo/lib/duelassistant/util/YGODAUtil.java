package com.ourygo.lib.duelassistant.util;


import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_VERSION;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_YGO_TYPE;

import android.net.Uri;

import com.ourygo.lib.duelassistant.listener.OnDeDeckListener;
import com.ourygo.lib.duelassistant.listener.OnDeRoomListener;

import java.util.List;
import java.util.Map;

/**
 * Create By feihua  On 2021/9/29
 */
public class YGODAUtil {

    /**
     * 解析卡组协议
     * @param message 识别的文本
     * @param isUri 是否是uri
     * @param onDeDeckListener 监听回调
     */
    public static void deDeckListener(String message, boolean isUri, OnDeDeckListener onDeDeckListener) {
        if (isUri)
            deDeckListener(Uri.parse(message), onDeDeckListener);
        else
            deDeckListener(message, onDeDeckListener);
    }

    /**
     * 根据卡组文件文本内容解析卡组协议
     * @param deckFileMessage 卡组文件文本内容
     * @param onDeDeckListener 监听回调
     */
    public static void deDeckListener(String deckFileMessage, OnDeDeckListener onDeDeckListener) {
        DADeckUtil.deDeckListener(deckFileMessage, onDeDeckListener);
    }

    /**
     * 根据uri解析卡组协议
     * @param uri 识别的uri
     * @param onDeDeckListener 监听回调
     */
    public static void deDeckListener(Uri uri, OnDeDeckListener onDeDeckListener) {
        DADeckUtil.deDeckListener(uri, onDeDeckListener);
    }

    /**
     * 卡组转uri
     * @param mainList 主卡组卡密
     * @param exList 额外卡组卡密
     * @param sideList 副卡组卡密
     * @param parameter uri自定义参数，没有可以为空
     * @return 转换后的uri
     */
    public static Uri toUri(List<Integer> mainList, List<Integer> exList, List<Integer> sideList, Map<String, String> parameter) {
        return toUri(DARecord.URL_SCHEME_HTTP, DARecord.URL_HOST_DECK, mainList, exList, sideList, parameter);
    }

    /**
     * 卡组转uri
     * @param scheme 协议
     * @param host 主机
     * @param mainList 主卡组卡密
     * @param exList 额外卡组卡密
     * @param sideList 副卡组卡密
     * @param parameter uri自定义参数，没有可以为空
     * @return 转换后的uri
     */
    public static Uri toUri(String scheme,String host,List<Integer> mainList,List<Integer> exList,List<Integer> sideList,Map<String,String> parameter) {
        return DADeckUtil.toUri(scheme, host, mainList, exList, sideList, parameter);
    }

    /**
     * 根据uri解析房间
     * @param uri 识别的uri
     * @param onDeRoomListener 监听回调
     */
    public static void deRoomListener(Uri uri, OnDeRoomListener onDeRoomListener) {
        String host = "", password = "";
        int port = 0;

        int version = DARecord.YGO_ROOM_PROTOCOL_1;

        try {
            String ygoType = uri.getQueryParameter(QUERY_YGO_TYPE);
            if (ygoType.equals(DARecord.ARG_ROOM)) {
                version = Integer.parseInt(uri.getQueryParameter(QUERY_VERSION));
            }
        } catch (Exception exception) {
            onDeRoomListener.onDeRoom(null, -1, null, "非加房协议");
        }

        switch (version) {
            case DARecord.YGO_ROOM_PROTOCOL_1:
            default:
                try {
                    host = UrlUtil.deURL(uri.getQueryParameter(DARecord.ARG_HOST));
                } catch (Exception ignored) {
                }
                try {
                    port = Integer.parseInt(UrlUtil.deURL(uri.getQueryParameter(DARecord.ARG_PORT)));
                } catch (Exception ignored) {
                }
                try {
                    password = UrlUtil.deURL(uri.getQueryParameter(DARecord.ARG_PASSWORD));
                } catch (Exception ignored) {
                }
                break;
        }
        onDeRoomListener.onDeRoom(host, port, password, null);
    }

}
