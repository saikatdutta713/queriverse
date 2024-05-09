package com.example.queriverse.data;

public class UserComment {
    private String userId;
    private String commentauthorImageUrl;
    private String commenttauthorName;
    private String commentDate;
    private String commentDescription;
    private String commentImageUrl; // New field for post image URL
    private String commentLikes;
    private String commentDislikes;



    public UserComment( String userId, String commentauthorImageUrl, String commenttauthorName, String commentDate, String commentDescription, String commentLikes, String commentDislikes) {
        this.userId = userId;
        this.commentauthorImageUrl = commentauthorImageUrl;
        this.commenttauthorName = commenttauthorName;
        this.commentDate = commentDate;
        this.commentDescription = commentDescription;
        this.commentImageUrl = commentImageUrl;
        this.commentLikes = commentLikes;
        this.commentDislikes = commentDislikes;
    }
    public String getUserId() {
        return userId;
    }



    public String getCommentauthorImageUrl() {
        return commentauthorImageUrl;
    }

    // Corrected setter method for commentauthorImageUrl
    public void setCommentauthorImageUrl(String commentAuthorImageUrl) {
        this.commentauthorImageUrl = commentAuthorImageUrl;
    }


    public String getCommenttauthorName() {
        return commenttauthorName;
    }

    public void setCommenttauthorName(String commenttauthorName) {
        this.commenttauthorName = commenttauthorName;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public void setCommentDescription(String commentDescription) {
        this.commentDescription = commentDescription;
    }

    public String getCommentImageUrl() {
        return commentImageUrl;
    }

    public void setCommentImageUrl(String commentImageUrl) {
        this.commentImageUrl = commentImageUrl;
    }

    public String getCommentLikes() {
        return commentLikes;
    }

    public void setCommentLikes(String commentLikes) {
        this.commentLikes = commentLikes;
    }

    public String getCommentDislikes() {
        return commentDislikes;
    }

    public void setCommentDislikes(String commentDislikes) {
        this.commentDislikes = commentDislikes;
    }
}
