package com.bd.front.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterModel implements Serializable {
    private String name;
    private int minLevel = 0;
    private int maxLevel = 9;
    private List<String> components;
    private List<String> classes;
    private String classesLogic;
    private List<String> keywords;

    public FilterModel() {
        components = new ArrayList<>();
        classes = new ArrayList<>();
        keywords = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "FilterModel{" +
                "name='" + name + '\'' +
                ", minLevel=" + minLevel +
                ", maxLevel=" + maxLevel +
                ", components=" + components +
                ", classes=" + classes +
                ", classesLogic='" + classesLogic + '\'' +
                ", keywords=" + keywords +
                '}';
    }

    public FilterModel(String name, int minLevel, int maxLevel, List<String> components, List<String> classes, String classesLogic, List<String> keywords) {
        this.name = name;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.components = components;
        this.classes = classes;
        this.classesLogic = classesLogic;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public String getClassesLogic() {
        return classesLogic;
    }

    public void setClassesLogic(String classesLogic) {
        this.classesLogic = classesLogic;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
