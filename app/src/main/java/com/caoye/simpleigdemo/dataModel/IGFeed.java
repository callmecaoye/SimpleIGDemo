package com.caoye.simpleigdemo.dataModel;

/**
 * Created by admin on 10/6/16.
 */
public class IGFeed {
    String username;
    String caption;
    String imageUrl;
    String videoUrl;
    String imageHeight;
    int likesCount;
    String profile_picture;
    Long timeStamp;

    public String getUsername() {
        return username;
    }

    public String getCaption() {
        return caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
