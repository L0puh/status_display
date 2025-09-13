import os
import sys

import logging
import asyncio

from aiogram import Bot, html
from aiogram.client.default import DefaultBotProperties
from aiogram.enums import ParseMode

from bot import dp
from dotenv import load_dotenv


async def main() -> None:
    load_dotenv()
    TOKEN = os.getenv("TOKEN")
    bot = Bot(token = TOKEN, default =
              DefaultBotProperties(parse_mode=ParseMode.HTML))
    await dp.start_polling(bot)



if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, stream=sys.stdout)
    asyncio.run(main())
    
