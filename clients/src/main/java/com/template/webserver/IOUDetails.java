package com.template.webserver;

public class IOUDetails {
    private int value;
    private String otherParty;
    private String partyToShare;

    public IOUDetails(int value, String otherParty, String partyToShare) {
        this.value = value;
        this.otherParty = otherParty;
        this.partyToShare = partyToShare;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getOtherParty() {
        return otherParty;
    }

    public void setOtherParty(String otherParty) {
        this.otherParty = otherParty;
    }

    public String getPartyToShare() {
        return partyToShare;
    }

    public void setPartyToShare(String partyToShare) {
        this.partyToShare = partyToShare;
    }
}
