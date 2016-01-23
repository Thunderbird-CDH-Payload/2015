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

// This code is used on all three arduinos.
// Default serial (Serial) is used to debug the code on pc, can remove all code involving it.

//Libraries
#include "TimerOne.h"
#include "TimerThree.h"
#include "avr/interrupt.h"
#include "avr/power.h"
#include "avr/sleep.h"

//*** CHANGE THIS NUMBER BEFORE UPLOADING SKETCH ***
#define ARDUINO_ID 3

//***VOTING FN USE

#define TRUE 1
#define FALSE 0

// number of milliseconds to wait before acquiring data from host
#define WAIT_TIME 50
// number of milliseconds to wait after resetting the Arduino
#define RESET_TIME 200

// size of the character buffers
#define BUFFER_SIZE 64

// serial rate
#define SERIAL_RATE 115200

// These characters are appended to the beginning and end of each string sent from the host
// e.g. "AA0000000000000Z" in this case
#define CHAR1 'A'
#define CHAR2 'A'
#define SECOND_CHAR 'Y' // this must be different than END_CHAR
#define END_CHAR 'Z'

//*** ERROR FN USE
int errMode=FALSE;
int errNum = 0;
int errbit = 0;


//Pin declarations here
const int chipRestart = 4; //sends out call for reset of other chip os cycles
int resetOSCyclePin = 2; //recieve pin of interupt to reset chip os cycle  //change pin of this
int wakeCorePin = 8; //jump wake external interupt for exiting sleep mode //call this when battery is good //changed pin number

int Breset = 12; // pin to reset logic for MOSFET for Arduino B
int Creset = 13; // pin to reset logic for MOSFET for Arduino C

// Variables for voting array use
// Buffers for storing received data from the other Arduinos
char Adata[BUFFER_SIZE];
char Bdata[BUFFER_SIZE];
char Cdata[BUFFER_SIZE];

int ac = 0;
int ab = 0;

long randNum;

//***VOTING FN USE

//variables for general system use
volatile int OSCycle = 0; //number of cycles of the OS, keeps things running in check, allows for synchronized reset
boolean sleepMode = false; //determines sleep mode access
int sleepDepth;  //the depth of the core sleep (power saving measures) (needed to make it global)

//characteristic variables, can change behaviours
const int OSCycleCap = 10000; //number of OS cycles before roll-over


void setup() {
  randomSeed(100);  //random number generator 
  
  Serial.begin(SERIAL_RATE);  
  Serial1.begin(SERIAL_RATE);
  Serial2.begin(SERIAL_RATE);
  Serial3.begin(SERIAL_RATE);

  pinMode(Breset, OUTPUT);
  pinMode(Creset, OUTPUT);

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
  
  //Internal interrupts
  Timer1.attachInterrupt(startUpSync);  //sync them 
  Timer3.attachInterrupt(T3interrupt);

  //startup functions (run once)
  startUpSync();  

  digitalWrite(Breset, LOW);
  digitalWrite(Creset, LOW);  
}

void loop() {
  
  sleepDepth = 0; //just intially setting it to being awake
  
  // call the voting array function
  votingArray();
  
  //OS cycle
  while(OSCycle < OSCycleCap){
    if (!sleepMode){
      sleepDepth = chooseSleepMode(); //checking to see if satellite should go to sleep      
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
          // default case
          break;        
      }
    }  
  }
}

void votingArray(){
  
  // clear all three buffers
  clearArray(Adata, BUFFER_SIZE);
  clearArray(Bdata, BUFFER_SIZE);
  clearArray(Cdata, BUFFER_SIZE);
  
  // check if there is serial data that has been received from the host
  if (getSignalData()){    
    // if you dont want to operate in error mode, just comment out this line:
    checkError();
    
    if (errbit>0) {
      simulateError(errNum);
    }
            
    else {    
      // write the received data from host to the other 2 Arduinos
      writeOthers();
      delay(WAIT_TIME);
      
      // read data from other 2 arduinos
      readB();
      readC();
      
      // compare data with arduino B
      ab = different(Adata, Bdata, BUFFER_SIZE);
      Serial.print("AB Compare: ");
      Serial.println(ab);
      
      // compare data with arduino C
      ac = different(Adata, Cdata, BUFFER_SIZE);
      Serial.print("AC Compare: ");
      Serial.println(ac);   
      
      // if there is a difference between the data received from the other arduions, trigger reset logic
      if (ab){
        digitalWrite(Breset, HIGH);
      }
      if (ac){
        digitalWrite(Creset, HIGH);
      }
      if (ab || ac){
        delay(RESET_TIME); // give time for the arduinos to reset
      }
      
      // drive reset pins back low
      digitalWrite(Breset, LOW);
      digitalWrite(Creset, LOW);
    }
    sendDataToMain(); //SENDING TO MAIN  

  
  }
  
}

// this function reads the data from the host
int getSignalData(){
  delay(WAIT_TIME);
  clearArray(Adata, BUFFER_SIZE);
  int m=0;
  while(Serial1.available() > 0){
    char temp = Serial1.read();
    Adata[m] = temp;
    m++;
    if (temp == END_CHAR)
      break;
  }
  Serial1.flush();
  Adata[m]='\0';
  
  // Adata[4] = 'B';
  if (Adata[0] == CHAR1 && Adata[1] == CHAR2){
    Serial.print("Got Data from main: ");
    Serial.println(Adata); //printing to screen
    
    return TRUE;
  }
  return FALSE;
}

void writeOthers(){
  Serial1.print(Adata);
}

// this function reads the data from the serial connected to Arduino B
int readB(){
  clearArray(Bdata, BUFFER_SIZE);
  int m=0;
  while(Serial2.available() > 0){
    char temp = Serial2.read();
    Bdata[m] = temp;
    m++;
    if (temp == END_CHAR)
      break;
  }
  // clear the serial buffer.
  Serial2.flush();
  Bdata[m]='\0';
  if (Bdata[0] == CHAR1 && Bdata[1] == CHAR2){
    Serial.print("Got Data from B: ");
    Serial.println(Bdata); //printing to screen
    return TRUE;
  }
  return FALSE;
}

// this function reads the data from the serial connected to Arduino C
int readC(){
  clearArray(Cdata, BUFFER_SIZE);
  int m=0;
  while(Serial3.available() > 0){
    char temp = Serial3.read();
    Cdata[m] = temp;
    m++;
    if (temp == END_CHAR)
      break;
  }
  // clear the serial buffer.
  Serial3.flush();
  Cdata[m]='\0';
  if (Cdata[0] == CHAR1 && Cdata[1] == CHAR2){
    Serial.print("Got Data from C: ");
    Serial.println(Cdata); //printing to screen
    return TRUE;
  }
  return FALSE;
}

// This function will return true if the string a1 and a2 are different, else false
int different(char* a1, char* a2, int n){
  for(int i = 0; i < n; i++){
    if (a1[i] != a2[i])
      return TRUE;
  }
  return FALSE;
}

// This function writes null bytes to the buffer array passed to it.
void clearArray(char* a, int n){
  for(int i = 0; i < n; i++){
    a[i] = '\0';
  } 
}

void sendDataToMain(){
    Serial2.print(ARDUINO_ID);
    Serial2.print(errNum);
    Serial.print("Arduino id and errnum:");
    Serial.print(ARDUINO_ID);
    Serial.println(errNum);

  if (!errMode){
  int i = 0;
    while(i < BUFFER_SIZE){
      if (Adata[i] != NULL){
        Serial2.print(Adata[i]);
        i++;
      }
      else
        break;
    }
    //send its vote to main
    Serial2.print("AB and AC vote: ");
    Serial2.print(ab);
    Serial2.println(ac);
  }
}
  

/*
  Helper functions for Trilium Core to carry out its operations
*/

//checks which sleep mode to go into, checked periodically with main loop
int chooseSleepMode(){
  //things to verify:
  //get how much power is availble to determine which sleep mode to go into
  
  
  /*
    TODO: FIX THE LOGIC FOR THE CONDIITONAL STATEMENTS HERE, if(1) means first statement will ALWAYS execute and all other logic is IGNORED...
    replace someVar with the variable you want to compare the logic with
  */
  int someVar = 0;
  //first mode 
  if(someVar == 1){
    setSleepMode(1);
    return sleepDepth;  
  }

  //second mode
  else if(someVar == 2){
    setSleepMode(2);
    return sleepDepth;
  }  

  //thrid mode
  else if(someVar == 3){
    setSleepMode(3);
    return sleepDepth;
  }  

  //fourth mode
  else if(someVar == 4){
    setSleepMode(4);
    return sleepDepth;
  }

  else if (someVar == 5){
    setSleepMode(5);
    return sleepDepth;
  }
  else{
    setSleepMode(0);
    return sleepDepth;
  }
  
  //reset OSCycle
  resetOSCycle();
  
}


//sync the trillium chips upon a restart
void startUpSync(){
  digitalWrite(chipRestart, HIGH); 
}


//resets the OS cycle number, restarting the OS cycle
void resetOSCycle(){
  OSCycle = 0;
}

//wakes the core, starts the OSCycle again
void wakeCore(){
  sleep_disable();   
  sleepMode = false; 
  startUpSync();
}

//setting sleepMode
void setSleepMode(int a){
  //case where you don't need to sleep
  if (a == 0){
    sleepDepth = 0;
    sleepMode = false;
    return;
  }
  else if (a == 1){
    sleepDepth = 1;
    set_sleep_mode(SLEEP_MODE_IDLE);
  }
  else if (a == 2){
    sleepDepth = 2;
    set_sleep_mode(SLEEP_MODE_ADC);
  }
  else if (a == 3){
    sleepDepth = 3;
    set_sleep_mode(SLEEP_MODE_PWR_SAVE);
  }
  else if (a == 4){
    sleepDepth = 4;
    set_sleep_mode(SLEEP_MODE_STANDBY);
  }
  else if (a == 5){
    sleepDepth = 5;
    set_sleep_mode(SLEEP_MODE_PWR_DOWN);
  }  
  sleep_enable();
  sleep_mode();
  sleepMode = true;   //sleepMode is on
}


//internal interrupt methods  stub

void T3interrupt(){
  //fn calls to various methods that need to be called regularly - still need to determine  
}

// *** ERROR FUNCTIONS ***
//only changes data recevied from host
//INPUT=type of error
void simulateError(int e){
  switch(e) {
    case 1:
      Adata[errbit]=~(Adata[errbit]);
      Serial.println("Bit flip");
      break;
    case 2:
      Serial.println("Latch-up");
      for (int i =0; i< BUFFER_SIZE -1; i++){
        Adata[i]=(char) 255;}
      break;
    case 3:
      Serial.println("Random");
      randNum=(random(300) % 3) + 1;  //covers all cases
      simulateError(randNum);      
      break;
    default:
      Serial.println("Invalid errorcode");
      break;
  }
  Serial.print("Error bit: ");
  Serial.println(errbit);
}

void checkError(){
  if (Adata[2] == 'E'){
    errMode = TRUE;    //indicates to fn in error mode
    errNum = (int) Adata[2 + ARDUINO_ID] - '0';
    errbit = (int) Adata[6] - 32;
  }
  else {
    errMode = FALSE; //means just a regular msg is being sent 'normal mode'
  } 
  
}
