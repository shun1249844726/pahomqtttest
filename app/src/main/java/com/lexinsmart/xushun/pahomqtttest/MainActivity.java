package com.lexinsmart.xushun.pahomqtttest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://120.92.**:1883";


    String clientId = "xushunpaho";
    final String subscriptionTopic = "xushunpahotopic";
    final String publishTopic = "xushunexampleAndroidPublishTopic";
    final String publishMessage = "Hello xushun!";

    private String TAG = "pahoMqtt";

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  //OSS相关
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
        setContentView(R.layout.activity_main);
        mContext = this;

        ArrayList<String> topicList = new ArrayList<String>();
        topicList.add(subscriptionTopic);
        topicList.add("a");
        topicList.add("b");



        MqttV3Service.connectionMqttServer(mContext,myHandler, Constant.MQTT_ADDRESS, Constant.MQTT_PORT, clientId, topicList);

        Button sendBtn = findViewById(R.id.btn_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MqttV3Service.publishMsg("d",1,0);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                System.out.println(TAG+"网络连接成功!");


            } else if (msg.what == 0) {
                System.out.println(TAG+"网络连接失败!");

            } else if (msg.what == 2) {
                String strContent = "";
                strContent += msg.getData().getString("content");

                System.out.println(TAG+" strcontent:" + strContent);

            } else if (msg.what == 3) {
                if (!MqttV3Service.mqttAndroidClient.isConnected()) {
                    System.out.println(TAG+ "MQTT:连接丢失");
                    System.out.println(TAG+"断开连接");

                }
            }
        }
    };

}
