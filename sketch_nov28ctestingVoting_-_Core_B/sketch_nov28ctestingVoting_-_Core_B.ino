//Test voting array
//A 


//read from core from serial port of other two core
//data array from different boards
int Bdata[32];
int Cdata[32];
int Adata[32];


int comBA=2; //compared value of B and C
//int comBC=3; //compared value of B and C


//just for testing


void setup() {
  //just for testing
  pinMode(12,OUTPUT); //light for A
//  pinMode(13,OUTPUT); //light for C
  
  // put your setup code here, to run once:
//  pinMode(comBC,INPUT);  //input to nand for A
  pinMode(comBA,INPUT);  //input to nand for C 

  //serial comms startup
  Serial1.begin(28800); //B comms Trill
  Serial2.begin(28800); //B commes A
//  Serial3.begin(28800); //B comms C
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

writeA();
//writeC();
delay(200);


readA(); //reading A from serial
//readC(); //reading from C

delay(200);

//comparing B and A's data
int j=0;
int BA;
while(j<32){
  if (Bdata[j]==Adata[j]){
    BA=0;}
    else{BA=1;
    j=0;
    break;}
  }
/*
//comparing B and C's data
int f=0;
int BC;
while(f<32){
  if (Bdata[f]==Cdata[f]){
    BC=0;}
    else{BC=1;
    break;}
  }
  */

//sending vote for B
digitalWrite(BA,comBA);
//turn on light for B
if (comBA){
  digitalWrite(12,HIGH);}
  else{
    digitalWrite(12,LOW);}

/*
//sending vote for C
digitalWrite(BC,comBC);  
//resetting done by hardware
if (comBC){
  digitalWrite(13,HIGH);}
  else{
    digitalWrite(13,LOW);}
  }
*/
}
//A writes to B
void writeA(){
  int i=0;
  while (i<32){    
      Serial2.write(Adata[i]);
      i++;
      }}
/*
//A writes to C
void writeC(){
  int i=0;
  while (i<32){    
      Serial2.write(Adata[i]);
      i++;
      }}*/

//Gets signal
void getSignalData(){
  int a=0;
while(Serial1.available()>0){  
  Bdata[a]==Serial1.read();
  Serial.print(Bdata[a]);
  a++;}
  }

//Get A's data
void readA(){
  if (Serial2.available() > 0){ 
  int aAvail=Serial2.available();
  for(int i=0; i<aAvail; i++){
    Adata[i] = Serial2.read();}           
   }
  }
/*
void readC(){
  if (Serial3.available() > 0){ // Don't read unless
  int cAvail=Serial3.available();
  for(int i=0; i<cAvail; i++){
   Cdata[i] = Serial3.read();}
   }
  }
  */

