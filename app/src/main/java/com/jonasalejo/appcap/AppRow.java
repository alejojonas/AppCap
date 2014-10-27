package com.jonasalejo.appcap;

import android.graphics.drawable.Drawable;

public class AppRow {
    public String appname = "";
    public Drawable icon = null;
    private boolean selected;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon){
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean isChecked) {
        this.selected = isChecked;
    }

}