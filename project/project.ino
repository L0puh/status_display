#include <Wire.h>
#include <LiquidCrystal_I2C.h>

/* values for display printing */
float last_update = 0.0f;
const float sleep_time = 1000.0f;     // update time of display
LiquidCrystal_I2C lcd(0x27, 16, 2);   // setup I2C for display


void print_on_lcd(const char* text, int x, int y);
void receive_message();
void print_message();

void setup() {
  Serial.begin(9600);

  lcd.init();
  lcd.backlight();
  last_update = millis();
}

void loop() {
}


// TODO: recieve text from serial port 
void receive_message() {
}

// TODO: add "flowing" text
void print_message() {
}

void print_on_lcd(const char* text, int x, int y){
  float curr = millis();
  if (curr - last_update >= sleep_time) {
    lcd.clear();
    lcd.setCursor(x, y);
    lcd.print(text);
    last_update = curr;
  }
}
