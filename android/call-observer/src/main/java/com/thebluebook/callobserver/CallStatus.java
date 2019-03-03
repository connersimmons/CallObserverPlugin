package com.thebluebook.callobserver;

/**
 * Created by Conner Simmons on 3/2/19.
 * The Blue Book Building & Construction Network
 * csimmons@mail.thebluebook.com
 */
public enum CallStatus {
    NONE("none"),
    INCOMING("incoming"),
    DIALING("dialing"),
    ONGOING("ongoing"),
    ENDED("ended"),
    MISSED("missed");

    private String stringValue;
    private CallStatus(String toString) {
        stringValue = toString;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
