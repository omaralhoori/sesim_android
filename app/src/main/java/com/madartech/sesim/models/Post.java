package com.madartech.sesim.models;

public class Post {
    private String postid;
    private String postAudio;
    private String postimage;
    private String publisher;

    public Post(){}

    public Post(String postid, String postAudio, String postimage, String publisher) {
        this.postid = postid;
        this.postAudio = postAudio;
        this.postimage = postimage;
        this.publisher = publisher;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostAudio() {
        return postAudio;
    }

    public void setPostAudio(String postAudio) {
        this.postAudio = postAudio;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
