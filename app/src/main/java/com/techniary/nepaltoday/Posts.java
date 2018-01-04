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
    private String TotalReactions;
    private String CurrentUserReaction;
    private String Unique;


    public Posts(String caption, String image, String time, String username, String userPhoto, String totalReactions, String currentUserReaction, String Unique) {
        Caption = caption;
        Image = image;
        Time = time;
        Username = username;
        UserPhoto = userPhoto;
        TotalReactions = totalReactions;
        CurrentUserReaction = currentUserReaction;
        this.Unique= Unique;
    }


    public Posts()
    {

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

    public String getTotalReactions() {
        return TotalReactions;
    }

    public void setTotalReactions(String totalReactions) {
        TotalReactions = totalReactions;
    }

    public String getCurrentUserReaction() {
        return CurrentUserReaction;
    }

    public void setCurrentUserReaction(String currentUserReaction) {
        CurrentUserReaction = currentUserReaction;
    }

    public String getUnique() {
        return Unique;
    }

    public void setUnique(String unique) {
        this.Unique = unique;
    }
}




















