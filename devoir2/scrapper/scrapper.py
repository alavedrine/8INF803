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

urls = ["https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-a-b/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-c-d/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-e-f/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-g-h/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-i-j/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-k-l/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-m-n/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-o-p/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-q-r/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-s-t/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-u-v/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-w-x/",
        "https://www.d20pfsrd.com/bestiary/bestiary-alphabetical/bestiary-y-z/"]

refs = []
data = []

for url in urls:
    req = requests.get(url, headers)
    soup = BeautifulSoup(req.content, 'html.parser')

    for table in soup.find_all('table'):
        for a in table.find_all('a', { 'href': re.compile("^https://www.d20pfsrd.com/bestiary/")}):
            #print(a)
            if (a.parent.name != 'h3'):
                print(a)
                refs.append(a['href']) 

for ref in refs:
    print(ref)
    req = requests.get(ref, headers)
    soup_beasts = BeautifulSoup(req.content, 'html.parser')
    spells = []
    for spell in soup_beasts.find_all('a', { 'href': re.compile("^https://www.d20pfsrd.com/magic/all-spells/")}):
        # print(spell.string.strip())
        spells.append(spell.string.strip())

    name = ref.split("/")[-1].replace('-', ' ')
    res = {
        'name': name,
        'spells': spells
    }
    data.append(res)


with open('beasts.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=4, ensure_ascii=False)
    print("Created Json File")