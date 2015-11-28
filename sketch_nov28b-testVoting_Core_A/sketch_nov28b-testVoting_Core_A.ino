//Test voting array
//A 


//read from core from serial port of other two core
//data array from different boards
char Bdata[32];
//int Cdata[32];
char Adata[32];


//int comAC=2; //compared value of A and C
int comAB=3; //compared value of A and B


//just for testing


void setup() {
  //just for testing
  pinMode(12,OUTPUT); //light for B
//  pinMode(13,OUTPUT); //light for 
  
  // put your setup code here, to run once:
//  pinMode(comAC,INPUT);  //input to nand for C
  pinMode(comAB,INPUT);  //input to nand for B 

  //serial comms startup
  Serial1.begin(28800); //A comms Trill
  Serial2.begin(28800); //A comms B
//  Serial3.begin(28800); //A comms C
}

void loop() {
  // put your main code here, to run repeatedly:
  votingArray();

}


//if theres data is going to be sent  call this
//TODO check if there is data needed to be compared
int votingArray(){

//Read data from Trill
 getSignalData();
 
delay(200);

writeB();
//writeC();
delay(200);


readB(); //reading B from serial
//readC(); //reading from C

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

/*
//comparing A and C's data
int f=0;
int AC;
while(f<32){
  if (Adata[f]==Cdata[f]){
    AC=0;}
    else{AC=1;
    break;}
  }
  */

//sending vote for B
digitalWrite(AB,comAB);
//turn on light for B
if (comAB){
  digitalWrite(12,HIGH);}
  else{
    digitalWrite(12,LOW);}
}

/*
//sending vote for C
digitalWrite(AC,comAC);  
//resetting done by hardware
if (comAC){
  digitalWrite(13,HIGH);}
  else{
    digitalWrite(13,LOW);}
  }
*/

//A writes to B
void writeB(){
//  int i=0;
//  while (i<32){    
      Serial2.write(Adata[i]);
//      i++;
//      }
    }
/*
//A writes to C
void writeC(){
  int i=0;
  while (i<32){    
      Serial2.write(Adata[i]);
      i++;
      }}
*/
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

//Get B's data
void readB(){
  int b=0;
  Serial.println("Getting B's data:");
  while (Serial2.available() > 0){ 

    Bdata[b] = Serial2.read();
    b++;}    
    Bdata[b] = '\0';    
   }

  
/*
void readC(){
  if (Serial3.available() > 0){ // Don't read unless
  int cAvail=Serial3.available();
  for(int i=0; i<cAvail; i++){
   Cdata[i] = Serial3.read();}
   }
  }*/

