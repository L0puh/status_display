import os

from aiogram import Bot, Dispatcher, html, F
from aiogram.filters import CommandStart, Command 
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery


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


#FIXME: doesn't work...
@dp.callback_query(F.data.startswith("opt_"))
async def callback_options(callback: CallbackQuery):
    action = callback.data.split("_")[1]
    print(action, callback.message)

    if action == "ai":
        await callback.message.answer(callback.message)
    elif action == "reminder":
        await callback.message.answer(callback.message)
    elif action == "task":
        await callback.message.answer(callback.message)
    elif action == "weather":
        await call.message.answer(callback.message)
    elif action == "custom":
        await callback.message.answer(callback.message)
    await callback.answer()


def get_options_keyboard() -> ReplyKeyboardMarkup:
    kb = [
            [
                KeyboardButton(text="AI quotes", callback_data="opt_ai"),
                KeyboardButton(text="Reminder", callback="opt_reminder"),
                KeyboardButton(text="Current task", callback="opt_task"),
                KeyboardButton(text="Weather", callback="opt_weather"),
                KeyboardButton(text="Custom", callback="opt_custom"),
                ],
            ]
    keyboard = ReplyKeyboardMarkup(
            keyboard=kb,
            resize_keyboard=True,
            input_field_placeholder="Choose options"
            )
    return keyboard

