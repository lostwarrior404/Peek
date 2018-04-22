package com.example.tushar.mc_final;

/**
 * Created by dell on 4/22/2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageLocation;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageLocation, String messageUser) {
        this.messageText = messageText;
        this.messageLocation = messageLocation;
        this.messageUser = messageUser;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageLocation() {
        return messageLocation;
    }

    public void setMessageLocation(String messageLocation) {
        this.messageLocation = messageLocation;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
