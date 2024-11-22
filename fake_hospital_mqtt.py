from time import sleep

import paho.mqtt.client as mqtt

BROKER = "localhost"
SUBSCRIBE_TOPIC = "hospital/emergencies"
PUBLISH_TOPIC = "hospital/dispatch"


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected to MQTT Broker!")
    else:
        print("Failed to connect, return code %d\n", rc)


def on_message(client, userdata, msg):
    print(msg.payload)
    sleep(10)
    
    client.publish(PUBLISH_TOPIC, "Ambulance is on its way!", qos=2)

client = mqtt.Client()
client.on_message = on_message
client.on_connect = on_connect
client.connect(BROKER, 1883)
client.subscribe(SUBSCRIBE_TOPIC)
client.loop_forever()