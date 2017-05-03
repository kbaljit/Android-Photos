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

    /**
     * Sets the last searched album
     * @param a
     */
    public void setLastSearch(Album a){
        lastSearch = a;
    }

    /**
     * Gets the last searched album
     * @return
     */
    public Album getLastSearch(){
        return lastSearch;
    }

    /**
     * Sets Album List to given Albums list
     * @param Albums
     */
    public void setAlbums(ArrayList<Album> Albums){

        this.Albums=Albums;
    }

    /**
     * Gets Albums List
     * @return
     */
    public ArrayList<Album> getAlbums(){
        if(Albums==null){
            return new ArrayList<Album>();
        }
        return Albums;
    }

}
