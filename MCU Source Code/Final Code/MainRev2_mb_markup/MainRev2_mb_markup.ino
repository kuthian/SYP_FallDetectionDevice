#include <MPU9250.h>
#include <quaternionFilters.h>
#include "MPU9250.h"            // Sparkfun Library
#include <SoftwareSerial.h>     // Bluetooth Serial Connection
#include <LinkedList.h>
#include <QueueList.h>

MPU9250 myIMU;                  // Define the I2C address for the MPU
  //Create Gaussian vectors
QueueList <float> X;
QueueList <float> Y;
QueueList <float> Z;
QueueList <float> XY;
QueueList <float> XZ;
QueueList <float> YZ;
QueueList <float> XYZ;

void setup()
{
  int intPin = 2; //These can be changed, 2 and 3 are the Arduinos ext int pins
  SoftwareSerial BT(5, 6);        // Init Blutooth Serial Rx Pin 5, Tx Pin 6
  
  pinMode(9, OUTPUT);           // Needed for SoftSerial not sure why
  BT.begin(38400);              // set the data rate for the SoftwareSerial port
    
  Wire.begin();                 // init I2C connection
  TWBR = 12;                    // 400 kbit/sec I2C speed
  Serial.begin(38400);          // set up Serial connection

  pinMode(intPin, INPUT);       // Set up the interrupt pin
  digitalWrite(intPin, LOW);    // on digital write this pin is held low preventing interupt 

  byte c = myIMU.readByte(MPU9250_ADDRESS, WHO_AM_I_MPU9250);   // This is checking the MPU addess default is 71
  if (c == 0x71)
  {
    uint8_t SmpRtDiv = 0x09;        // This is the number the fundamental freq of MPU sampler is divided by +1 the default fund freq is 1kHz IE for a 100Hz refresh SmpRtDiv = 9
    Serial.println("MPU9250 is online...");
    
    myIMU.MPU9250SelfTest(myIMU.SelfTest); //Check Biases
    
    //myIMU.calibrateMPU9250(myIMU.gyroBias, myIMU.accelBias); //Load Biases into registers

    myIMU.initMPU9250();
    myIMU.writeByte(MPU9250_ADDRESS, SMPLRT_DIV, SmpRtDiv); // Sets the MPU sample rate to 100Hz
        
    Serial.println("MPU9250 initialized...");
    
  }
  else
  {
    Serial.print("Could not connect to MPU9250: 0x");
    Serial.println(c, HEX);                          // Print address of MPU
    while(1) ;                                       // Loop forever if communication doesn't happen
  }
}

void loop()
{
    double now = 0;// Set up variable for the timer
    float threshold = 2.0;
    
  // This loops until the the MPU Int data register goes high, This will go high only when every data register contains new data 
  
  if (myIMU.readByte(MPU9250_ADDRESS, INT_STATUS) & 0x01)
  {  
//    if(X.count() >= 99)
//    {
      //Reading from MPU9250
      now = millis();                                   // records how long the MPU has been running in milliseconds
      myIMU.readAccelData(myIMU.accelCount);            // Read the x/y/z adc values
      myIMU.getAres();                                  // Sets the DAC resolution
  
      myIMU.ax = (float)myIMU.accelCount[0]*myIMU.aRes; // records MPU x axis  
      myIMU.ay = (float)myIMU.accelCount[1]*myIMU.aRes; // records MPU y axis
      myIMU.az = (float)myIMU.accelCount[2]*myIMU.aRes; // records MPU z axis
  
      //These are single datapoints for computing moving average
      float vx = magfunction1(myIMU.ax);
      float vy = magfunction1(myIMU.ay);
      float vz = magfunction1(myIMU.az);
      float vxy = magfunction2(myIMU.ax,myIMU.ay);
      float vxz = magfunction2(myIMU.ax,myIMU.az);
      float vyz = magfunction2(myIMU.ay,myIMU.az);
      float vxyz = magfunction3(myIMU.ax,myIMU.ay,myIMU.az);

      int stat = X.count();
      if(stat>=10)
      {
        X.pop();
        X.push(vx);
        Y.pop();
        Y.push(vy);
        Z.pop();
        Z.push(vz);
        XY.pop();
        XY.push(vxy);
        XZ.pop();
        XZ.push(vxz);
        YZ.pop();
        YZ.push(vyz);
        XYZ.pop();
        XYZ.push(vyz);
      }
      
      else
      {
        X.push(vx);
        Y.push(vy);
        Z.push(vz);
        XY.push(vxy);
        XZ.push(vxz);
        YZ.push(vyz);
        XYZ.push(vxyz);
      }
      
  //    float dx = delta(vx, qvx); //Appends and calculates delta value
  //    float dy = delta(vy, qvy);
  //    float dz = delta(vz, qvz);
  //    float dxy = delta(vxy, qvxy);
  //    float dxz = delta(vxz, qvxz);
  //    float dyz = delta(vyz, qvyz);
  //    float dxyz = delta(vxyz, qvxyz);
  
  ////       
  //    if(i > 100){
  //      if ((dX > threshold) || (dY > threshold) || (dZ > threshold) || (dXY > threshold) || (dXZ > threshold) || (dYZ > threshold) || (dXYZ > threshold)){
  //      Serial.print("\n");
  //      Serial.print("ALERT");
  //      Serial.print("\n");
  //      BT.print("\n");
  //      BT.print("ALERT");
  //      BT.print("\n");
  //      }
  //    }
      
      myIMU.updateTime();
  
      //Serial.print(now);
      //Serial.print("\t");
      Serial.print(vx);
      Serial.print("\t");
      Serial.print(vy);
      Serial.print("\t");
      Serial.print(vz);
      Serial.print("\t");
      Serial.print(vxy);
      Serial.print("\t");
      Serial.print(vxz);
      Serial.print("\t");
      Serial.print(vyz);
      Serial.print("\t");
      Serial.print(vxyz);
      Serial.print("\n");
      
  //    BT.print(now);
  //    BT.print("\t");
  //    BT.print(vx);
  //    BT.print("\t");
  //    BT.print(vy);
  //    BT.print("\t");
  //    BT.print(vz);
  //    BT.print("\t");
  //    BT.print(vxy);
  //    BT.print("\t");
  //    BT.print(vxz);
  //    BT.print("\t");
  //    BT.print(vyz);
  //    BT.print("\t");
  //    BT.print(vxyz);
  //    BT.print("\n");
  
      
  //    Serial.print(now);
  //    Serial.print("\t");
  //    Serial.print(myIMU.ax*1000);
  //    Serial.print("\t");
  //    Serial.print(myIMU.ay*1000);
  //    Serial.print("\t");
  //    Serial.print(myIMU.az*1000);
  //    Serial.print("\n");
  //    BT.print(now);
  //    BT.print("\t");
  //    BT.print(myIMU.ax*1000);
  //    BT.print("\t");
  //    BT.print(myIMU.ay*1000);
  //    BT.print("\t");
  //    BT.print(myIMU.az*1000);
  //    BT.print("\n");

  }

}

float magfunction1(float one){
  float mags = sqrt(sq(one));
  return mags;
}

float magfunction2(float one, float two){
  float mags = sqrt(sq(one)+sq(two));
  return mags;
}

float magfunction3(float one, float two, float three){
  float mags = sqrt(sq(one)+sq(two)+sq(three));
  return mags;
}

//Appends and calculates delta value
//float delta(float vx, GaussianAverage qvx)
//{
//  //qvx+=vx;
//  qvx.add(Gaussian(vx));
//  Gaussian XAVG = qvx.process();
//  float delta = vx-XAVG.mean;
//  return delta;
//}

