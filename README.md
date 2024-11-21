# Ride SOS - Crash Detection & Emergency Alert App
## Overview
Ride SOS is a life-saving smartphone application designed to detect accidents and notify emergency contacts in real time. Whether you're a motorcyclist, cyclist, or driver, Ride SOS provides an added layer of safety during your travels by using advanced sensors in your smartphone to detect crashes and respond quickly.

## Features
### 🚨 Crash Detection
Utilizes your smartphone’s accelerometer, gyroscope, and GPS sensors to detect sudden impacts or abnormal movements associated with crashes.
Configurable sensitivity settings to suit different vehicles and riding conditions.
### 📡 Emergency Alerts
Automatically sends an SOS message to your emergency contacts upon detecting a crash.
Includes real-time location (GPS coordinates) in the alert for accurate assistance.
### ⏳ Countdown Timer for False Alarms
Allows a brief countdown after detecting a crash, giving users time to cancel the alert if the detection is false.
Customizable timer duration to fit user needs.
### 📍 Real-Time Location Sharing
Shares your trip progress and current location with trusted contacts for added safety.
Works even in remote areas with minimal network coverage.

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

## Disclaimer
This is a student project made by Antonio Franzoso, Ana Daria Corpaci and Stefano Morano for the Mobile Programming course in Universidad Politecnica de Madrid

Ride safely with Ride SOS—your trusted companion on the road. 🚴‍♂️🚗🛵
