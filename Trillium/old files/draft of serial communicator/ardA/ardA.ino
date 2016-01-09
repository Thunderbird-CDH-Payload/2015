char samplestring[] = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.*";
int interruptPin = 2;
int stringlen;
int index = 0;
boolean shouldSend;

void setup() {
  pinMode(interruptPin, INPUT);
  Serial.begin(9600);
  Serial3.begin(9600);
  Serial.println("ArdA");
  stringlen = sizeof(samplestring)-1;
  delay(3000);
  shouldSend = true;
  attachInterrupt(interruptPin, handleInterrupt, RISING);
  
}

void loop() {
  if (index < stringlen && shouldSend){
    Serial.write(samplestring + index, 10);
    Serial3.write(samplestring + index, 10);
    index += 10;
    shouldSend = false; 
  }
}

void handleInterrupt() {
//  shouldSend = false;
  shouldSend = true;
  Serial.println("hit inteerupt");
}
