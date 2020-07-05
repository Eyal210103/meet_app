package com.example.meetapp.model.message;

public class ImageMessage extends Message {
    String url;

    public ImageMessage(String senderId, String senderDisplayName, String context, String hour, int day, String url) {
        super(senderId, senderDisplayName, context, hour, day);
        this.url = url;
    }

    public ImageMessage(String url) {
        super();
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageMessage{" +
                super.toString() +
                "url='" + url + '\'' +
                '}';
    }
}
