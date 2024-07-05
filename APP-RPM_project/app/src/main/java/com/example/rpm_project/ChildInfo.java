package com.example.rpm_project;

import com.google.gson.annotations.SerializedName;

public class ChildInfo {
    @SerializedName("childName")
    private String childName;

    @SerializedName("childAge")
    private int childAge;

    @SerializedName("childHeight")
    private int childHeight;

    public ChildInfo(String childName, int childAge, int childHeight) {
        this.childName = childName;
        this.childAge = childAge;
        this.childHeight = childHeight;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildAge() {
        return childAge;
    }

    public void setChildAge(int childAge) {
        this.childAge = childAge;
    }

    public int getChildHeight() {
        return childHeight;
    }

    public void setChildHeight(int childHeight) {
        this.childHeight = childHeight;
    }
}
