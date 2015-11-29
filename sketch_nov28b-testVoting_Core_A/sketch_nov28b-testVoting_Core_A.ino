//Test voting array
//A

//read from core from serial port of other two core
//data array from different boards
char Bdata[32];
char Adata[32];

boolean getMaindata = false;


void setup() {
    
    //serial comms startup
    Serial.begin(9600);
    Serial1.begin(9600); //A comms Trill
    Serial3.begin(9600); //A comms B
    
}

void loop() {
    Serial.print("going to call voting array\n");
    votingArray();
}

void votingArray(){
    //delay(5000);
    //Read data from Trill
    Serial.print("going to call getSignalData\n");
    getSignalData();

    if (getMaindata){
    //delay(5000);
    Serial.print("going to write to B\n");
    
    writeB();
    
    delay(200);
    
    Serial.print("going to read from B\n");
    readB(); //reading B from serial}
}}

//Gets signal
void getSignalData(){
    Serial.print("Got data from Main:\n");
    int a=0;
    getMaindata=false;
    while(Serial1.available()>0){
      Serial.print("got into loop \n");
        Adata[a]==Serial1.read();
        Serial.print(Adata[a]);
        a++;
        getMaindata=true;
        Serial.print(getMaindata);}
        Serial.print(getMaindata);
    Adata[a]='\0';
}

//A writes to B
void writeB(){
    Serial.print("writing to B\n");
    Serial3.write(Adata);
}

//Get B's data
void readB(){
    int b=0;
    Serial.print("Getting B's data:\n");
    while (Serial3.available() > 0){
        
        Bdata[b] = Serial3.read();
        b++;}
    Bdata[b] = '\0';
}

