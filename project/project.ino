#include <Wire.h>
#include <LiquidCrystal_I2C.h>

#define BUTTON_PIN  2 
#define LED_ON_PIN  3
#define LED_OFF_PIN 4

float last_update = 0.0f;
const float sleep_time = 850.0f;     // update time of display, speed of
                                     // "scrolling"
LiquidCrystal_I2C lcd(0x27, 16, 2);   // setup I2C for display
#include <SoftwareSerial.h>

SoftwareSerial HM05(8, 9); // RX = 8, TX = 9

void print_on_lcd(String text, int x=0, int y=0);
String receive_message();

bool is_on = false;
bool prev_state = false;
float last_press = 0;

const int lcd_width = 16; 
int scroll_pos = 0; 

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

}

void blink_led(){
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
  blink_led();
  if (is_on == LOW){
    receive_message();
  }
  else {
    print_on_lcd("the display is turned off");
  }
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
    print_on_lcd(s);

    return s;
  }
  return "";
}

void print_on_lcd(String text, int x, int y){
  //FIXME: doesn't work for recieved messages for some reason? 
  float curr = millis();
  if (curr - last_update >= sleep_time) {
    lcd.clear();

    String chunk = "";
    if (scroll_pos + lcd_width <= text.length()){
      chunk = text.substring(scroll_pos, scroll_pos + lcd_width);
    } else {
      int chars = text.length() - scroll_pos;
      chunk = text.substring(scroll_pos);
      for (int i = 0; i < (lcd_width - chars); i++) chunk += " ";
      if (chars < lcd_width) {
        int start = 0;
        int remain = lcd_width - chunk.length();
        if (remain > 0) chunk += text.substring(start, min(remain, text.length()));
      }
    }
    lcd.setCursor(x, y);
    lcd.print(chunk);
    scroll_pos++;
    last_update = curr;
    if (scroll_pos > text.length() + lcd_width) {
      scroll_pos = 0;
    }
  }
}


