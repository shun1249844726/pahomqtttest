package com.lexinsmart.xushun.pahomqtttest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MqttV3Service {
    private static String TAG = "pahoMqtt";

    String addr = "";
    String port = "";
    public static MqttAndroidClient mqttAndroidClient = null;
    private static String topic = null;
    public static ArrayList<String> topicList = new ArrayList<String>();
    private static MqttConnectOptions mqttConnectOptions;
    private static ScheduledExecutorService scheduler;
    private static CallBack callback;

    public static boolean connectionMqttServer(Context context, Handler handler, String ServAddress, String ServPort, String userID, final ArrayList<String> Topics) {

        final String serverUri = "tcp://" + ServAddress + ":" + ServPort;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, userID);
        topicList = Topics;

        CallBack callBack = new CallBack(userID, handler);
        mqttAndroidClient.setCallback(callBack);
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                   System.out.println(TAG+"connect to:!" + serverUri);

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(Topics);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println(TAG+"Failed to connect to:!" + serverUri);
                    exception.printStackTrace();


                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void subscribeToTopic(ArrayList<String> subscriptionTopicList) {

        String[] subscriptionTopic = new String[subscriptionTopicList.size()];
        int[] qoss = new int[subscriptionTopicList.size()];
        for (int i = 0; i < subscriptionTopicList.size(); i++) {
            subscriptionTopic[i] = subscriptionTopicList.get(i);
            qoss[i] = 1;
        }

        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qoss, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println(TAG+"Failed to subscribe!");
                }
            });

            mqttAndroidClient.subscribe(subscriptionTopic,qoss);
            // THIS DOES NOT WORK!
//            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
//                }
//            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public static boolean publishMsg(String msg, int Qos, int position) {
        boolean successfulFlag = false;

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(topicList.get(position), message);
            System.out.println(TAG+ "Message Published");
            successfulFlag = true;
            if (!mqttAndroidClient.isConnected()) {
                Log.d(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            successfulFlag = false;
            System.out.println(TAG+ "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }

        return successfulFlag;

    }
}

