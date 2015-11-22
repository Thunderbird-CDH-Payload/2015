#define BUFSIZE 128

char buffer[BUFSIZE];
int LEDpin = 13;
int trigPin = 4;
int index = 0;
int tempcount;

void setup() {
  pinMode(trigPin, OUTPUT);
  Serial.begin(9600);
  Serial3.begin(9600);
  Serial.println("ArdB");
}

void loop() {
  
  if(Serial3.available()){
    digitalWrite(trigPin, LOW);
    digitalWrite(LEDpin, LOW);
    Serial.println("Got signal");
    while (Serial3.available() > 0){
      char c = Serial3.read();
      Serial.print(c);
      buffer[index] = c;
      if (c == '*'){
        buffer[index+1] = '\0';
        break;
      } 
      index++;
    }
  }
  else {
    delay(500);
    digitalWrite(trigPin, HIGH);
    digitalWrite(LEDpin, HIGH);
    Serial.println("Done sending");
  }
  
}
