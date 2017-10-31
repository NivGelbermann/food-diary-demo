package com.nivgelbermann.fooddiarydemo.data;

import java.io.Serializable;

/**
 * Created by Niv on 31-Oct-17.
 */

public class Category implements Serializable {
    private static final long serialVersionUID = 1603102270117255964L;

    private final String m_Id;
    private String mName;
    private String mColor;

    public Category(String m_Id, String name, String color) {
        this.m_Id = m_Id;
        mName = name;
        mColor = color;
    }

    public String getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    @Override
    public String toString() {
        return "Category{" +
                "_id='" + m_Id + '\'' +
                ", name='" + mName + '\'' +
                ", color='" + mColor + '\'' +
                '}';
    }
}
