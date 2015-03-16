package us.sodiumlabs.rpg.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.sodiumlabs.rpg.messaging.Message;
import us.sodiumlabs.rpg.services.MessageService;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageEndpoint {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Message> getMessages() {
        return messageService.getMessages();
    }
}
