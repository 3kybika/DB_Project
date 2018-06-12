package db.demo.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageModel {
    private String message;

    @JsonCreator
    public MessageModel(
            @JsonProperty("message") String message
    ) {
        this.message = message;
    }

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
