#include "MPU9250.h"            // Sparkfun Library
#include <SoftwareSerial.h>     // Bluetooth Serial Connection
#include <cppQueue.h>           // Queue Library
#include <Gaussian.h>
#include <LinkedList.h>
#include <GaussianAverage.h>

SoftwareSerial BT(5, 6);        // Init Blutooth Serial Rx Pin 5, Tx Pin 6

double threshold = 2.0;

int intPin = 2;                 // These can be changed, 2 and 3 are the Arduinos ext int pins
double now = 0;                 // Set up variable for the timer
uint8_t SmpRtDiv = 0x09;        // This is the number the fundamental freq of MPU sampler is divided by +1 the default fund freq is 1kHz IE for a 100Hz refresh SmpRtDiv = 9

float vx = 0;
float vy = 0;
float vz = 0;
float vxy = 0;
float vxz = 0;
float vyz = 0;
float vxyz = 0;

//GaussianAverage qvx = GaussianAverage(100);
//GaussianAverage qvy = GaussianAverage(100);
//GaussianAverage qvz = GaussianAverage(100);
//GaussianAverage qvxy = GaussianAverage(100);
//GaussianAverage qvxz = GaussianAverage(100);
//GaussianAverage qvyz = GaussianAverage(100);
//GaussianAverage qvxyz = GaussianAverage(100);

//Gaussian XAVG = 0.0;
//Gaussian YAVG = 0.0;
//Gaussian ZAVG = 0.0;
//Gaussian XYAVG = 0.0;
//Gaussian XZAVG = 0.0;
//Gaussian YZAVG = 0.0;
//Gaussian XYZAVG = 0.0;

//double dX = 0.0;
//double dY = 0.0;
//double dZ = 0.0;
//double dXY = 0.0;
//double dXZ = 0.0;
//double dYZ = 0.0;
//double dXYZ = 0.0;

//int i = 0;


MPU9250 myIMU;                  // Define the I2C address for the MPU

float magfunction();

void setup()
{
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
  // This loops until the the MPU Int data register goes high, This will go high only when every data register contains new data 
  if (myIMU.readByte(MPU9250_ADDRESS, INT_STATUS) & 0x01)
  {  
    now = millis();                                   // records how long the MPU has been running in milliseconds
    myIMU.readAccelData(myIMU.accelCount);            // Read the x/y/z adc values
    myIMU.getAres();                                  // Sets the DAC resolution

    myIMU.ax = (float)myIMU.accelCount[0]*myIMU.aRes; // records MPU x axis  
    myIMU.ay = (float)myIMU.accelCount[1]*myIMU.aRes; // records MPU y axis
    myIMU.az = (float)myIMU.accelCount[2]*myIMU.aRes; // records MPU z axis

    vx = magfunction(myIMU.ax);
    vy = magfunction(myIMU.ay);
    vz = magfunction(myIMU.az);
    vxy = magfunction(myIMU.ax,myIMU.ay);
    vxz = magfunction(myIMU.ax,myIMU.az);
    vyz = magfunction(myIMU.ay,myIMU.az);
    vxyz = magfunction(myIMU.ax,myIMU.ay,myIMU.az);

//    qvx.add(Gaussian(vx));
//    qvy.add(Gaussian(vy));
//    qvz.add(Gaussian(vz));
//    qvxy.add(Gaussian(vxy));
//    qvxz.add(Gaussian(vxz));
//    qvyz.add(Gaussian(vyz));
//    qvxyz.add(Gaussian(vxyz));

//    qvx+= vx;
//    qvy+= vy;
//    qvz+= vz;
//    qvxy+= vxy;
//    qvxz+= vxz;
//    qvyz+= vyz;
//    qvxyz+= vxyz;


//    XAVG = qvx.process();
//    YAVG = qvy.process();
//    ZAVG = qvz.process();
//    XYAVG = qvxy.process();
//    XZAVG = qvxz.process();
//    YZAVG = qvyz.process();
//    XYZAVG = qvxyz.process();
//
//    dX = vx-XAVG.mean; 
//    dY = vy-YAVG.mean; 
//    dZ = vz-ZAVG.mean; 
//    dXY = vxy-XYAVG.mean; 
//    dXZ = vxz-XZAVG.mean; 
//    dYZ = vyz-YZAVG.mean; 
//    dXYZ = vxyz-XYZAVG.mean; 
//       
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


//    Serial.print(now);
//    Serial.print("\t");
//    Serial.print(vx);
//    Serial.print("\t");
//    Serial.print(vy);
//    Serial.print("\t");
//    Serial.print(vz);
//    Serial.print("\t");
//    Serial.print(vxy);
//    Serial.print("\t");
//    Serial.print(vxz);
//    Serial.print("\t");
//    Serial.print(vyz);
//    Serial.print("\t");
//    Serial.print(vxyz);
//    Serial.print("\n");
    
    BT.print(now);
    BT.print("\t");
    BT.print(vx);
    BT.print("\t");
    BT.print(vy);
    BT.print("\t");
    BT.print(vz);
    BT.print("\t");
    BT.print(vxy);
    BT.print("\t");
    BT.print(vxz);
    BT.print("\t");
    BT.print(vyz);
    BT.print("\t");
    BT.print(vxyz);
    BT.print("\n");

    
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
  
//  i++;

}

float magfunction(float one){
  float mags = 0;
  mags = sqrt(sq(one));
  return mags;
}

float magfunction(float one, float two){
  float mags = 0;
  mags = sqrt(sq(one)+sq(two));
  return mags;
}

float magfunction(float one, float two, float three){
  float mags = 0;
  mags = sqrt(sq(one)+sq(two)+sq(three));
  return mags;
}

