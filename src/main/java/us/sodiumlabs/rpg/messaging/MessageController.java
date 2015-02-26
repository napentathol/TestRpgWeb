package us.sodiumlabs.rpg.messaging;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/hello")
    @SendTo("/topic/message")
    public Message greeting(final Message message) {
        final Message retVal = new Message();

        retVal.setUsername(message.getUsername());
        retVal.setPayload("Hello, " + message.getPayload() + "!");

        return retVal;
    }

    @MessageMapping("/message")
    @SendTo("/topic/message")
    public Message message(final Message message) {
        return message;
    }

    @MessageMapping("/roll")
    @SendTo("/topic/message")
    public Message roll(final Die die) {
        final Message message = new Message();

        message.setUsername(die.getUsername());
        message.setPayload(die.rollString());

        return message;
    }

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public Line drawLine(final Line line) {
        return line;
    }
}
