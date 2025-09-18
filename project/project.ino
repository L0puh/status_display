#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define BUTTON_PIN  2 
#define LED_ON_PIN  3
#define LED_OFF_PIN 4
/* values for display printing */
float last_update = 0.0f;
const float sleep_time = 1000.0f;     // update time of display
LiquidCrystal_I2C lcd(0x27, 16, 2);   // setup I2C for display
#include <SoftwareSerial.h>
SoftwareSerial HM05(8, 9); // RX = 8, TX = 9

void print_on_lcd(const char* text, int x=0, int y=0);
void receive_message();
void print_message();

bool is_on = false;
bool prev_state = false;
float last_press = 0;

void setup() {
  Serial.begin(9600);
  HM05.begin(9600);

  pinMode(BUTTON_PIN, INPUT);
  pinMode(LED_ON_PIN, OUTPUT);
  pinMode(LED_OFF_PIN,OUTPUT);

  lcd.init();
  lcd.backlight();
  last_update = millis();

}

void button_turn_on(){
  if (millis() - last_press > 300.0f) {
    byte state = digitalRead(BUTTON_PIN);
    if (state != prev_state) {
      last_press = millis();
      prev_state = state;
      if (state == LOW) {
        is_on = (is_on == HIGH) ? LOW: HIGH;
      }
    }
  }

  if (is_on){
    digitalWrite(LED_ON_PIN, HIGH);
    digitalWrite(LED_OFF_PIN, LOW);
  } else {
    digitalWrite(LED_OFF_PIN, HIGH);
    digitalWrite(LED_ON_PIN, LOW);
  }
}

/*
 *
 * A - AI mode
 * C - custom mode
 * W - weather
 * T - task 
 * R - reminder
 *
 */

void process_mode(char mode){ 
  switch(mode) {
    case 'A':
      {

        break;
      }
  }
}

void loop() {
  /* print_message("some long text that\ */
  /*               probably doesn't fit in the something\ */
  /*               of this size and better it to split\ */
  /*               and show flowing..."); */

  /* HM05.listen(); */
  if (HM05.available() > 0){
    char mode = HM05.read();
    HM05.print("RECIEVED: ");
    HM05.println(mode);
    if (mode == 'A')
      print_on_lcd("AI MODE");
    if (mode == 'W')
      print_on_lcd("WEATHER MODE");
    if (mode == 'T')
      print_on_lcd("TASK MODE");
    if (mode == 'R')
      print_on_lcd("REMINDER MODE");
    if (mode == 'C')
      print_on_lcd("CUSTOM MODE");
  }
  else {
  }
  button_turn_on();

}


// TODO: recieve text from serial port 
void receive_message() {
}

// TODO: add "flowing" text
// the size if 16 chars * 2 lines
void print_message(const char* text) {
  int len = strlen(text);
  if (len > 16) {
  } else {
    print_on_lcd(text, 1, 0);
  }
  

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
