from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton


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
    return InlineKeyboardMarkup(
        inline_keyboard=kb
    )
def get_ai_keyboard() -> InlineKeyboardMarkup: 
    kb = [ 
          [
              InlineKeyboardButton(text="custom prompt", callback_data =
                                   "ai_custom"),
              InlineKeyboardButton(text="random quote", callback_data =
                                   "ai_random")
              ],
          ]
    return InlineKeyboardMarkup(inline_keyboard=kb)
