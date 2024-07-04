package com.example.rpm_project;

public class ChildInfo {
    private String childName;
    private int childAge;
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
