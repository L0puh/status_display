#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define BUTTON_PIN  2 
#define LED_ON_PIN  3
#define LED_OFF_PIN 4

float last_update = 0.0f;
const float sleep_time = 1000.0f;     // update time of display
LiquidCrystal_I2C lcd(0x27, 16, 2);   // setup I2C for display
#include <SoftwareSerial.h>

SoftwareSerial HM05(8, 9); // RX = 8, TX = 9

void print_on_lcd(const char* text, int x=0, int y=0);
void print_on_lcd(String text, int x=0, int y=0);
String receive_message();
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


void loop() {
  receive_message();
  button_turn_on();

}


String receive_message() {
  if (HM05.available() > 0){
    String s = "";
    while (HM05.available() > 0) {
      char c = HM05.read();
      s += c;
      delay(10);
    }
    print_on_lcd(s, 0, 3);
    return s;
  }
  return "";
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

void print_on_lcd(String text, int x, int y){
  float curr = millis();
  if (curr - last_update >= sleep_time) {
    lcd.clear();
    lcd.setCursor(x, y);
    lcd.print(text);
    last_update = curr;
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
