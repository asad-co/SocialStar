package com.assadcoorp.socialstar.DataTypes;

public class PostDataType {
    String postID,postedbyID;
    String postimage,postdescription;
    long postedat;
    int postlike,postdislike,comment,share;


    public PostDataType() {
    }

    public PostDataType(String postID, String postedbyID, String postimage, String postdescription, long postedat) {
        this.postID = postID;
        this.postedbyID = postedbyID;
        this.postimage = postimage;
        this.postdescription = postdescription;
        this.postedat = postedat;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostedbyID() {
        return postedbyID;
    }

    public void setPostedbyID(String postedbyID) {
        this.postedbyID = postedbyID;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public long getPostedat() {
        return postedat;
    }

    public void setPostedat(long postedat) {
        this.postedat = postedat;
    }

    public int getPostlike() {
        return postlike;
    }

    public void setPostlike(int postlike) {
        this.postlike = postlike;
    }

    public int getPostdislike() {
        return postdislike;
    }

    public void setPostdislike(int postdislike) {
        this.postdislike = postdislike;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }
}
