package com.nivgelbermann.fooddiarydemo.data.sqlite_to_be_deprecated;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = 1603102270117255964L;

    private final String m_Id;
    private String mName;
    private String mColor;

    public Category(String id, String name, String color) {
        m_Id = id;
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
