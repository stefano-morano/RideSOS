package com.example.crashsimulator;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;

import java.util.ArrayList;

public class MQTTClient {
    public static String TAG = "MQTTClient";
    private static final String CHANNEL_ID = "MQTTClientChannel";
    private static final String CHANNEL_NAME = "Ambulance Updates";

    String serverHost = "10.0.2.2";
    int serverPort = 1883;
    String subscriptionTopic = "hospital/dispatch";
    String publishingTopic = "hospital/emergencies";
    Mqtt3AsyncClient client;
    boolean connected;
    ArrayList<String> messagesSent, messagesReceived;
    Context ctx;

    // Constructor
    public MQTTClient(Context ctx) {
        this.ctx = ctx;
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                //.useSslWithDefaultConfig()
                .buildAsync();

        messagesSent = new ArrayList<>();
        messagesReceived = new ArrayList<>();
        AppHelper.CreateNotificationChannel(ctx, CHANNEL_ID, CHANNEL_NAME);
    }

    void connectToBroker() {
        if(client != null) {
            client.connectWith()
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            // handle failure
                            Log.d(TAG, "Problem connecting to server:");
                            Log.d(TAG, throwable.toString());
                        } else {
                            // connected -> setup subscribes and publish a message
                            Log.d(TAG, "Connected to server");
                            subscribeToTopic();
                            connected = true;
                            //publishMessage("hello");
                        }
                    });
        } else {
            Log.d(TAG, "Cannot connect client (null)");
        }
    }

    void subscribeToTopic() {
        client.subscribeWith()
                .topicFilter(subscriptionTopic)
                .callback(publish -> {
                    // "publish" is an object of class Mqtt3Publish, which can be used to
                    // obtain the information of the received message
                    // Process the received message
                    String msg_payload = new String(publish.getPayloadAsBytes());
                    // Change text of txt received
                    String received_text = "Message received (topic: "+subscriptionTopic+"): "+msg_payload;
                    Log.d(TAG, received_text);
                    messagesReceived.add(received_text);
                    Notification notification = AppHelper.CreateNotification(ctx,
                            CHANNEL_ID, CHANNEL_NAME, msg_payload, R.drawable.emergency_notification);
                    NotificationManager manager = ctx.getSystemService(NotificationManager.class);
                    manager.notify(1, notification);
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Handle failure to subscribe
                        Log.d(TAG, "Problem subscribing to topic:");
                        Log.d(TAG, throwable.toString());
                    } else {
                        // Handle successful subscription, e.g. logging or incrementing a metric
                        Log.d(TAG, "Subscribed to topic");
                    }
                });
    }

    void publishMessage(String msg_payload) {
        client.publishWith()
                .topic(publishingTopic)
                .payload(msg_payload.getBytes())
                .qos(MqttQos.fromCode(2))
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        // handle failure to publish
                        Log.d(TAG, "Problem publishing on topic:");
                        Log.d(TAG, throwable.toString());

                    } else {
                        // handle successful publish, e.g. logging or incrementing a metric
                        // Change text of txt published
                        String published_text = "Message published (topic: "+publishingTopic+"): "+msg_payload;
                        Log.d(TAG, published_text);
                        messagesSent.add(published_text);

                        Notification notification = AppHelper.CreateNotification(ctx,
                                CHANNEL_ID, CHANNEL_NAME, "An ambulance will soon help you!", R.drawable.emergency_notification);
                        NotificationManager manager = ctx.getSystemService(NotificationManager.class);
                        manager.notify(1, notification);
                    }
                });
    }

    void disconnectFromBroker() {
        if (client != null) {
            client.disconnect()
                    .whenComplete ((result, throwable) -> {
                        if (throwable != null) {
                            // handle failure
                            Log.d(TAG, "Problem disconnecting from server:");
                            Log.d(TAG, throwable.toString());
                        } else {
                            Log.d(TAG, "Disconnected from server");
                        }
                    });
        } else {
            Log.d(TAG, "Cannot disconnect client (null)");
        }
    }
}
