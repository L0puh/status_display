import os

from aiogram import Bot, Dispatcher, html, F
from aiogram.filters import CommandStart, Command, StateFilter
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import StatesGroup, State

from .keyboard import get_ai_keyboard, get_options_keyboard

dp = Dispatcher()

class Options(StatesGroup):
    AI       = State()
    custom   = State()
    reminder = State()
    weather  = State()
    task     = State()

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
async def callback_options(callback: CallbackQuery, state: FSMContext):
    action = callback.data.split("_")[1]
    response_text = ""
    if action == "ai":
        response_text = "you chose ai quotes" 

        #FIXME: state doesn't work...
        await state.set_state(Options.AI)
        await callback.message.answer(response_text)

    elif action == "reminder":
        response_text = "you chose reminder"
    elif action == "task":
        response_text = "what task are you working on?"
    elif action == "weather":
        response_text = "what's the location?" 
    elif action == "custom":
        response_text = "enter custom text"
        await state.set_state(Options.custom)
        await callback.message.edit_text(response_text)
    

    await callback.answer() 


