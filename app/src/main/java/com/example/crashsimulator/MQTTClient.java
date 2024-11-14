package com.example.crashsimulator;

import android.util.Log;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.MqttClient;

import java.util.ArrayList;

public class MQTTClient {
    public static String TAG = "MQTTClient";
    private static final String CHANNEL_ID = "MQTTClientChannel";

    // Set host of “VirtualBox Host-Only Ethernet Adapter” ,
    // To find it, type “ipconfig /all” and look for the adapter with that name
    String serverHost = "192.168.56.1";
    int serverPort = 1883;
    String subscriptionTopic = "hospital/dispatch";
    String publishingTopic = "hospital/emergencies";
    Mqtt3AsyncClient client;
    ArrayList<String> messagesSent, messagesReceived;

    // Constructor
    public MQTTClient() {
        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("my-mqtt-client-id")
                .serverHost(serverHost)
                .serverPort(serverPort)
                //.useSslWithDefaultConfig()
                .buildAsync();

        messagesSent = new ArrayList<>();
        messagesReceived = new ArrayList<>();
    }

    void connectToBroker() {
        if(client != null) {
            client.connectWith()
                    //.simpleAuth()
                    //.username("")
                    //.password("".getBytes())
                    //.applySimpleAuth()
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
//                            publishMessage();
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
