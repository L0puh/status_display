ROOT=$(pwd)
SRC=$ROOT/src
if [ ! -d vendor ]; then
   echo "downloading dependicies..."
   mkdir vendor
   cd vendor
   git clone https://github.com/xtekky/gpt4free.git
   cd gpt4free
   python3 -m venv venv
   source venv/bin/activate
   pip install -r requirements-slim.txt
   pip install -e . 
else 
   echo "activating virtual environment..."
   source vendor/gpt4free/venv/bin/activate
fi
cd $ROOT
echo "running the project..."
python src/main.py
