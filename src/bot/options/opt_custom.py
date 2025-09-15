
from bot import dp, Options
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, CallbackQuery


@dp.message(Options.custom, F.text) 
async def process_custom_text(message: Message, state: FSMContext):
    user_custom_text = message.text
    print(f"User entered custom text: '{user_custom_text}'")
    await message.answer(f"Got your custom text: '{user_custom_text}'. Thanks!")

    # IMPORTANT: Clear the state after processing
    # This ensures that subsequent messages are not treated as custom input
    await state.clear()
    print("State cleared after processing custom text.")
