package us.sodiumlabs.rpg.services;

import us.sodiumlabs.rpg.messaging.Message;

import java.util.List;

public interface MessageService {
    void persistMessage(Message message);

    List<Message> getMessages();
}
