//Main demo version
//Based on user input send data to three boards and have them send back what they received


int BUFFER_SIZE = 64;
char msg[BUFFER_SIZE];
char Adata[BUFFER_SIZE];
char Bdata[BUFFER_SIZE];
char Cdata[BUFFER_SIZE];

void setup() {
    Serial.begin(115200);
    while(!Serial){ //waiting for serial to connect
    }
    Serial1.begin(115200);
    while(!Serial1){ //waiting for serial to connect
    }
    Serial2.begin(115200);
    while(!Serial2){ //waiting for serial to connect
    }
    Serial3.begin(115200);
    while(!Serial3){ //waiting for serial to connect
    }
}

void loop() {
    if(Serial.available>0){
        readInput();
        sendInput();
        receiveResponse(); //might need to include timer here **check after testing if needed
        outputResponse();
    }
}

void readInput(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial.available()>0){
            msg[i]=Serial.read;
            i++;   //only increase i when its read something
        }
    }
}

void sendInput(){
    Serial.print("AA");  //indicates to boards that its the start of the msg
    int i=0;
    while(i<BUFFER_SIZE){
        if (!(msg[i]=="\0")){
            Serial.print(msg[i]);
            i++;}
        else {break;}
    }
    Serial.print("ZZ");  //indicates to boards that its the end of the msg 
    
}

void receiveResponse(){
    while !((Serial1.available()>0) && (Serial2.available()>0) && (Serial3.available()>0)){
        //loop around till theres something to read from all of them
        //still gotta take of the case where a board didnt get anything or failed to send back - should add some sort of timer **
    }
    readA();
    readB();
    readC();
}

void outputResponse(){
    Serial.println(Adata);
    Serial.println(Bdata);
    Serial.println(Cdata);
}

void readA(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial1.available()>0){
            Adata[i]=Serial1.read;
            i++;
        }
    }
}

void readB(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial2.available()>0){
            Bdata[i]=Serial2.read;
            i++;
        }
    }
}

void readC(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial3.available()>0){
            Cdata[i]=Serial3.read;
            i++;
        }
    }
}
    
}

