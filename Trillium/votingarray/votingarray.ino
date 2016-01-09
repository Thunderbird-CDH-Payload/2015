// testing voting array 
// This code is used on all three arduinos.
// Default serial (Serial) is used to debug the code on pc, can remove all code involving it.

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
#define END_CHAR 'Z'

// Buffers for storing received data from the other Arduinos
char Adata[BUFFER_SIZE];
char Bdata[BUFFER_SIZE];
char Cdata[BUFFER_SIZE];

int ac = 0;
int ab = 0;

int Breset = 12;
int Creset = 13;

void setup() {
  Serial.begin(SERIAL_RATE);  
  Serial1.begin(SERIAL_RATE);
  Serial2.begin(SERIAL_RATE);
  Serial3.begin(SERIAL_RATE);

  pinMode(Breset, OUTPUT);
  pinMode(Creset, OUTPUT);

  digitalWrite(Breset, LOW);
  digitalWrite(Creset, LOW);  
}

void loop() {
  // call the voting array function
  votingArray();
}

void votingArray(){
  
  // clear all three buffers
  clearArray(Adata, BUFFER_SIZE);
  clearArray(Bdata, BUFFER_SIZE);
  clearArray(Cdata, BUFFER_SIZE);
  
  // check if there is serial data that has been received from the host
  if (getSignalData()){
    
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
