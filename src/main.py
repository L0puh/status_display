from g4f.client import Client

client = Client()

status = "current status" 
prompt = """you are status display, answer short and consistent,\\
            your input is: """
response = client.chat.completions.create(
    model="gpt-4.1",  
    messages=[{"role": "user", 
               "content": prompt + status}],
    web_search=False
)
print(response.choices[0].message.content)
