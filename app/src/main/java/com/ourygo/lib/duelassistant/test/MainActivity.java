package com.ourygo.lib.duelassistant.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.feihua.dialogutils.util.DialogUtils;

public class MainActivity extends AppCompatActivity {

    private DialogUtils dialogUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialogUtils=DialogUtils.getInstance(this);


    }
}