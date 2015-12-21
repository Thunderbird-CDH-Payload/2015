//testing voting array 
//CORE A

#define TRUE 1
#define FALSE 0

#define WAIT_TIME 50

#define BUFFSIZE 64

#define SERIAL_RATE 115200

#define CHAR1 'A'
#define CHAR2 'A'
#define END_CHAR 'Z'

char Adata[BUFFSIZE];
char Bdata[BUFFSIZE];
char Cdata[BUFFSIZE];

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
  votingArray();
}

void votingArray(){
  clearArray(Adata, BUFFSIZE);
  clearArray(Bdata, BUFFSIZE);
  clearArray(Cdata, BUFFSIZE);
  if (getSignalData()){
    writeOthers();
    delay(WAIT_TIME);
    readB();
    readC();
    ab = different(Adata, Bdata, BUFFSIZE);
    Serial.print("AB Compare: ");
    Serial.println(ab);
    ac = different(Adata, Cdata, BUFFSIZE);
    Serial.print("AC Compare: ");
    Serial.println(ac);   
    
    if (ab){
      digitalWrite(Breset, HIGH);
    }
    if (ac){
      digitalWrite(Creset, HIGH);
    }
    if (ab || ac){
      delay(200); // give time for the arduinos to reset 
    }
    
    digitalWrite(Breset, LOW);
    digitalWrite(Creset, LOW);
  } 
}

int getSignalData(){
  delay(WAIT_TIME);
  clearArray(Adata, BUFFSIZE);
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

int readB(){
  clearArray(Bdata, BUFFSIZE);
  int m=0;
  while(Serial2.available() > 0){
    char temp = Serial2.read();
    Bdata[m] = temp;
    m++;
    if (temp == END_CHAR)
      break;
  }
  Serial2.flush();
  Bdata[m]='\0';
  if (Bdata[0] == CHAR1 && Bdata[1] == CHAR2){
    Serial.print("Got Data from B: ");
    Serial.println(Bdata); //printing to screen
    return TRUE;
  }
  return FALSE;
}

int readC(){
  clearArray(Cdata, BUFFSIZE);
  int m=0;
  while(Serial3.available() > 0){
    char temp = Serial3.read();
    Cdata[m] = temp;
    m++;
    if (temp == END_CHAR)
      break;
  }
  Serial3.flush();
  Cdata[m]='\0';
  if (Cdata[0] == CHAR1 && Cdata[1] == CHAR2){
    Serial.print("Got Data from C: ");
    Serial.println(Cdata); //printing to screen
    return TRUE;
  }
  return FALSE;
}

int different(char* a1, char* a2, int n){
  for(int i = 0; i < n; i++){
    if (a1[i] != a2[i])
      return TRUE;
  }
  return FALSE;
}

void clearArray(char* a, int n){
  for(int i = 0; i < n; i++){
    a[i] = '\0';
  } 
}
