package com.techniary.nepaltoday;



public class YoutubeTrends {


    private String publishedDate;
    private String ChannelTitle;
    private String thumbnail;
    private String videoID;
    private String videoTitle;


    public YoutubeTrends(String publishedDate, String channelTitle, String thumbnail, String videoID, String videoTitle) {
        this.publishedDate = publishedDate;
        ChannelTitle = channelTitle;
        this.thumbnail = thumbnail;
        this.videoID = videoID;
        this.videoTitle = videoTitle;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getChannelTitle() {
        return ChannelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        ChannelTitle = channelTitle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }


}
