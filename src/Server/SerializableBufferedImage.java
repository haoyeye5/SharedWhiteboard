package Server;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

/***** This class transmit the BufferedImage provided by java into a Serializable type so that it can be unmarshalled and marshalled 
 *     by the components in the remote method invocation mechanisms *****/

public class SerializableBufferedImage implements Serializable {
    private static final long serialVersionUID = 1L;
	private transient BufferedImage image; // transient to exclude from serialization
	private final int CANVAS_WIDTH = 563;
	private final int CANVAS_HEIGHT = 405;
    
    public SerializableBufferedImage(BufferedImage image) {
        this.image = image;
    }
    
    // Getter for image
    public BufferedImage getImage() {
        return image;
    }
    
    // Write object to output stream
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Write non-transient fields
        // Convert BufferedImage to byte array and write to stream
        ImageIO.write(image, "png", out); // Use appropriate image format
    }
    
    // Read object from input stream
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Read non-transient fields
        // Read byte array from stream and convert to BufferedImage
        image = ImageIO.read(in);
    }
    
    // clear the canvas, that is, add a blank image
    public void clear() {
    	image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }
}
