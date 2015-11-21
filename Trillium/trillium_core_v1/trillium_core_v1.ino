/*
TRILLIUM CORE V1

QQQQQQQQQ

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
Pin declarations here
*/

#include "TimerOne.h"
#include "TimerThree.h"
#include "avr/interrupt.h"
#include "avr/power.h"
#include "avr/sleep.h"

const int chipRestart = 4; //sends out call for reset of other chip os cycles
int resetOSCyclePin = 2; //recieve pin of interupt to reset chip os cycle  
int wakeCorePin = 3; //jump wake external interupt for exiting sleep mode //call this when battery is good


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
      //include all internal interrupts
      Timer1.attachInterrupt(startUpSync);
      Timer3.attachInterrupt(T3interrupt);
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
  
  // change in to a switch statement, as this first conditional statement will always run due to if(1)
  if(1){
    setSleepMode(1);
    return sleepDepth;  //not sure ifs it just better to return a int 
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
  sleep_disable();   //can you put this here?
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

