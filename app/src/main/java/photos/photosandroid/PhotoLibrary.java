package photos.photosandroid;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.lang.reflect.Array;
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
    private ArrayList<Album> Albums;
    private Album lastSearch;

    /**
     * Initializes object with an album list
     */
    public PhotoLibrary(){

        this.Albums=new ArrayList<>();
    }

    public void setLastSearch(Album a){
        lastSearch = a;
    }

    public Album getLastSearch(){
        return lastSearch;
    }

    public void setAlbums(ArrayList<Album> Albums){

        this.Albums=Albums;
    }

    public ArrayList<Album> getAlbums(){
        if(Albums==null){
            return new ArrayList<Album>();
        }
        return Albums;
    }

}
