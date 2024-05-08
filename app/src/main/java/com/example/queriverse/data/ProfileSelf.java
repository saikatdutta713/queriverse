package com.example.queriverse.data;

public class ProfileSelf {
    private String nameSelf;
    private String emailSelf;
    private String profileImageSelf;
    private String followerSelf;
    private String followingSelf;
    private String numberOfPostSelf;
    private String aboutSelf;

    public ProfileSelf(String nameSelf, String emailSelf, String profileImageSelf, String followerSelf, String followingSelf, String numberOfPostSelf, String aboutSelf) {
        this.nameSelf = nameSelf;
        this.emailSelf = emailSelf;
        this.profileImageSelf = profileImageSelf;
        this.followerSelf = followerSelf;
        this.followingSelf = followingSelf;
        this.numberOfPostSelf = numberOfPostSelf;
        this.aboutSelf = aboutSelf;
    }

    public String getNameSelf() {
        return nameSelf;
    }

    public void setNameSelf(String nameSelf) {
        this.nameSelf = nameSelf;
    }

    public String getEmailSelf() {
        return emailSelf;
    }

    public void setEmailSelf(String emailSelf) {
        this.emailSelf = emailSelf;
    }

    public String getProfileImageSelf() {
        return profileImageSelf;
    }

    public void setProfileImageSelf(String profileImageSelf) {
        this.profileImageSelf = profileImageSelf;
    }

    public String getFollowerSelf() {
        return followerSelf;
    }

    public void setFollowerSelf(String followerSelf) {
        this.followerSelf = followerSelf;
    }

    public String getFollowingSelf() {
        return followingSelf;
    }

    public void setFollowingSelf(String followingSelf) {
        this.followingSelf = followingSelf;
    }

    public String getNumberOfPostSelf() {
        return numberOfPostSelf;
    }

    public void setNumberOfPostSelf(String numberOfPostSelf) {
        this.numberOfPostSelf = numberOfPostSelf;
    }

    public String getAboutSelf() {
        return aboutSelf;
    }

    public void setAboutSelf(String aboutSelf) {
        this.aboutSelf = aboutSelf;
    }
}
