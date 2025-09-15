import os

from aiogram import Bot, Dispatcher, html, F
from aiogram.filters import CommandStart, Command 
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton


dp = Dispatcher()

@dp.message(CommandStart())
async def command_start_handler(message : Message) -> None:
    await message.answer(f"Hello, {html.bold(message.from_user.full_name)}!")

@dp.message(Command("help"))
async def command_help_handler(message : Message) -> None:
    #TODO: write guide to setup the display
    await message.answer("Here will be some info...") 



@dp.message(Command("options"))
async def command_options_handler(message : Message) -> None:
    await message.answer("Enter your choice",
                         reply_markup=get_options_keyboard())



@dp.callback_query(F.data.startswith("opt_"))
async def callback_options(callback: CallbackQuery):
    action = callback.data.split("_")[1]
    response_text = ""
    if action == "ai":
        response_text = "you chose ai quotes! here's a quote..." 
    elif action == "reminder":
        response_text = "you chose reminder! what should i remind you about?"
    elif action == "task":
        response_text = "you chose current task! what task are you working on?"
    elif action == "weather":
        response_text = "you chose weather! what's the location?" 
    elif action == "custom":
        response_text = "you chose custom! how can i help you customize?" 
    
    if callback.message: 
        await callback.message.edit_text(response_text,
                                         reply_markup=None) 
    else:
        await callback.answer(response_text, show_alert=True) 

    await callback.answer() 

def get_options_keyboard() -> InlineKeyboardMarkup:
    kb = [
        [
            InlineKeyboardButton(text="AI quotes", callback_data="opt_ai"),
            InlineKeyboardButton(text="Reminder", callback_data="opt_reminder"),
        ],
        [
            InlineKeyboardButton(text="Current task", callback_data="opt_task"),
            InlineKeyboardButton(text="Weather", callback_data="opt_weather"),
        ],
        [
            InlineKeyboardButton(text="Custom", callback_data="opt_custom"),
        ]
    ]
    keyboard = InlineKeyboardMarkup(
        inline_keyboard=kb
    )
    return keyboard
