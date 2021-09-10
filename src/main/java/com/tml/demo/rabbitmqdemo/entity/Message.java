package com.tml.demo.rabbitmqdemo.entity;

import java.util.Date;

public class Message {
    private String name;

    private Date date;

    public Message(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public Message() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}