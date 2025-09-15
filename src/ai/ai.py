from g4f.client import Client


client = Client()
prompt = """you are status display, answer short and consistent,\\
            your input is: """

def get_response(status):

    response = client.chat.completions.create(
        model="gpt-4.1",  
        messages=[{"role": "user", 
                   "content": prompt + status}],
        web_search=False
    )
    return response.choices[0].message.content
