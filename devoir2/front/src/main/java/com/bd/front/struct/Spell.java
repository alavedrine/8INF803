package com.bd.front.struct;

import scala.collection.mutable.WrappedArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Spell implements Serializable {
    public String name;
    public WrappedArray<String> levels;
    public String castingTime;
    public WrappedArray<String> components;
    public String spellResistance;
    public String description;
    public String url;

    public Spell() {

    }

    public Spell(String name, WrappedArray<String> levels, String castingTime, WrappedArray<String> components, String spellResistance, String description, String url) {
        this.name = name;
        this.levels = levels;
        this.castingTime = castingTime;
        this.components = components;
        this.spellResistance = spellResistance;
        this.description = description;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Spell{" +
                "name='" + name + '\'' +
                ", levels=" + levels +
                ", castingTime='" + castingTime + '\'' +
                ", components=" + components +
                ", spellResistance='" + spellResistance + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}