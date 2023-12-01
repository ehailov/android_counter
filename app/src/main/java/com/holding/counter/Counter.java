package com.holding.counter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

class Counter {

    static private final String TAG = "MyLog";
    static private final String server = "tcp://bc-24.ru:1883";
    static private final String user = "user";
    static private final String pass = "user";
    static private final String topic = "1761608504/JSON";
    static private MqttAndroidClient mqttAndroidClient;
    static final String clientId = "AndroidClient";
    static private CounterResult mDelegate;

    static public void subscription(Context context, CounterResult delegate) {
        mDelegate = delegate;
        if (mqttAndroidClient == null) mqttAndroidClient = new MqttAndroidClient(context, server, clientId, Ack.AUTO_ACK);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                mDelegate.onMessage(s);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                mDelegate.onFailure(throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.e(TAG, " messageArrived " + mqttMessage);
                mDelegate.onSuccess(deserializeJson(mqttMessage.toString()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                mDelegate.onMessage(iMqttDeliveryToken.toString());
            }
        });
        start();
    }

    static private void start() {
        if (!mqttAndroidClient.isConnected()) {
            IMqttActionListener listener = new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "Failure to connect to: " + server + " " + exception.getMessage().toString());
                    mDelegate.onFailure("Failure to connect to: " + server + " " + exception.getMessage().toString());
                }
            };
            connect(listener);
        }
        else subscribeToTopic(topic);
    }

    static private void connect(IMqttActionListener listener){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(user);
        mqttConnectOptions.setPassword(user.toCharArray());
        mqttAndroidClient.connect(mqttConnectOptions,null, listener);
    }

    static private void subscribeToTopic(String topic) {
        mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                mDelegate.onMessage("Subscribed: " + topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                mDelegate.onMessage("Failure: " + topic);
            }
        });
    }

    static private CounterModel deserializeJson(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, CounterModel.class);
    }

    static public void unscribe() {
        try {
            mqttAndroidClient.unsubscribe(topic);
        }
        catch (Exception e) {
            mDelegate.onFailure(e.getMessage());
        }
    }
}
