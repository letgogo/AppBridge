package com.freesky.hostapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.freesky.hostapp.service.Message;
import com.freesky.hostapp.service.MessageDispatcher;

/**
 * Created by letgogo on 2018/11/20.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_disptch) {
            Message message = new Message("com.freesky.clientapp",
                    "我是来自HostApp的消息");
            MessageDispatcher.getInstance().dispatchMessage(message);
        }
    }
}
