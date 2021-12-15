const fs = require('fs');

const jsonPath = "./creatures.json";
const creatures = require(jsonPath)

var result = "digraph G {\n" + "    ratio=\"fill\";size=\"30,11.7!\";margin=0;node[shape=box, fontsize=32];";

creatures.forEach(element => {
  element.spells.forEach(spell => {
    const leftSide = element.name.replace(/ /g, '')
    .replace("(", "")
    .replace(")", "")
    .replace("-", " ")
    .replace("+", "plus")
    .replace(",", "");

    const rightSide = spell.replace(/ /g, '')
    .replace("/", "")
    .replace("-", " ")
    .replace("edge", "edgee")
    .replace(",", "");

    result = result 
    + leftSide
    + " -> " 
    + rightSide
     + ";\n";
  })
});

result = result + "\n}"

fs.writeFile('creatures-graph.dot', result, err => {
  if (err) {
    console.error(err)
    return
  }
  //file written successfully
})
