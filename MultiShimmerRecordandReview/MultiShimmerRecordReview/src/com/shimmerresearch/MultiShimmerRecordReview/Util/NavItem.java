package com.shimmerresearch.MultiShimmerRecordReview.Util;

/**
 * Created by joe on 22/06/15.
 */
public class NavItem {
    private String text;
    private int icon;

    public NavItem(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
