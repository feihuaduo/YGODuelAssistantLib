package com.ourygo.lib.duelassistant.util;


import static com.ourygo.lib.duelassistant.util.DARecord.ARG_DECK;
import static com.ourygo.lib.duelassistant.util.DARecord.NUM_40_LIST;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_DECK;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_EXTRA_ALL;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_MAIN_ALL;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_SIDE_ALL;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_VERSION;
import static com.ourygo.lib.duelassistant.util.DARecord.QUERY_YGO_TYPE;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.ourygo.lib.duelassistant.listener.OnDeDeckListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Create By feihua  On 2022/11/19
 */
public class DADeckUtil {

    private static final int YGO_DECK_PROTOCOL_0 = 0;
    private static final int YGO_DECK_PROTOCOL_1 = 1;
    //协议0id分隔符
    private static final String CARD_DIVIDE_ID = "_";
    //协议0数量分隔符
    private static final String CARD_DIVIDE_NUM = "*";

    private static final int DECK_TYPE_MAIN=0;
    private static final int DECK_TYPE_EX=1;
    private static final int DECK_TYPE_SIDE=2;

    protected static void deDeckListener(String deckFileMessage, OnDeDeckListener onDeDeckListener){
        List<Integer> mainlist,extraList,sideList;
        mainlist = new ArrayList<>();
        extraList = new ArrayList<>();
        sideList = new ArrayList<>();
        String[] info=deckFileMessage.split("\n");
        int type=-1;
        for (String line:info) {
            if (line.startsWith("!side")) {
                type = DECK_TYPE_SIDE;
                continue;
            }
            if (line.startsWith("#")) {
                if (line.startsWith("#main")) {
                    type = DECK_TYPE_MAIN;
                } else if (line.startsWith("#extra")) {
                    type = DECK_TYPE_EX;
                }
                continue;
            }
            line = line.trim();
            if (line.length() == 0 || !TextUtils.isDigitsOnly(line)) {
                continue;
            }
            if (line.equals("0") || line.length() > 9) {//密码为0或者长度大于9位直接过滤
                continue;
            }
            Integer id = Integer.parseInt(line);
            if (type == DECK_TYPE_MAIN) {
                mainlist.add(id);
            } else if (type == DECK_TYPE_EX) {
                extraList.add(id);
            } else if (type == DECK_TYPE_SIDE) {
                sideList.add(id);
            }
        }
        onDeDeckListener.onDeDeck(null,mainlist,extraList,sideList,false,null);
    }

    protected static void deDeckListener(Uri uri, OnDeDeckListener onDeDeckListener) {
        List<Integer> mainlist,extraList,sideList;
        String deckException = null;
        mainlist = new ArrayList<>();
        extraList = new ArrayList<>();
        sideList = new ArrayList<>();

        boolean isCompleteDeck = true;
        int version = YGO_DECK_PROTOCOL_0;

        try {
            String ygoType = uri.getQueryParameter(QUERY_YGO_TYPE);
            if (ygoType.equals(ARG_DECK)) {
                version = Integer.parseInt(uri.getQueryParameter(QUERY_VERSION));
            }
        } catch (Exception exception) {
            version = YGO_DECK_PROTOCOL_0;
        }
        String main = null, extra = null, side = null;

        List<String> mList = new ArrayList<>();
        List<String> eList = new ArrayList<>();
        List<String> sList = new ArrayList<>();

        String[] mains, extras, sides;
        try {

            URL url ;
        switch (version) {
            case YGO_DECK_PROTOCOL_0:
                main = uri.getQueryParameter(QUERY_MAIN_ALL);
                extra = uri.getQueryParameter(QUERY_EXTRA_ALL);
                side = uri.getQueryParameter(QUERY_SIDE_ALL);

                mains = main.split(CARD_DIVIDE_ID);
                mList.addAll(Arrays.asList(mains));

                extras = extra.split(CARD_DIVIDE_ID);
                eList.addAll(Arrays.asList(extras));

                sides = side.split(CARD_DIVIDE_ID);
                sList.addAll(Arrays.asList(sides));

                for (String m : mList) {
                    int[] idNum = toIdAndNum(m, version);
                    if (idNum[0] > 0) {
                        for (int i = 0; i < idNum[1]; i++) {
                            mainlist.add(idNum[0]);
                        }
                    }
                }

                for (String m : eList) {
                    int[] idNum = toIdAndNum(m, version);
                    if (idNum[0] > 0) {
                        for (int i = 0; i < idNum[1]; i++) {
                            extraList.add(idNum[0]);
                        }
                    }
                }

                for (String m : sList) {
                    int[] idNum = toIdAndNum(m, version);
                    if (idNum[0] > 0) {
                        for (int i = 0; i < idNum[1]; i++) {
                            sideList.add(idNum[0]);
                        }
                    }
                }

                break;
            case YGO_DECK_PROTOCOL_1:
            default:
                String deck = uri.getQueryParameter(QUERY_DECK);
                if (TextUtils.isEmpty(deck))
                    return;
                int end = deck.indexOf(" ");
                if (end != -1) {
                    deck = deck.substring(0, end);
                }
                end = deck.indexOf("\n");
                if (end != -1) {
                    deck = deck.substring(0, end);
                }
                deck = deck.replace("-", "+");
                deck = deck.replace("_", "/");
                byte[] bytes = Base64.decode(deck, Base64.NO_WRAP);
                Log.e("Deck", deck.length() + "字符位数" + bytes.length);
                String[] bits = new String[bytes.length * 8];

                for (int i = 0; i < bytes.length; i++) {

                    String b = Integer.toBinaryString(bytes[i]);

                    b = toNumLength(b, 8);
                    if (b.length() > 8)
                        b = b.substring(b.length() - 8);
                    if (i < 8)
                        Log.e("Deck", b + "  byte：" + bytes[i]);
                    for (int x = 0; x < 8; x++)
                        bits[i * 8 + x] = b.substring(x, x + 1);
                }
                bits = toNumLength(bits, 16);


                int mNum = Integer.valueOf(getArrayString(bits, 0, 8), 2);
                int eNum = Integer.valueOf(getArrayString(bits, 8, 12), 2);
                int sNum = Integer.valueOf(getArrayString(bits, 12, 16), 2);

                Log.e("Deck", "种类数量" + mNum + " " + eNum + " " + sNum + " ");
                Log.e("Deck", "m：" + getArrayString(bits, 0, 8));
                Log.e("Deck", "e：" + getArrayString(bits, 8, 12));
                Log.e("Deck", "s：" + getArrayString(bits, 12, 16));
                if (bits.length < (16 + (mNum * 29))) {
                    mNum = (bits.length - 16) / 29;
                    isCompleteDeck = false;
                }
                for (int i = 0; i < mNum; i++) {
                    int cStart = 16 + (i * 29);
                    int cardNum = Integer.valueOf(getArrayString(bits, cStart, cStart + 2), 2);
                    int cardId = Integer.valueOf(getArrayString(bits, cStart + 2, cStart + 29), 2);
                    for (int x = 0; x < cardNum; x++) {
                        mainlist.add(cardId);
                    }
                }
                if (!isCompleteDeck)
                    return;
                if (bits.length < (16 + mNum * 29 + (eNum * 29))) {
                    eNum = (bits.length - 16 - (mNum * 29)) / 29;
                    isCompleteDeck = false;
                }
                for (int i = 0; i < eNum; i++) {
                    int cStart = 16 + mNum * 29 + (i * 29);
                    int cardNum = Integer.valueOf(getArrayString(bits, cStart, cStart + 2), 2);
                    Log.e("DeckSetting", eNum + " 当前 " + i + "  " + cStart);
                    int cardId = Integer.valueOf(getArrayString(bits, cStart + 2, cStart + 29), 2);
                    for (int x = 0; x < cardNum; x++) {
                        extraList.add(cardId);
                    }
                }

                if (!isCompleteDeck)
                    return;
                if (bits.length < (16 + mNum * 29 + (eNum * 29) + (sNum * 29))) {
                    sNum = (bits.length - 16 - (mNum * 29) - (eNum * 29)) / 29;
                    isCompleteDeck = false;
                }
                for (int i = 0; i < sNum; i++) {
                    int cStart = 16 + mNum * 29 + eNum * 29 + (i * 29);
                    int cardNum = Integer.valueOf(getArrayString(bits, cStart, cStart + 2), 2);
                    int cardId = Integer.valueOf(getArrayString(bits, cStart + 2, cStart + 29), 2);
                    for (int x = 0; x < cardNum; x++) {
                        sideList.add(cardId);
                    }
                }
                break;
        }
        } catch (Exception exception) {
            deckException=exception.toString();
        }
        onDeDeckListener.onDeDeck(uri,mainlist,extraList,sideList,isCompleteDeck,deckException);
    }

    protected static Uri toUri(String scheme, String host, List<Integer> mainList, List<Integer> exList, List<Integer> sideList, Map<String,String> parameter) {
        Uri.Builder uri = Uri.parse(scheme + "://")
                .buildUpon()
                .authority(host);
        //.path(Constants.PATH_DECK);
        if (parameter!=null){
            for (Map.Entry<String,String> para:parameter.entrySet()){
                uri.appendQueryParameter(para.getKey(),para.getValue());
            }
        }
        uri.appendQueryParameter(QUERY_YGO_TYPE, ARG_DECK);
        uri.appendQueryParameter(QUERY_VERSION, "1");

//        if (mainlist.size() != 0)
//            deck+=toString(mainlist);
//        if (extraList.size() != 0)
//            deck+=toString(extraList);
//        if (sideList.size() != 0)
//            deck+=toString(sideList);
        int mNum = getTypeNum(mainList);
        int eNum = getTypeNum(exList);
        int sNum = getTypeNum(sideList);

        String deck = toBit(mainList, exList, sideList, mNum, eNum, sNum);

        String m = Integer.toBinaryString(mNum);
        String e = Integer.toBinaryString(eNum);
        String s = Integer.toBinaryString(sNum);

        m = toNumLength(m, 8);
        e = toNumLength(e, 4);
        s = toNumLength(s, 4);

        Log.e("Deck", "分享数量" + mNum + " " + eNum + "  " + sNum);

        deck = m + e + s + deck;
        byte[] bytes = toBytes(deck);
        String message = Base64.encodeToString(bytes, Base64.NO_WRAP);
        Log.e("Deck", message.length() + " 转换时位数 " + bytes.length);
        message = message.replace("+", "-");
        message = message.replace("/", "_");
        message = message.replace("=", "");
        Log.e("Deck", "转换后数据" + message);
        for (int i = 0; i < 8; i++) {

        }
        uri.appendQueryParameter(QUERY_DECK, message);

        return uri.build();
    }

    public static String getArrayString(String[] bytes, int start, int end) {
        String message = "";
        for (int i = start; i < end; i++) {
            message += bytes[i];
        }
        return message;
    }


    /**
     * 根据单张卡片信息转换为可读的卡密和数量，只有协议0在用
     * @param m 协议0的单张卡片信息
     * @param protocol  协议版本
     * @return  卡片信息，元素0为卡密，元素1为卡片数量
     */
    private static int[] toIdAndNum(String m, int protocol) {
        //元素0为卡密，元素1为卡片数量
        int[] idNum;

        switch (protocol) {
            case YGO_DECK_PROTOCOL_0:
                idNum = toIdAndNum0(m);
                break;
            default:
                idNum = toIdAndNum0(m);
                break;
        }
        return idNum;
    }

    private static int[] toIdAndNum0(String m) {
        //元素0为卡密，元素1为卡片数量
        int[] idNum = {0, 1};
        if (m.contains(CARD_DIVIDE_NUM)) {
            //如果有卡片分隔符则分离卡密和数量，否则视为一张卡
            try {
                idNum[1] = Integer.parseInt(m.substring(m.length() - 1));
            } catch (Exception e) {

            }
            idNum[0] = toId(m.substring(0, m.length() - 2), YGO_DECK_PROTOCOL_0);
        } else {
            idNum[0] = toId(m, YGO_DECK_PROTOCOL_0);
        }
        return idNum;
    }

    private static String toBit(List<Integer> mainlist, List<Integer> extraList, List<Integer> sideList, int mNum, int eNum, int sNum) {
        String mains = tobyte(mainlist, mNum);
        String extras = tobyte(extraList, eNum);
        String sides = tobyte(sideList, sNum);
        String message = mains + extras + sides;
        return message;
    }

    private static int getTypeNum(List<Integer> list) {
        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            Integer id = list.get(i);
            if (id > 0) {
                num++;
                //如果是最后一张就不用对比下张卡
                if (i != list.size() - 1) {
                    int id1 = list.get(i + 1);
                    //如果下张是同名卡
                    if (id1 == id) {
                        //如果是倒数第二张就不用对比下下张卡
                        if (i != list.size() - 2) {
                            id1 = list.get(i + 2);
                            //如果下下张是同名卡
                            if (id1 == id) {
                                i++;
                            }
                        }
                        i++;
                    }
                }
            }
        }
        return num;
    }

    private static String tobyte(List<Integer> ids, int typeNum) {
        String bytes = "";
        if (ids==null)
            return bytes;
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            if (id > 0) {
                //转换为29位二进制码
                String idB = toB(id);
                //如果是最后一张就不用对比下张卡
                if (i != ids.size() - 1) {
                    int id1 = ids.get(i + 1);
                    //同名卡张数
                    int tNum = 1;
                    //如果下张是同名卡
                    if (id1 == id) {
                        tNum++;
                        //如果是倒数第二张就不用对比下下张卡
                        if (i != ids.size() - 2) {
                            id1 = ids.get(i + 2);
                            //如果下下张是同名卡
                            if (id1 == id) {
                                tNum++;
                                i++;
                            }
                        }
                        i++;
                    }
                    tNum = Math.min(3, tNum);
                    switch (tNum) {
                        case 1:
                            idB = "01" + idB;
                            break;
                        case 2:
                            idB = "10" + idB;
                            break;
                        case 3:
                            idB = "11" + idB;
                            break;
                    }
                } else {
                    idB = "01" + idB;
                }
                bytes += idB;
            }
        }
        return bytes;
    }


    private static String toB(int id) {
        return toNumLength(Integer.toBinaryString(id), 27);
    }

    //压缩卡密,目前直接转换为40进制
    private static String compressedId(int id) {
        StringBuilder compressedId1 = new StringBuilder();

        while (id > 40) {
            compressedId1.insert(0, NUM_40_LIST[id % 40]);
            id /= 40;
        }
        compressedId1.insert(0, NUM_40_LIST[id]);
        if (compressedId1.length() < 5)
            for (int i = compressedId1.length(); i < 5; i++)
                compressedId1.insert(0, "0");
        return compressedId1.toString();
    }


    private static int getCardIdUnCompressedId(String compressedNum) {
        for (int i = 0; i < NUM_40_LIST.length; i++) {
            if (compressedNum.equals(NUM_40_LIST[i]))
                return i;
        }
        return 0;
    }



    private static int toId(String str, int version) {
        if (TextUtils.isEmpty(str)) return 0;
        try {
            switch (version) {
                case YGO_DECK_PROTOCOL_0:
                    return Integer.parseInt(str);
                case YGO_DECK_PROTOCOL_1:
                    return Integer.parseInt(str);
                default:
                    return Integer.parseInt(str);
            }
        } catch (Exception e) {
            return 0;
        }
    }



    private static byte[] toBytes(String bits) {

        int y = bits.length() % 8;
        Log.e("Deck",bits.length()+"之前余数"+y);
        if (y != 0)
            bits = toNumLengthLast(bits, bits.length()+8 - y);
        Log.e("Deck",bits.length()+"余数"+y);
        byte[] bytes=new byte[bits.length()/8];
        for (int i=0;i<bits.length()/8;i++) {
            bytes[i] = (byte) Integer.valueOf(bits.substring(i * 8, i * 8 + 8), 2).intValue();
            if (i<8){
                Log.e("Deck",bits.substring(i*8,i*8+8)+" 字节 "+bytes[i] );

            }
        }
        Log.e("Deck","二进制"+bits );
        return bytes;
    }

    /**
     * 将数字转换为制定长度，不够前面添0
     * @param message 数字内容
     * @param num 位数
     * @return 转换后的数字
     */
    private static String toNumLength(String message, int num) {
        while (message.length() < num) {
            message = "0" + message;
        }
        return message;
    }

    /**
     * 将数字转换为制定长度，不够后面添0
     * @param message 数字内容
     * @param num 位数
     * @return 转换后的数字
     */
    private static String toNumLengthLast(String message, int num) {
        while (message.length() < num) {
            message +="0";
        }
        return message;
    }

    /**
     * 将数组转换为制定长度，不够后面添0
     * @param nums
     * @param num
     * @return
     */
    private static String[] toNumLength(String[] nums, int num) {
        if (nums.length < num) {
            String[] bms = nums;
            nums = new String[num];
            for (int i = 0; i < num - bms.length; i++)
                nums[i] = "0";
            for (int i = 0; i < bms.length; i++)
                nums[i + num - bms.length] = bms[i];
        }
        return nums;
    }

    /**
     *
     *解析卡密，做40进制残留的东西，用不上，目前直接16进制转换为10进制
     */
    @Deprecated
    private static int unId(String id) {
        int num = 0;
        id = id.trim();
        String[] sList = new String[id.length()];
        for (int i = 0; i < id.length(); i++) {
            sList[i] = id.charAt(i) + "";
        }

        for (int i = sList.length - 1; i >= 0; i--)
            num += (getCardIdUnCompressedId(sList[i]) * Math.pow(40, sList.length - 1 - i));
//        Log.e("DeckUU",(getCardIdUnCompressedId(sList[0]) * Math.pow(40, sList.length-1)+"   "+sList.length+"   num"+num));
        return num;
    }

}
