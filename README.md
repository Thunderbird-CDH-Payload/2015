# UBC Orbit CDH-Payload Repo
UBC Orbit CDH team repo for 2015-2016  
WE MADE A THING FOR SPAAAACE  
This repo contains the code for UBC Orbit's triple redundancy Trillium main processor and code to manipulate the 
Android phone that we will have on the satellite. 

# Android App
-Will run on boot
-Uses Intents for IPC with Arduino Communicator

# For communicating with Arduino (Android)
Edit
\2015\Android\CDHprototype2\app\src\main\java\com\example\aschere\cdhprototype2\DataFromArduinoReceiver.java  
And add a switch case with a byte representing an instruction to the Arduino. (Sorta like opcode)
