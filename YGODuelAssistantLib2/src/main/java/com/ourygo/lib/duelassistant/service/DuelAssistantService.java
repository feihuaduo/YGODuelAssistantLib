package com.ourygo.lib.duelassistant.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ourygo.lib.duelassistant.R;
import com.ourygo.lib.duelassistant.listener.OnDuelAssistantListener;
import com.ourygo.lib.duelassistant.util.ClipManagement;
import com.ourygo.lib.duelassistant.util.DuelAssistantManagement;
import com.ourygo.lib.duelassistant.util.PermissionUtil;
import com.ourygo.lib.duelassistant.util.Util;

import java.lang.reflect.Method;
import java.util.List;


public class DuelAssistantService extends Service implements OnDuelAssistantListener {

    private static final String TAG = "DuelAssistantService";
    private static final String CHANNEL_ID = "YGOMobile";
    private static final String CHANNEL_NAME = "Duel_Assistant";
    private static final String DUEL_ASSISTANT_SERVICE_ACTION = "YGOMOBILE:ACTION_DUEL_ASSISTANT_SERVICE";
    private static final String CMD_NAME = "CMD";
    private static final String CMD_START_GAME = "CMD : START GAME";
    private static final String CMD_STOP_SERVICE = "CMD : STOP SERVICE";

    //悬浮窗显示的时间
    private static final int TIME_DIS_WINDOW = 3000;

    public static String DeckText = "";

    //悬浮窗布局View
    private View mFloatLayout;
    private TextView tv_message;
    private Button bt_join, bt_close;

    //是否可以移除悬浮窗上面的视图
    private boolean isDis = false;

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;

    private OnDuelAssistantListener onDuelAssistantListener;
    private DuelAssistantManagement duelAssistantManagement;
    //决斗跳转


    public void setOnDuelAssistantListener(OnDuelAssistantListener onDuelAssistantListener) {
        this.onDuelAssistantListener = onDuelAssistantListener;
    }

    @Override
    public IBinder onBind(Intent p1) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public void onCreate() {
        // TODO: Implement this method
        super.onCreate();
        //开启服务
        startForeground();
        //初始化加房布局
        createFloatView();
        duelAssistantManagement = DuelAssistantManagement.getInstance();
        duelAssistantManagement.addDuelAssistantListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        duelAssistantManagement.removeDuelAssistantListener(this);
        //关闭悬浮窗时的声明
        stopForeground(true);
    }


    private void startForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (PermissionUtil.isNotificationListenerEnabled(this)) {
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_view_duel_assistant);
                Intent intent = new Intent(this, this.getClass());
                intent.setAction(DUEL_ASSISTANT_SERVICE_ACTION);
                PendingIntent pendingIntent;

                intent.putExtra(CMD_NAME, CMD_START_GAME);
                pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.notification_view_duel_assistant, pendingIntent);

                intent.putExtra(CMD_NAME, CMD_STOP_SERVICE);
                pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.buttonStopService, pendingIntent);

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(null, null);
                channel.enableLights(false);
                channel.setSound(null, Notification.AUDIO_ATTRIBUTES_DEFAULT);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null)
                    manager.createNotificationChannel(channel);

                Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
//                builder.setSmallIcon(R.drawable.icl);
                builder.setCustomContentView(remoteViews);
                startForeground(1, builder.build());
            } else {
                //如果没有通知权限则关闭服务
                stopForeground(true);
                stopService(new Intent(DuelAssistantService.this, DuelAssistantService.class));
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        Log.d(TAG, "rev action:" + action);
        if (DUEL_ASSISTANT_SERVICE_ACTION.equals(action)) {
            String cmd = intent.getStringExtra(CMD_NAME);
            Log.d(TAG, "rev cmd:" + cmd);

            if (null == cmd) {
                Log.e(TAG, "cmd null");
            } else {
                switch (cmd) {
                    case CMD_STOP_SERVICE:
                        stopSelf();
                        break;

                    case CMD_START_GAME:
//                        Intent intent2 = new Intent(DuelAssistantService.this, MainActivity.class);
//                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent2);
                        break;

                    default:
                        Log.e(TAG, "unknown cmd:" + cmd);
                        break;

                }
                collapseStatusBar();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void collapseStatusBar() {
        try {
            @SuppressLint("WrongConstant") Object statusBarManager = getSystemService("statusbar");
            if (null == statusBarManager) {
                return;
            }
            Class<?> clazz = statusBarManager.getClass();

            Method methodCollapse;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                methodCollapse = clazz.getMethod("collapse");
            } else {
                methodCollapse = clazz.getMethod("collapsePanels");
            }
            methodCollapse.invoke(statusBarManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDeck(Uri uri, List<Integer> mainList, List<Integer> exList, List<Integer> sideList
            , boolean isCompleteDeck, String exception) {
        tv_message.setText(R.string.find_deck_text);
        bt_close.setText(R.string.close);
        bt_join.setText(R.string.save_open);
        disJoinDialog();
        showJoinDialog();
        new Handler().postDelayed(() -> {
            if (isDis) {
                isDis = false;
                mWindowManager.removeView(mFloatLayout);
            }
        }, TIME_DIS_WINDOW);
        bt_close.setOnClickListener(v -> disJoinDialog());
        bt_join.setOnClickListener(v -> {
            disJoinDialog();
            if (onDuelAssistantListener != null) {
                onDuelAssistantListener.onSaveDeck(uri, mainList, exList, sideList, isCompleteDeck, exception, ClipManagement.ID_CLIP_LISTENER);
                return;
            }
//            Intent startSetting = new Intent(getBaseContext(), MainActivity.class);
            //如果是卡组url
//            if (isUrl) {
//                Deck deckInfo = new Deck(getString(R.string.rename_deck) + System.currentTimeMillis(), Uri.parse(deckMessage));
//                File file = deckInfo.saveTemp(AppsSettings.get().getDeckDir());
//                if (!deckInfo.isCompleteDeck()){
//                    YGOUtil.show("当前卡组缺少完整信息，将只显示已有卡片");
//                }
//
//                startSetting.putExtra("flag", 2);
//                startSetting.putExtra(Intent.EXTRA_TEXT, file.getAbsolutePath());
//            } else {
//                //如果是卡组文本
//                try {
//                    //以当前时间戳作为卡组名保存卡组
//                    File file = DeckUtils.save(getString(R.string.rename_deck) + System.currentTimeMillis(), deckMessage);
//                    startSetting.putExtra("flag", 2);
//                    startSetting.putExtra(Intent.EXTRA_TEXT, file.getAbsolutePath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(DuelAssistantService.this, getString(R.string.save_failed_bcos) + e, Toast.LENGTH_SHORT).show();
//                }
//            }
        });

    }

    private void joinRoom(String host, int port, String password) {
        String message;
        if (!TextUtils.isEmpty(host))
            message = getString(R.string.quick_join)
                    + "\nIP：" + host
                    + "\n端口：" + port
                    + "\n密码：" + password;
        else
            message = getString(R.string.quick_join) + "\"" + password + "\"";
        tv_message.setText(message);
        bt_join.setText(R.string.join);
        bt_close.setText(R.string.close);
        disJoinDialog();
        showJoinDialog();
        new Handler().postDelayed(() -> {
            if (isDis) {
                isDis = false;
                mWindowManager.removeView(mFloatLayout);
            }
        }, TIME_DIS_WINDOW);

        bt_close.setOnClickListener(p1 -> disJoinDialog());
        bt_join.setOnClickListener(p1 -> {
            if (isDis) {
                isDis = false;
                mWindowManager.removeView(mFloatLayout);
            }
            if (onDuelAssistantListener != null) {
                onDuelAssistantListener.onJoinRoom(host, port, password, ClipManagement.ID_CLIP_LISTENER);
            }

//            ServerListAdapter mServerListAdapter = new ServerListAdapter(DuelAssistantService.this);
//
//            ServerListManager mServerListManager = new ServerListManager(DuelAssistantService.this, mServerListAdapter);
//            mServerListManager.syncLoadData();
//
//            File xmlFile = new File(getFilesDir(), Constants.SERVER_FILE);
//            VUiKit.defer().when(() -> {
//                ServerList assetList = ServerListManager.readList(DuelAssistantService.this.getAssets().open(ASSET_SERVER_LIST));
//                ServerList fileList = xmlFile.exists() ? ServerListManager.readList(new FileInputStream(xmlFile)) : null;
//                if (fileList == null) {
//                    return assetList;
//                }
//                if (fileList.getVercode() < assetList.getVercode()) {
//                    xmlFile.delete();
//                    return assetList;
//                }
//                return fileList;
//            }).done((list) -> {
//                if (list != null) {
//                    String host1=host;
//                    int port1=port;
//                    ServerInfo serverInfo = list.getServerInfoList().get(0);
//                    if (TextUtils.isEmpty(host1)){
//                        host1=serverInfo.getServerAddr();
//                        port1=serverInfo.getPort();
//                    }
//                    Util.duelIntent(DuelAssistantService.this, host1, port1, serverInfo.getPlayerName(), password);
//                }
//            });

        });

    }

    private void disJoinDialog() {
        if (isDis) {
            isDis = false;
            mWindowManager.removeView(mFloatLayout);
        }
    }

    private void showJoinDialog() {
        if (!isDis) {
            mWindowManager.addView(mFloatLayout, wmParams);
            isDis = true;
        }
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置背景透明
        wmParams.format = PixelFormat.TRANSLUCENT;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        //实现悬浮窗到状态栏
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //安卓7.0要求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //安卓8.0要求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //获取浮动窗口视图所在布局
        mFloatLayout = LayoutInflater.from(this).inflate(R.layout.duel_assistant_service, null);
        //添加mFloatLayout
        bt_join = mFloatLayout.findViewById(R.id.bt_join);
        tv_message = mFloatLayout.findViewById(R.id.tv_message);
        bt_close = mFloatLayout.findViewById(R.id.bt_close);
    }

    @Override
    public void onJoinRoom(String host, int port, String password, int id) {
        if (id == ClipManagement.ID_CLIP_LISTENER) {
            //如果有悬浮窗权限再显示
            if (PermissionUtil.isServicePermission(this)) {
                joinRoom(host, port, password);
            }
        }
    }

    @Override
    public void onCardQuery(String key, int id) {
        if (id == ClipManagement.ID_CLIP_LISTENER) {
            if (onDuelAssistantListener != null) {
                onDuelAssistantListener.onCardQuery(key, id);
            }
//            Intent intent = new Intent(DuelAssistantService.this, CardSearchFragment.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(CardSearchFragment.SEARCH_MESSAGE, key);
//            startActivity(intent);
        }
    }

    @Override
    public boolean isListenerEffective() {
        return Util.isContextExisted(this);
    }

    @Override
    public void onSaveDeck(Uri uri, List<Integer> mainList, List<Integer> exList, List<Integer> sideList, boolean isCompleteDeck, String exception, int id) {
        if (id == ClipManagement.ID_CLIP_LISTENER) {
            saveDeck(uri, mainList, exList, sideList, isCompleteDeck, exception);
        }
    }
}
