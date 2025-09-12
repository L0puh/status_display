from g4f.client import Client

client = Client()
response = client.chat.completions.create(
    model="gpt-4.1",  
    messages=[{"role": "user", 
               "content": "say hello and leave"}],
    web_search=False
)
print(response.choices[0].message.content)
