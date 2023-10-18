package com.ourygo.lib.duelassistant.test;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.feihua.dialogutils.util.DialogUtils;
import com.ourygo.lib.duelassistant.util.YGODAUtil;

public class MainActivity extends AppCompatActivity {

    private DialogUtils dialogUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialogUtils = DialogUtils.getInstance(this);
        findViewById(R.id.bt_decode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View[] view = dialogUtils.dialoge(null, null);
                EditText editText = (EditText) view[0];
                view[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = editText.getText().toString().trim();
                        YGODAUtil.deDeckListener(message, (uri, mainList, exList, sideList, isCompleteDeck, exception) -> {
                            if (!TextUtils.isEmpty(exception)) {
                                show("解析失败：" + exception);
                                return;
                            }
                            show("解析成功： " + mainList.size() + " " + exList.size()
                                    + " " + sideList.size()
                                    + " " + isCompleteDeck);
                        });
                    }
                });
            }
        });

    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", message);
    }
}