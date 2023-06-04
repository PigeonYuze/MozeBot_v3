package com.pigeonyuze.command.recreation.random.pants;

import java.util.ArrayList;

/**
 * PantsProperty class, bundle group of information of Pants' property
 */
public class PantsProperty {
    private final int level;

    private ArrayList<String> entry = new ArrayList<>();

    private final String category;

    /**
     * PantsProperty constructor
     *
     * @param level Property Level
     * @param category Property category, color, material, etc.
     */
    public PantsProperty(int level, String category) {
        this.level = level;
        this.category = category;
    }

    /**
     * Set property entry list via external ArrayList
     *
     * @param entry List of entries
     */
    public void setEntry(ArrayList<String> entry) {
        this.entry = entry;
    }

    /**
     * Get the level of this property
     *
     * @return Field variable level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Get the category of this property
     *
     * @return Field variable category
     */
    public String getCategory() {
        return this.category;
    }

    public ArrayList<String> getEntry() {
        return this.entry;
    }
}