package com.example.queriverse.data;

public class UserPost {

    private int authorImage;
    private String authorName;
    private String postDate;
    private String postDescription;
    private int postIV;
    private String postLikes;
    private String postDislikes;
    private String postComments;

    public UserPost(int authorImage, String authorName, String postDate, String postDescription, int postIV, String postLikes, String postDislikes, String postComments) {
        this.authorImage = authorImage;
        this.authorName = authorName;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.postIV = postIV;
        this.postLikes = postLikes;
        this.postDislikes = postDislikes;
        this.postComments = postComments;
    }


    public int getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(int authorImage) {
        this.authorImage = authorImage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public int getPostIV() {
        return postIV;
    }

    public void setPostIV(int postIV) {
        this.postIV = postIV;
    }

    public String getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(String postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostDislikes() {
        return postDislikes;
    }

    public void setPostDislikes(String postDislikes) {
        this.postDislikes = postDislikes;
    }

    public String getPostComments() {
        return postComments;
    }

    public void setPostComments(String postComments) {
        this.postComments = postComments;
    }
}
