package com.techniary.nepaltoday;

/**
 * Created by sanjiv on 1/1/18.
 */

public class Posts {

    private String Caption;
    private String Image;
    private String Time;
    private String Username;
    private String UserPhoto;

    public Posts()
    {

    }



    public Posts(String caption, String image, String time, String username, String UserPhoto) {
        Caption = caption;
        Image = image;
        Time = time;
        Username = username;
        this.UserPhoto = UserPhoto;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getUserPhoto() {
        return UserPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        UserPhoto = userPhoto;
    }
}
