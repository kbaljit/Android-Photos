package photos.photosandroid;

import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Baljit Kaur
 * @author Milan Patel
 *
 *Represents an Photo object with image, date, caption, and tags
 */
public class Photo implements Serializable{
    private String bitmap;
    private String title;
    private ArrayList<Tag> tags;

    /**
     * Initializes a photo object with given image
     * @param bitmap
     */
    public Photo(String bitmap){
        this.bitmap = bitmap;
        tags = new ArrayList<>();
    }

    /**
     * Returns the image file
     * @return Image file
     */
    public String getImage(){

        return this.bitmap;
    }

    /**
     * Returns caption of photo
     * @return The caption of photo
     */
    public String getTitle(){

        return title;
    }


    /**
     * Adds new tag to photo
     * @param tag Tag object
     * @return true if successful
     */
    public boolean addTag(Tag tag){
        //check if tag already exists
        for(int i = 0; i < tags.size(); i++){
            if(tag.getTagName() == tags.get(i).getTagName() &&
                    tag.getTagValue() == tags.get(i).getTagValue()){
                return false;
            }
        }
        tags.add(tag);
        return true;
    }

    /**
     * Returns all tags of Photo
     * @return Tags
     */
    public ArrayList<Tag> getTags(){

        return tags;
    }

    /**
     * Sets tags
     * @param Tag ArrayList of tags
     */
    public void setTags(ArrayList<Tag> Tag){

        this.tags=Tag;
    }

    /**
     * Searches a given tag
     * @param tag Tag to be searched
     * @return true if match found
     */
    public boolean searchTag(Tag tag){
        for(int i = 0; i < tags.size(); i++){
            if(tags.get(i).equals(tag)){
                return true;
            }
        }
        return false;
    }

    /**
     * Searches a list of tags
     * @param searchTags ArrayList of tags
     * @return true if all tags were found
     */
    public boolean searchTags(ArrayList<Tag> searchTags){
        for(int i = 0; i < searchTags.size(); i++){
            if(!(searchTag(searchTags.get(i)))) {
                return false;
            }
        }
        return true;
    }
}

