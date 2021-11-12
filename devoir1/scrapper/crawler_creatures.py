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


def parse_bestiary(url):
    req = requests.get(url, headers)
    soup = BeautifulSoup(req.content, 'html.parser')

    for s in soup.select('h3'):
        s.extract()

    for s in soup.select('sup'):
        s.extract()

    table = soup.find('table')

    table_body = table.find('tbody')

    rows = table_body.find_all('tr')
    for row in rows:
        cols = row.find_all('td')
        for col in cols:
            creatures = col.find_all('a')
            for creature in creatures:
                if not creature.has_attr("href"):
                    continue
                print(creature)
                refs.append(creature['href'])


def append_spells(creature_spells_tags, creature_spells):
    for spell in creature_spells_tags:
        if spell.text not in creature_spells:
            creature_spells.append(spell.text)

for url in urls:
    parse_bestiary(url)

for ref in refs:
    print("REF : ", ref)
    ref = ref.replace(' ', '%20')
    ref = ref.replace('\'', '%27')
    creature_url = ref
    print("URL : ", creature_url)
    req = requests.get(creature_url, headers)
    soup_creature = BeautifulSoup(req.content, 'html.parser')

    if soup_creature.find("article", attrs={'id': 'post-404'}):
        continue

    creature_name = soup_creature.find("h1").text

    to_dump = soup_creature.find('p', text=re.compile("^SPECIAL ABILI"))
    if to_dump is None:
        to_dump = soup_creature.find('span', attrs={'id': re.compile("^SPECIAL ABILI")})
    if to_dump is not None:
        for s in to_dump.find_next_siblings('p'):
            s.extract()

    creature_spells_tags = soup_creature.find_all('a', attrs={'class': 'spell'})

    creature_spells = []

    append_spells(creature_spells_tags, creature_spells)

    res = {
        'name': creature_name,
        'spells': creature_spells
    }

    data.append(res)

with open('creatures.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=4, ensure_ascii=False)
    print("Created Json File")
