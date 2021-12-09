package com.bd.front.struct;

import java.util.List;

public class Spell {
    private String name;
    private String level;
    private String castingTime;
    private List<String> components;
    private String spellResistance;
    private String description;

    public Spell() {

    }

    public Spell(String name, String level, String castingTime, List<String> components, String spellResistance, String description) {
        this.name = name;
        this.level = level;
        this.castingTime = castingTime;
        this.components = components;
        this.spellResistance = spellResistance;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCastingTime() {
        return castingTime;
    }

    public void setCastingTime(String castingTime) {
        this.castingTime = castingTime;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public String getSpellResistance() {
        return spellResistance;
    }

    public void setSpellResistance(String spellResistance) {
        this.spellResistance = spellResistance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
