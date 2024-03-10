package com.assadcoorp.socialstar.DataTypes;

import java.util.ArrayList;

public class StoryDataType {
    String storyby;
    long storyat;
    ArrayList<UserStoryDataType> stories;

    public StoryDataType() {
    }

    public String getStoryby() {
        return storyby;
    }

    public void setStoryby(String storyby) {
        this.storyby = storyby;
    }

    public long getStoryat() {
        return storyat;
    }

    public void setStoryat(long storyat) {
        this.storyat = storyat;
    }

    public ArrayList<UserStoryDataType> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStoryDataType> stories) {
        this.stories = stories;
    }
}
