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

### Installation
Download Ride SOS APK from GitHub and install it on your android device.

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

## Running MQTT server

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
