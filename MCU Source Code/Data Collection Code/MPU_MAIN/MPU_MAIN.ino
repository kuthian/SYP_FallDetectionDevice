#include "MPU9250.h"  // includes the MPU sparkfun Library

MPU9250 myIMU;

void setup()
{
  Wire.begin();
  // TWBR = 12;  // 400 kbit/sec I2C speed
  Serial.begin(38400);

  myIMU.initialize

  
}

