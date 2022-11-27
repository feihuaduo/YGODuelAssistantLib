package com.ourygo.lib.duelassistant.util;

public class DARecord {

    //卡查关键字
    public static final String[] CARD_SEARCH_KEY = new String[]{"?", "？"};

    public static final int YGO_ROOM_PROTOCOL_1=1;

    //加房关键字
    public static final String[] PASSWORD_PREFIX = {
            "M,", "m,",
            "T,","t,",
            "PR,", "pr,",
            "AI,", "ai,",
            "LF2,", "lf2,",
            "M#", "m#",
            "T#", "t#",
            "PR#", "pr#",
            "NS#", "ns#",
            "S#", "s#",
            "AI#", "ai#",
            "LF2#", "lf2#",
            "R#", "r#"
    };

    public static final String ROOM_PREFIX="room:{";

    public static final String ROOM_END="}";

    //卡组复制
    public static final String[] DeckTextKey = new String[]{"#main"};

    //卡组url前缀
    public static final String DECK_URL_PREFIX = "ygo://deck";
    public static final String ROOM_URL_PREFIX = "room://deck";
    public static final String HTTP_URL_PREFIX = "http://";
    public static final String HTTPS_URL_PREFIX = "https://";

    public static final String URL_SCHEME_HTTP="http";
    public static final String URL_SCHEME_HTTPS="https";

    public static final String URL_HOST_DECK = "deck.ourygo.top";

    public static final String ARG_PORT = "po";
    public static final String ARG_HOST = "h";
    public static final String ARG_PASSWORD = "pw";
    public static final String ARG_PORT_ALL = "port";
    public static final String ARG_HOST_ALL = "host";
    public static final String ARG_PASSWORD_ALL = "password";
    public static final String ARG_YGO_TYPE="ygotype";
    public static final String ARG_DECK="deck";
    public static final String ARG_ROOM="room";

    public static final String QUERY_VERSION = "v";

    public static final String QUERY_MAIN_ALL = "main";
    public static final String QUERY_EXTRA_ALL = "extra";
    public static final String QUERY_SIDE_ALL = "side";
    public static final String QUERY_YGO_TYPE = "ygotype";

    public static final String QUERY_DECK = "d";
    public static final String QUERY_MAIN = "m";
    public static final String QUERY_EXTRA = "e";
    public static final String QUERY_SIDE = "s";
    public static final String[] NUM_40_LIST = {
            "0"
            , "1"
            , "2"
            , "3"
            , "4"
            , "5"
            , "6"
            , "7"
            , "8"
            , "9"
            , "a"
            , "b"
            , "c"
            , "d"
            , "e"
            , "f"
            , "g"
            , "h"
            , "i"
            , "j"
            , "k"
            , "l"
            , "m"
            , "n"
            , "o"
            , "p"
            , "q"
            , "r"
            , "s"
            , "t"
            , "u"
            , "v"
            , "w"
            , "x"
            , "y"
            , "z"
            , "A"
            , "B"
            , "C"
            , "D"
    };

}
