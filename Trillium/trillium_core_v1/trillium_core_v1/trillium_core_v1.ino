/*
TRILLIUM CORE V1


UBC Orbit
Thunderbird Satellite
October 2015

Creative Commons License CC-BY-3.0 
https://creativecommons.org/licenses/by/3.0/


Project lead:
Sebastian Cline
  cline.sebastian@gmail.com
  teammanager@ubcorbit.com


Purpose:
Software for controlling radiation redundency in the Trillium architechture. Code designed for implementation on ATmega2560 chips operating at 16MHz.
*/
/*

*/

//Libraries
#include "TimerOne.h"
#include "TimerThree.h"
#include "avr/interrupt.h"
#include "avr/power.h"
#include "avr/sleep.h"


//Pin declarations here
const int chipRestart = 4; //sends out call for reset of other chip os cycles
int resetOSCyclePin = 2; //recieve pin of interupt to reset chip os cycle  //change pin of this
int wakeCorePin = 8; //jump wake external interupt for exiting sleep mode //call this when battery is good //changed pin number
int input=5; //temporary input value for voting array pin
int comAC=2; //compared value of A and C
int comAB=3; //compared value of A and B

//read from core from serial port of other two core
//data array from different boards
int Bdata[32]; //change length later
int Cdata[32];
int Adata[32];




/*
variables for general system use
*/

volatile int OSCycle = 0; //number of cycles of the OS, keeps things running in check, allows for synchronized reset
boolean sleepMode = false; //determines sleep mode access
int sleepDepth;  //the depth of the core sleep (power saving measures) (needed to make it global)

//characteristic variables, can change behaviours
const int OSCycleCap = 10000; //number of OS cycles before roll-over




void setup(){
  pinMode(chipRestart, OUTPUT);  
  pinMode(resetOSCyclePin, INPUT);
  pinMode(wakeCorePin, INPUT);
  
  pinMode(input,INPUT);  //input to this core
  pinMode(comAC,INPUT);  //input to nand for C
  pinMode(comAB,INPUT);  //input to nand for B  
  
//external interupt reset os loop
  attachInterrupt(digitalPinToInterrupt(resetOSCyclePin), resetOSCycle, FALLING);
  attachInterrupt(digitalPinToInterrupt(wakeCorePin), wakeCore, CHANGE);   

//internal interrupts
//still need to determine how often interrupt runs
  Timer1.initialize();  
  Timer3.initialize();
  
//serial comms startup
  Serial1.begin(28800); //comms to other core
  Serial2.begin(28800); //comms to other core
  
//Internal interrupts
  Timer1.attachInterrupt(startUpSync);  //sync them 
  Timer3.attachInterrupt(T3interrupt);

//startup functions (run once)
  startUpSync();  
}


void loop(){  

 sleepDepth = 0; //just intially setting it to being awake
  
  
  //internal clock check of the analog current sensor for consumption of other chips
  
  //internal clock triggered serial 
  
  
  //OS cycle
  while(OSCycle<OSCycleCap){
    if (!sleepMode){
      sleepDepth=chooseSleepMode(); //checking to see if satellite should go to sleep      
      OSCycle++;      
    }      
                           }  
  
  //sleep mode cycle, "wakes up" trillium core periodically to check systems, send ping or otherwise, requires an internal or external interupt to wake
   if (sleepMode){
    //internal clock setup


    //function call to check for wake up of the core, run sparingly, in function of sleep depth
   //do something as a function of depth of sleep  
    if (!(sleepDepth==0)){
      switch(sleepDepth) {
        case 1:
        //do something for sleepDepth of 1
        break;
        case 2:
        //do something for sleepDepth of 2
        break;
        case 3:
        //do something for sleepDepth of 3
        break;
        case 4:
        //do something for sleepDepth of 4
        break;
        case 5:
        //do something for sleepDepth of 5
        break;
        default:
        break;        
                       }
                       }
    
                }
    
                
 
  
}


//checks which sleep mode to go into, checked periodically with main loop
int chooseSleepMode(){
  //things to verify:
  //get how much power is availble to determine which sleep mode to go into
   
  //first mode 
  if(1){
    setSleepMode(1);
    return sleepDepth;  
  }

  //second mode
  else if(2){
    setSleepMode(2);
    return sleepDepth;
  }  

  //thrid mode
  else if(3){
    setSleepMode(3);
    return sleepDepth;
  }  

  //fourth mode
  else if(4){
    setSleepMode(4);
    return sleepDepth;
  }

  else if (5){
    setSleepMode(5);
    return sleepDepth;}

  else{
    setSleepMode(0);
    return sleepDepth;
  }  
}


//sync the trillium chips upon a restart
void startUpSync(){
  digitalWrite(chipRestart, HIGH); 
}


//resets the OS cycle number, restarting the OS cycle
void resetOSCycle(){
  OSCycle=0;
}

//wakes the core, starts the OSCycle again
void wakeCore(){
  sleep_disable();   
  sleepMode=false; 
  startUpSync();
}

//setting sleepMode
void setSleepMode(int a){
  //case where you don't need to sleep
  if (a==0){
    sleepDepth=0;
    sleepMode=false;
    return;}
  else if (a==1){
    sleepDepth=1;
    set_sleep_mode(SLEEP_MODE_IDLE);}
  else if (a==2){
    sleepDepth=2;
    set_sleep_mode(SLEEP_MODE_ADC);}
  else if (a==3){
    sleepDepth=3;
    set_sleep_mode(SLEEP_MODE_PWR_SAVE);}
  else if (a==4){
    sleepDepth=4;
    set_sleep_mode(SLEEP_MODE_STANDBY);}
  else if (a==5){
      sleepDepth=5;
      set_sleep_mode(SLEEP_MODE_PWR_DOWN);}  
  sleep_enable();
  sleep_mode();
  sleepMode=true;   //sleepMode is on
  }


//internal interrupt methods  stub

void T3interrupt(){
  

  //fn calls to various methods that need to be called regularly - still need to determine
  
  }


//voting array
int voteingArray(){

//reading value of A from digitalPin while theres something to read
int a=0;
  while (digitalRead(input)){
    
    if(digitalRead(input)==-1)
      break;
      else{
    Adata[a]=digitalRead(input);
    //write this value to the other boards
    
    
    a++;}     
    }

delay(200);
//writes this boards data to other boards

writeB();
writeC();
delay(200);


    
//reading B from serial
if (Serial1.available() > 0){ 
  int bAvail=Serial1.available();
  for(int i=0; i<bAvail; i++){
    Bdata[i] = Serial1.read();}           
   }
                            
//reading C from serial
if (Serial2.available() > 0){ // Don't read unless
  int cAvail=Serial2.available();
  for(int i=0; i<cAvail; i++){
   Cdata[i] = Serial2.read();}
   }

delay(200);

//comparing A and B's data
int j=0;
int AB;
while(j<32){
  if (Adata[j]==Bdata[j]){
    AB=0;}
    else{AB=1;
    j=0;
    break;}
  }

//comparing A and C's data
int f=0;
int AC;
while(f<32){
  if (Adata[f]==Cdata[f]){
    AC=0;}
    else{AC=1;
    break;}
  }

//sending vote for B
digitalWrite(AB,comAB);
//sending vote for C
digitalWrite(AC,comAC);  
//resetting done by hardware

  }


//A writes to B
void writeB(){
  int i=0;
  while (i<32){    
      Serial1.write(Adata[i]);
      i++;
      }}

//A writes to C
void writeC(){
  int i=0;
  while (i<32){    
      Serial2.write(Adata[i]);
      i++;
      }}

 
  



  

