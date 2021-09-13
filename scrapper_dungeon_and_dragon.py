from bs4 import BeautifulSoup
import requests
import re
import json

headers = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Max-Age': '3600',
    'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0'
    }

url = "https://aonprd.com/Spells.aspx?Class=All"
req = requests.get(url, headers)

soup = BeautifulSoup(req.content, 'html.parser')
refs = []
data = []

for span in soup.find_all('span', {'id': re.compile("LabelName$")}):
    for a in span.find_all('a'):
        refs.append(a['href'])


for ref in refs:
    print("REF : ", ref)
    ref = ref.replace(' ', '%20')
    ref = ref.replace('\'', '%27')
    spell_url = "https://aonprd.com/" + ref
    print("URL : ", spell_url)
    req = requests.get(spell_url, headers)
    soup_spell = BeautifulSoup(req.content, 'html.parser')
    for span in soup_spell.find_all('span', {"id": re.compile("^ctl00_MainContent_DataListTypes_ct")}):
        if span.find('h1'):
            title = span.find('h1', attrs={'class': 'title'}).text.strip()
            if span.find('b', text='Level'):
                levels = span.find('b', text='Level').next_sibling.string.strip().split(', ')
            else:
                levels = []

            if span.find('b', text='Casting Time'):
                casting = span.find('b', text='Casting Time').next_sibling.string.strip()
            else:
                casting=""
            
            if span.find('b', text='Components'):
                components = span.find('b', text='Components').next_sibling.string.strip().split(', ')
            else:
                components = []
            
            if span.find('b', text='Spell Resistance'):
                resistance = span.find('b', text='Spell Resistance').next_sibling.string.strip()
            else:
                resistance = "False"
            
            if span.find('h3', text='Description'):
                description = span.find('h3', text='Description').next_sibling.string.strip()
            else:
                description = ""

            res = {
                'name': title,
                'levels': levels,
                'casting_time': casting,
                'components': components,
                'spell_resistance': resistance,
                'description': description
            }
            data.append(res)


with open('spells.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=4, ensure_ascii=False)
    print("Created Json File")