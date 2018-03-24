package com.debd.kgp.dsmusic.model;

public class SideNavItem {
    private String data;
    private int imageDrawable;

    public SideNavItem(String data, int imageDrawable) {
        this.data = data;
        this.imageDrawable = imageDrawable;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }
}
