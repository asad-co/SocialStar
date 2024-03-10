package com.assadcoorp.socialstar.DataTypes;

public class UserStoryDataType {
    String image;
    long storyat;

    public UserStoryDataType() {
    }

    public UserStoryDataType(String image, long storyat) {
        this.image = image;
        this.storyat = storyat;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryat() {
        return storyat;
    }

    public void setStoryat(long storyat) {
        this.storyat = storyat;
    }
}
