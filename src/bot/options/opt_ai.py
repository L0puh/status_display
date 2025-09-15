from bot import dp, Options
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery
from ..keyboard import get_ai_keyboard

@dp.message(Options.AI)
async def process_ai(message: Message) -> None:
    await message.answer("Enter your choice",
                         reply_markup=get_ai_keyboard())


@dp.callback_query(F.data.startswith("ai_"))
async def callback_ai_options(callback: CallbackQuery, state: FSMContext):
    action = callback.data.split("_")[1]

    if action == "custom":
        pass
    if action == "random":
        pass

    await callback.answer() 


