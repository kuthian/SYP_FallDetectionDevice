#include <SPI.h>
#include <Wire.h>
#include <MPU9250.h>


#define MPU9250_ADDRESS 	  0x68
#define ACC_FULL_SCALE_4_G 	0x08
#define WHO_AM_I_MPU9250 	  0x75
#define SELF_TEST_X_ACCEL 	0x0D
#define SELF_TEST_y_ACCEL 	0x0E
#define SELF_TEST_Z_ACCEL 	0x0F

void I2Cread(uint8_t Address, uint8_t Register, uint8_t Nbytes, uint8_t* Data)
{
	// Set register address
	Wire.beginTransmission(Address);
	Wire.write(Register);
	Wire.endTransmission();
  
	// Read Nbytes
	Wire.requestFrom(Address, Nbytes); 
	uint8_t index=0;
	while (Wire.available())
		Data[index++]=Wire.read();
}


void I2CwriteByte(uint8_t Address, uint8_t Register, uint8_t Data)
{
	// Set register address
	Wire.beginTransmission(Address);
	Wire.write(Register);
	Wire.write(Data);
	Wire.endTransmission();
}


void setup()
{
	Wire.begin();
	Serial.begin(38400);
  uint8_t* Astatus[1]; 
  uint8_t bitmask1 = 231;
  uint8_t bitmask2 = 8;


  I2CwriteByte(MPU9250_ADDRESS,ACCEL_CONFIG,0x8);
  I2Cread(MPU9250_ADDRESS,ACCEL_CONFIG,2,Astatus[1]);
  Serial.print(Astatus[1]);
  Serial.println("");
  Astatus[1] = (Astatus[1] & (~bitmask1));
  Astatus[2] = (Astatus[1] | (bitmask2));
  Serial.println("");
  I2CwriteByte(MPU9250_ADDRESS,ACCEL_CONFIG,Astatus[1]);
  Serial.println("");
}
   
void loop()
{
	// Display data counter
	//Serial.print ("\t");
/*
	uint8_t Buf[14];
	I2Cread(MPU9250_ADDRESS,0x3B,14,Buf);
  
	// Accelerometer
	int16_t ax=-(Buf[0]<<8 | Buf[1]);
	int16_t ay=-(Buf[2]<<8 | Buf[3]);
	int16_t az=Buf[4]<<8 | Buf[5];
	
	// Accelerometer
	Serial.print (ax,DEC); 
	Serial.print ("\t");
	Serial.print (ay,DEC);
	Serial.print ("\t");
	Serial.print (az,DEC);  
	Serial.print ("\t");
	

	Serial.println("");
*/ 	
}


