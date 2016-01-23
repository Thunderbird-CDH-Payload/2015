//Main demo version
//Based on user input send data to three boards and have them send back what they received

#define WAIT_TIME 1000
#define BUFFER_SIZE 64
char errMode;

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
    if(Serial.available()>0){
      Serial.print("in loop\n");
      delay(WAIT_TIME);
        readInput();
        errMode=msg[0];  //indicates if its in error mode
        Serial.print("done reading input\n");
        sendInput();
      Serial.println("Main's message: \n");
      Serial.println(msg);
        receiveResponse(); //might need to include timer here **check after testing if needed
        Serial.print("done receiving response\n");
        if (errMode=='E'){
          outputErrRes();}
          else {
        outputResponse();}
        
      clearArray(msg,BUFFER_SIZE);
      clearArray(Adata,BUFFER_SIZE);
      clearArray(Bdata,BUFFER_SIZE);
      clearArray(Cdata,BUFFER_SIZE);
    }
}

void readInput(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial.available()>0){
            msg[i]=Serial.read();
            i++;   //only increase i when its read something
        }
        else {break;}
    }
}

//needed only once cause theyll be connected by the same line
void sendInput(){
    Serial1.print("AA");  //indicates to boards that its the start of the msg
    int i=0;
    while(i<BUFFER_SIZE){
        if (!(msg[i]==NULL)){
            Serial1.print(msg[i]);
            i++;}
        else {break;}
    }
    Serial1.print("ZZ");  //indicates to boards that its the end of the msg 
    
}

void receiveResponse(){
    while (!((Serial1.available()>0) && (Serial2.available()>0) && (Serial3.available()>0))){
        //loop around till theres something to read from all of them
        //still gotta take of the case where a board didnt get anything or failed to send back - should add some sort of timer **
    }
    delay(WAIT_TIME);
    readA();
    readB();
    readC();  
    
}

//**change depending on how boards response smg will be formated
void outputResponse(){
    Serial.println("Response from A: \n");
    Serial.println(Adata);
   
    Serial.println("Response from B: \n");
    Serial.println(Bdata);
    
    Serial.println("Response from C: \n");
    Serial.println(Cdata);
}

void readA(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial1.available()>0){
            Adata[i]=Serial1.read();
            i++;
        }
        else{break;}
    }
}

void readB(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial2.available()>0){
            Bdata[i]=Serial2.read();
            i++;
        }
        else {break;}
    }
}

void readC(){
    int i=0;
    while (i<BUFFER_SIZE){
        if (Serial3.available()>0){
            Cdata[i]=Serial3.read();
            i++;
        }
        else {break;}
    }
}

void clearArray(char* a, int n){
  for(int i = 0; i < n; i++){
    a[i] = '\0';
  } 
}


//error response
//not recognizing which board has the error
void outputErrRes(){
  int Aerr = (int) Adata[1] -'0';
  int Berr = (int) Bdata[1] -'0';
  int Cerr = (int) Cdata[1] -'0';
  if (Aerr>0){
    Serial.print("A board got error: \n");
    Serial.print(Adata[1]);
    Serial.print("\n");}
    else {
      Serial.print("A is in normal mode\n");}
  if (Berr>0){
    Serial.print("B board got error:\n");
    Serial.print(Bdata[1]);
    Serial.print("\n");}
    else {
      Serial.print("B is in normal mode\n");}
  if (Cerr>0){
    Serial.print("C board got error:\n");
    Serial.print(Cdata[1]);
    Serial.print("\n");}
    else {
      Serial.print("C is in normal mode\n");}
  }

    



