package com.example.queriverse.data;

public class ProfileOtherUser {

    private String name;
    private String email;
    private String profileImage;
    private String follower;
    private String following;
    private String numberOfPost;
    private String about;

    public ProfileOtherUser(String name, String email, String profileImage, String follower, String following, String numberOfPost, String about) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.follower = follower;
        this.following = following;
        this.numberOfPost = numberOfPost;
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getNumberOfPost() {
        return numberOfPost;
    }

    public void setNumberOfPost(String numberOfPost) {
        this.numberOfPost = numberOfPost;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}