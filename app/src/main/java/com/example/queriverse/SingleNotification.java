package com.example.queriverse;

public class SingleNotification {
    private final String type;
    private final String title;
    private final String content;

    public SingleNotification(String type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    public String getType() {return type; }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
