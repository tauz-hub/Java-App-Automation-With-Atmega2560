int R1 = 6;
int R2 = 7;
int L1 = 8;
int L2 = 9;

int comand = "offAll";

void setup() {
Serial.begin(9600);
Serial1.begin(9600);
pinMode(R1,OUTPUT);
pinMode(R2,OUTPUT);
pinMode(L1,OUTPUT);
pinMode(L2,OUTPUT);
}

void loop() {
  
  if(Serial1.available() > 0){
  comand = Serial1.read();
    
  }
  
  Serial.print(comand);
  delay(1);
 
  if(comand == "offAll"){
    digitalWrite(R1, LOW);
    digitalWrite(R2, LOW);
    digitalWrite(L1, LOW);
    digitalWrite(L2, LOW);
    
  } else if(comand == "onR1"){
    digitalWrite(R1, HIGH);
    digitalWrite(R2, LOW);
  
  } else if(comand == "onR2"){
    digitalWrite(R1, LOW);
    digitalWrite(R2, HIGH);
  } else  if(comand == "onL1"){
    digitalWrite(L1, LOW);
    digitalWrite(L2, HIGH);
  }  else if(comand == "onL2"){
    digitalWrite(L1, HIGH);
    digitalWrite(L2, LOW);
  }
}
