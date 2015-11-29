//Test voting array
//A 

//read from core from serial port of other two core
//data array from different boards
char Bdata[32];
char Adata[32];


void setup() {
 
   //serial comms startup
  Serial.begin(9600);
  Serial1.begin(9600); //A comms Trill
  Serial2.begin(9600); //A comms B

}

void loop() {
  Serial.print("going to call voting array");
  votingArray();
}

void votingArray(){
delay(5000);
//Read data from Trill
Serial.print("going to call getSignalDatat");
 getSignalData();
 
delay(5000);
Serial.print("going to write to B");

writeB();

delay(200);

Serial.print("going to read from B");
readB(); //reading B from serial
}

//Gets signal
void getSignalData(){
  Serial.print("Got data from Main:");
  int a=0;
while(Serial1.available()>0){  
  Adata[a]==Serial1.read();
  Serial.print(Adata[a]);
  a++;}
  Adata[a]='\0';
  }

//A writes to B
void writeB(){
      Serial.print("writing to B");   
      Serial2.write(Adata);
    }

//Get B's data
void readB(){
  int b=0;
  Serial.print("Getting B's data:");
  while (Serial2.available() > 0){ 

    Bdata[b] = Serial2.read();
    b++;}    
    Bdata[b] = '\0';    
   }

