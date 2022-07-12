package com.example.mccchatapp.models;

import java.io.Serializable;

public class User implements Serializable {

    public String name, image, phone, email, token, id, typing;

    public User(){

    }

    public User(String typing) {
        this.typing = typing;
    }

    public String getTyping() {
        return typing;
    }

    public void setTyping(String typing) {
        this.typing = typing;
    }
}
