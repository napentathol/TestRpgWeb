package us.sodiumlabs.rpg.services;

import org.springframework.stereotype.Service;
import us.sodiumlabs.rpg.messaging.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultMessageService implements MessageService {
    private static final int MAX_MESSAGES =  150;

    private final List<Message> messages = new ArrayList<Message>();

    @Override
    public void persistMessage(final Message message) {
        messages.add(message);

        if(messages.size() > MAX_MESSAGES) messages.remove(0);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }
}
