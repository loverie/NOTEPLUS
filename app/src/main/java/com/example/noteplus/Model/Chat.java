package com.example.noteplus.Model;

import com.google.firebase.database.PropertyName;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    public Chat(String sender,String receiver,String message,boolean isseen){
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.isseen=isseen;
    }
    public Chat(){
    }
    @PropertyName("sender")
    public String getSender() {
        return sender;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    @PropertyName("receiver")
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
