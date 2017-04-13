package com.github.jowitam.security;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersion = 1L;

    private byte[] r1;
    private byte[] r2;
    private byte message;
    private byte[] hashMessage;

    public byte[] getR1() {
        return r1;
    }

    public void setR1(byte[] r1) {
        this.r1 = r1;
    }

    public byte[] getR2() {
        return r2;
    }

    public void setR2(byte[] r2) {
        this.r2 = r2;
    }

    public byte getMessage() {
        return message;
    }

    public void setMessage(byte message) {
        this.message = message;
    }

    public byte[] getHashMessage() {
        return hashMessage;
    }

    public void setHashMessage(byte[] hashMessage) {
        this.hashMessage = hashMessage;
    }
}
