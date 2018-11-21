package com.freesky.clientapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.freesky.appbridge.DSClient;

/**
 * Created by letgogo on 2018/11/21.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        String result = DSClient.getInstance().send("第三方发送给APP服务端的消息");
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
