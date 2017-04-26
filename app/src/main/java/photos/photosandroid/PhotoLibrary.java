package photos.photosandroid;

import android.content.Context;
import android.os.Environment;

import java.io.*;
        import java.util.ArrayList;
/**
 *
 * @author Baljit Kaur
 * @author Milan Patel
 *
 * Represents a photo library containing multiple albums
 * Serializes and saves user data
 */
public class PhotoLibrary implements Serializable{
    private static final long serialVersionUID = 1L;
    ArrayList<Album> Albums;
    public static final String storeFile = "library.bin";


    /**
     * Initializes object with an album list
     */
    public PhotoLibrary(){
        Albums=new ArrayList<>();
    }


    public void setAlbums(ArrayList<Album> albums){
        Albums=albums;
    }

}
