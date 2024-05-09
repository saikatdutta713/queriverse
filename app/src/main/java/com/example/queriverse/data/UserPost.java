package com.example.queriverse.data;

public class UserPost {
    private String postId;
    private String userId;
    private String authorImageUrl;
    private String authorName;
    private String postDate;
    private String postDescription;
    private String postImageUrl; // New field for post image URL
    private String postLikes;
    private String postDislikes;
    private String postComments;
    private boolean likedByUser;
    private boolean dislikedByUser;


    public UserPost(String postId, String userId, String authorImageUrl, String authorName, String postDate, String postDescription, String postImageUrl, String postLikes, String postDislikes, String postComments) {
        this.postId = postId;
        this.userId = userId;
        this.authorImageUrl = authorImageUrl;
        this.authorName = authorName;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.postImageUrl = postImageUrl;
        this.postLikes = postLikes;
        this.postDislikes = postDislikes;
        this.postComments = postComments;
        this.likedByUser = likedByUser;
        this.dislikedByUser = dislikedByUser;
    }

    public String getUserId() {
        return userId;
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImageUrl = authorImageUrl;
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

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
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

    public String generatePostLink() {
        return "https://queriverse.bytelure.in/api/posts" + postId;
    }

    // Getter and setter methods for postId if not already implemented
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }
    public boolean isDislikedByUser() {
        return dislikedByUser;
    }

    public void setDislikedByUser(boolean dislikedByUser) {
        this.dislikedByUser = dislikedByUser;
    }
}
