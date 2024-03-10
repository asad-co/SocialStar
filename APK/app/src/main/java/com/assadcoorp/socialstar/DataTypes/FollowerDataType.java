package com.assadcoorp.socialstar.DataTypes;

public class FollowerDataType {
    private String followedby;
    private long time;

    public FollowerDataType() {
    }

    public String getFollowedby() {
        return followedby;
    }

    public void setFollowedby(String followedby) {
        this.followedby = followedby;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
