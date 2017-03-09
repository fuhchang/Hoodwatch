package com.example.hoodwatch.hoodwatch;

/**
 * Created by norman on 9/3/17.
 */

public class arrayItems {
    private int icons;
    private String texts;

    public arrayItems(int icons, String texts) {
        this.icons = icons;
        this.texts = texts;
    }

    public int getIcon() {
        return icons;
    }

    public void setIcon(int icon) {
        this.icons = icon;
    }

    public String getText() {
        return texts;
    }

    public void setText(String text) {
        this.texts = text;
    }
}
