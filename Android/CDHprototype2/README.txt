How does this CDHprototype2 work?
1. Install Arduino Communicator on phone.
2. Install this app.
3. This app sends and receives data with the arduino through intents with Arduino Communicator.

TODO:
Each group wanting to do processing should create a class that will be called by DataFromArduinoReceiver when the header of the data packet matches their group id (to be decided). This class will receive a byte[], which represents the data from the arduino. Please make sure such a class does not run too long.

Group ID sample:
Payload: 0x69
ADCS: 0xFF