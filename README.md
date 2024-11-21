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
Automatically sends an SOS message to the Hospital MQTT server containing the crash location and the information of the user.
### 📍 Ambulance calling
### ❓ Personal Quiz 


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


## Crash Detection Algorithm:

Continuously monitors motion patterns using built-in sensors.
Detects abnormal deceleration, sudden stops, or high-impact movements.

Every 500ms the process read the accellerometers value and calculate each magnitude. 
When the last magnitude exceed an `CRASH_THRESHOLD` and the current magnitude is lower 
than a `BREAK_TRESHOLD` (istant break after 500ms) the algorithm detect a crash.

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
This is a student project made by Antonio Franzoso, Ana Daria Corpaci and Stefano Morano for the Mobile Programming course in Universidad Politecnica de Madrid

Ride safely with Ride SOS—your trusted companion on the road. 🚴‍♂️🚗🛵
