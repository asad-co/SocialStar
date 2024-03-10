package com.assadcoorp.socialstar.DataTypes;

public class CommentDataType {
    String comment,commentedby,commentID;
    long commentedat;
    int commentlike,commentdislike;

    public CommentDataType() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentedby() {
        return commentedby;
    }

    public void setCommentedby(String commentedby) {
        this.commentedby = commentedby;
    }

    public long getCommentedat() {
        return commentedat;
    }

    public void setCommentedat(long commentedat) {
        this.commentedat = commentedat;
    }

    public int getCommentlike() {
        return commentlike;
    }

    public void setCommentlike(int commentlike) {
        this.commentlike = commentlike;
    }

    public int getCommentdislike() {
        return commentdislike;
    }

    public void setCommentdislike(int commentdislike) {
        this.commentdislike = commentdislike;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
