package us.sodiumlabs.rpg.services;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import us.sodiumlabs.rpg.data.Line;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DefaultImageService implements ImageService {

    private BufferedImage image;

    private Graphics2D graphics;

    public DefaultImageService(final int w, final int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        graphics = image.createGraphics();
    }

    @Override
    public void drawLine(final Line line) {
        graphics.setColor(new Color(line.getColor()));
        graphics.drawLine(line.getX(), line.getY(), line.getNx(), line.getNy());
    }

    @Override
    public void clear() {
        graphics.setColor(Color.WHITE);
        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());
    }

    @Override
    public String imageAsBase64() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", out);
        final byte[] bytes = out.toByteArray();

        return Base64.encode(bytes);
    }
}
