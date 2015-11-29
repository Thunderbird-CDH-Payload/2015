//Test voting array
//A 


//read from core from serial port of other two core
//data array from different boards
char Bdata[32];
char Adata[32];


void setup() {

  //serial comms startup
  Serial.begin(9600);
  Serial1.begin(9600); //B comms Trill
  Serial2.begin(9600); //B commes A

}

void loop() {
  // put your main code here, to run repeatedly:
  Serial.print("going to call voting array");
  votingArray();

}


//if theres data is going to be sent  call this
//TODO check if there is data needed to be compared
void votingArray(){
delay(5000);
//Read data from Trill
Serial.print("going to call getSignalDatat");
 getSignalData();
 
delay(5000);

Serial.print("going to write to B");

writeA();

delay(200);

Serial.print("going to read from B");
readA(); //reading A from serial


delay(200);
  }

  //Gets signal
void getSignalData(){
  int a=0;
  Serial.print("Got data from Main:"); 
while(Serial1.available()>0){ 
  Bdata[a]==Serial1.read();
  Serial.print(Bdata[a]);
  a++;}
  Bdata[a]='\0';
  }


//B writes to A
void writeA(){
    Serial.print("writing to A");   
      Serial2.write(Bdata);

}


//Get A's data
void readA(){
  int a=0;
  Serial.println("Getting A's data:");
  while (Serial2.available() > 0){ 

    Adata[a] = Serial2.read();
    a++;}    
    Adata[a] = '\0';    
  }


