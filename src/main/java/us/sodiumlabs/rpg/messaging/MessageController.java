package us.sodiumlabs.rpg.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import us.sodiumlabs.rpg.data.Die;
import us.sodiumlabs.rpg.data.Line;
import us.sodiumlabs.rpg.data.Message;
import us.sodiumlabs.rpg.services.MessageService;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/hello")
    @SendTo("/topic/message")
    public Message greeting(final Message message) {
        final Message retVal = new Message();

        retVal.setUsername(message.getUsername());
        retVal.setPayload("Hello, " + message.getPayload() + "!");

        messageService.persistMessage(retVal);
        return retVal;
    }

    @MessageMapping("/message")
    @SendTo("/topic/message")
    public Message message(final Message message) {
        messageService.persistMessage(message);

        return message;
    }

    @MessageMapping("/roll")
    @SendTo("/topic/message")
    public Message roll(final Die die) {
        final Message message = new Message();

        message.setUsername(die.getUsername());
        message.setPayload(die.rollString());

        messageService.persistMessage(message);
        return message;
    }

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public Line drawLine(final Line line) {
        return line;
    }
}
