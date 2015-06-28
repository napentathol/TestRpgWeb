package us.sodiumlabs.rpg.services;

import us.sodiumlabs.rpg.data.Line;

import java.io.IOException;

public interface ImageService {
    void drawLine(Line line);

    void clear();

    String imageAsBase64() throws IOException;
}
