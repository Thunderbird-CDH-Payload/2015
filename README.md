# UBC Orbit CDH-Payload Repo
UBC Orbit CDH Team Repository for 2015-2016  
>*WE MADE A THING FOR SPAAAACE*  

This repo contains the code for UBC Orbit's triple redundancy Trillium main processor, and code to manipulate the 
Android phone that we will have on the satellite. 

## Trillium


## Android App
### Features
-Will run on boot (Thanks http://www.khurramitdeveloper.blogspot.ca/)  
-Uses Intents for IPC with Arduino Communicator  
-Uses Camera2 API to obtain RAW images (requires Android 5.0+ (SDK 21+))  
### For communicating with Arduino
Edit
\2015\Android\CDHprototype2\app\src\main\java\com\example\aschere\cdhprototype2\DataFromArduinoReceiver.java  
And add a switch case with a byte representing an instruction to the Arduino. (Sorta like opcode)
### TODO
See TODO under /Android/
