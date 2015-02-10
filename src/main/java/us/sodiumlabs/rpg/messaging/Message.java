package us.sodiumlabs.rpg.messaging;

public class Message {

    private String username;

    private String payload;

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
