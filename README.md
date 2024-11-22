# Ride SOS - Crash Detection & Emergency Alert App

<div align="center">
    <img src="app/src/main/res/drawable/logo.png" alt="Description" width="300"/>
</div>

## Overview
Ride SOS is a life-saving smartphone application designed to detect accidents and notify emergency contacts in real time. Whether you're a motorcyclist, cyclist, or driver, Ride SOS provides an added layer of safety during your travels by using advanced sensors in your smartphone to detect crashes and respond quickly.

## Features
### 🚨 Crash Detection
Utilizes your smartphone’s accelerometer and GPS sensors to detect sudden impacts or abnormal movements associated with crashes.
### 📡 MQTT Emergency Alerts
Automatically sends an SOS message to the Hospital MQTT server containing the crash location and the user informations.
### 📍 Ambulance calling
During the question activity after a crash, you can automatically call an ambulance by only pressing the phone icon.
### ❓ Personal Quiz 
When the app detect a crash, it will automatically open a pop-up that start the emergency services.

## Getting Started

### Pre-Requisites
In order to have MQTT functionalities, you need to:
- Download and start an MQTT Broker in your pc (we used Windows version of [Mosquitto](https://mosquitto.org/download/))
- Download and start [Gnirehtet](https://github.com/Genymobile/gnirehtet) for providing reverse tethering between pc-Android phone.
- Have python installed, along with paho-mqtt package. You can install it through:
  ```
  pip install paho-mqtt
  ```

### Installation
0. Connect your Android device with your computer and start Gnirehtet reverse tethering service.
1. Download _fake_hospital_mqtt.py_ script from this repository and run it from your terminal.

   > Make sure to start your MQTT broker before or it will not work!
2. Download _RideSOS.apk_ from this repository and install it on your android device.
3. Run _RideSOS_ application granting it the following permissions.

### Grant necessary permissions:
1. Location Services
2. Call services
3. Notifications

### Signup:
1. Put your personal information inside the label to create your profile.
2. Set your answers to create your personal quiz test.

## Activities

### 🏠 Home
This is the main page of the application. There's the main big switch inside that activates or deactivates the crash detector. The application will work in background also with the phone locked. 

### 🇭 Hospital
In this page you can find the list of hospitals (Madrid) that is getting updated every time you open the app. The hospital are sorted on a distance order. You can also search an hospital thanks to the search bar on the upside.

### 👤 Profile
In this page you can see your already set-up profile and change informations whenever you need it.

### 🚨 Alarm
When the app detect a crash, it will open a pop-up showing two button: `I'm not okay` or `I'm fine` (including the flashing camera light and sound alarm). 

If you wait up to 20 seconds or you click on the `I'm not okay` button, the app will send the Emergency MQTT message.

In the other case, the app will open the quiz pop-up, where you have to answers to your personal question. This service is meant to be sure that you has not a loss of consciusness.

If the answer is wrong or you press the `SOS` button, the app will send the Emergency MQTT message.

In every pop-up there's also a `PHONE ICON` that calls automatically the 112.

Every pop-up will be showed also if the phone is locked or you are using another application.

## Crash Detection Algorithm:

Continuously monitors motion patterns using built-in sensors.
Detects abnormal deceleration, sudden stops, or high-impact movements.

Every 500ms the process reads the accellerometers value and calculates each magnitude. 
When the last magnitude exceeds an `CRASH_THRESHOLD` and the current magnitude is lower 
than a `BREAK_TRESHOLD` (istant break after 500ms) the algorithm detects a crash.

## MQTT server

Application has MQTT functionalities that allows it to act both as publisher and subscriber.

Under the conditions written before, the application can send an Emergency MQTT message on a specific topic called `hospital/emergencies`.

At the same time, app is designed to always listen for messages coming from the `hospital/dispatch` topic.

If you setup everything like mentioned in the **Getting Started** section, MQTT workflow will be the following:
1. App detect crash and user select option that lead to sending an Emergency MQTT message -> message is sent and user receives notification
2. Hospital mqtt server (simulated by the python script) receive that message and after 10s it sends another message to the topic the application is subscribed to (telling him that ambulance is on its way)
3. App will receive that message and it will be displayed through a push notification

## ✅ Implemented Feauteres and ⏩️ Future enhancements
| Features            | State |
|:--------------------|:-----:|
| Crash Detector      |   ✅   |
| Location            |   ✅   |
| MQTT service        |   ✅   |
| Shared Preferences  |   ✅   |
| JSON parsing        |   ✅   |
| Background service  |   ✅   |
| Wake and pop-up     |   ✅   |
| Text-to-speech      |   ⏩️   |
| Watch-os            |   ⏩️   |
| Database upgrande   |   ⏩️   |
| MQTT upgrade        |   ⏩️   |
| AI crash detector   |   ⏩️   |

## Disclaimer
This is a student project made by Antonio Franzoso, Ana Daria Corpaci and Stefano Morano for the Mobile Programming course in Universidad Politecnica de Madrid.

Ride safely with Ride SOS. 🚴‍♂️🚗🛵
