# status display
arduino with LCD display that shows reminders, quotes or your own input 
# features
... 

# setup
## interacting with telegram bot
1. create `.env` file with API of the telegram bot 
2. run the app with `run.sh` on some server

## interacting with android app
1. compile the files with [android light](https://github.com/L0puh/android_light) script (or any other really) 
2. upload apk to android
3. connect arduino 
4. use the display 


# stack
## main functionality:
- Arduino UNO + I2C (LiquidCrystal_I2C)
- [g4f](https://g4f.dev/) for AI
## interacting:
- Telegram bot for settings and prompts 
- android app for interacting 


# useful resources:
- [Interfacing HC05 Bluetooth Module](https://lastminuteengineers.com/hc05-bluetooth-arduino-tutorial/)
