//#include "MPU9250.h"
#include <SD.h>
#include <SPI.h>
#include <Wire.h>

#define    MPU9250_ADDRESS            0x68
#define    MAG_ADDRESS                0x0C

#define    ACC_FULL_SCALE_2_G        0x00  
#define    ACC_FULL_SCALE_4_G        0x08
#define    ACC_FULL_SCALE_8_G        0x10
#define    ACC_FULL_SCALE_16_G       0x18

toggle0 = 0; //use this variable to avoid using PCINTs

File myFile;
File accel;


// This function read Nbytes bytes from I2C device at address Address. 
// Put read bytes starting at register Register in the Data array. 
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


// Write a byte (Data) in device (Address) at register (Register)
void I2CwriteByte(uint8_t Address, uint8_t Register, uint8_t Data)
{
  // Set register address
  Wire.beginTransmission(Address);
  Wire.write(Register);
  Wire.write(Data);
  Wire.endTransmission();
}


// Initializations
void setup()
{
  //Configuring pins for interrupts/timers

  //Configuring control registers
  //Clear
  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;
  OCR1A = 31250; //8MHz/256 prescaler
  TCCR1B |= (1 << WGM12); //CTC mode
  TCCR1B |= (1 << CS12); //256 prescaler
  TIMSKI1 |= (1 << OCIE1A); //enable timer compare interrupt
  
  // Arduino initializations
  Wire.begin();
  Serial.begin(115200);
  int Status = 0;

  // Configure accelerometers range
  I2CwriteByte(MPU9250_ADDRESS,28,ACC_FULL_SCALE_4_G);
  // Set by pass mode for the magnetometers
  I2CwriteByte(MPU9250_ADDRESS,0x37,0x02);

  Serial.print("Initializing SD card...");
  myFile.close();
  pinMode(10, OUTPUT);
 
  if (!SD.begin(10)) 
  {
    Serial.println("initialization failed!");
    Status = 0;
    return;
  }
  Serial.println("initialization done.");

  // open the file. note that only one file can be open at a time,
  // so you have to close this one before opening another.
  myFile = SD.open("test.txt", FILE_WRITE);
  
  // if the file opened okay, write to it:
  if (myFile) 
  {
    Serial.print("Writing to test.txt...");
    myFile.println("testing 1, 2, 3.");
    // close the file:
    myFile.close();
    Serial.println("done.");
    Status = 1;
    
  } 

        else 
    {
      // if the file didn't open, print an error:
      Serial.println("error opening test.txt");
      Status = 0;
    }
  
}


int cpt=0;


// Main loop, read and display data
void loop()
{
  int i;
  while(cpt<1000) {
    i = 1;
    myFile = SD.open("accel.txt", O_CREAT | O_WRITE);
    while(i<5) {   
      uint8_t Buf[14];
      I2Cread(MPU9250_ADDRESS,0x3B,14,Buf);
      
      // Create 16 bits values from 8 bits data
      
      // Accelerometer
      int16_t ax=-(Buf[0]<<8 | Buf[1]);
      int16_t ay=-(Buf[2]<<8 | Buf[3]);
      int16_t az=Buf[4]<<8 | Buf[5];
    
    
    
      char buffer[30];
      int n;
      n = sprintf (buffer, "%d: %d, %d, %d",cpt,ax,ay,az); 
    
      if (myFile) 
      {
        Serial.print(buffer);
        Serial.print("\n");
        myFile.println (buffer);
        Serial.println("cpt");
        cpt++;
      } 
    
      else 
      {
          // if the file didn't open, print an error:
          //Serial.println("error opening accel.txt");
      }
      Serial.println(i);
      i++;
    }
    
    myFile.close();
    Serial.println("flushing");
    //delay(10);
  }
 Serial.print("all done");
 while(1){}    
}

ISR(TIMER1_COMPA_vect)
{
 //write something to SD card to signify one second has past
}

