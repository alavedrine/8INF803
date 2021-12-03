from bs4 import BeautifulSoup
import requests
import re
import json

from requests.api import get

headers = {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Max-Age': '3600',
    'User-Agent': 'Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0'
}

urls = ["https://www.d20pfsrd.com/magic/all-spells/spells-a-d/",
        "https://www.d20pfsrd.com/magic/all-spells/spells-e-h/",
        "https://www.d20pfsrd.com/magic/all-spells/spells-i-l/",
        "https://www.d20pfsrd.com/magic/all-spells/spells-m-p/",
        "https://www.d20pfsrd.com/magic/all-spells/spells-q-t/",
        "https://www.d20pfsrd.com/magic/all-spells/spells-u-z/",]

refs = []
data = []

def parse_spells(url):
    req = requests.get(url, headers)
    soup = BeautifulSoup(req.content, 'html.parser')

    spells = soup.find_all('li', attrs={"class": "page new parent"})
    
    for spell in spells:
        refs.append(spell.find('a')['href'])


def get_spells(ref):
    for ref in refs:
        req = requests.get(ref, headers)
        soup = BeautifulSoup(req.content, 'html.parser')

        name = soup.find('h1').text
        print(name)

        raw_components = soup.find('b', text="Components").find_next_siblings().string
        # components = soup.find('b', text='Components').next_sibling.string.strip().split(', ')
        print(raw_components)

for url in urls:
    parse_spells(url)
    for ref in refs:
        get_spells(ref)