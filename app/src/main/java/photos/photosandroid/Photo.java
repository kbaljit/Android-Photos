package photos.photosandroid;

import java.io.File;
import java.io.Serializable;
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
    private File image;
    private Calendar date;
    private String title;
    private ArrayList<Tag> tags;

    /**
     * Initializes a photo object with given image
     * @param image Image file
     */
    public Photo(File image){
        this.image = image;
        date = Calendar.getInstance();
        Date d = new Date(image.lastModified());
        date.setTime(d);
        date.set(Calendar.MILLISECOND, 0);
        tags = new ArrayList<>();
        title = image.getName();
    }

    /**
     * Returns the image file
     * @return Image file
     */
    public File getImage(){
        return this.image;
    }

    /**
     * Returns caption of photo
     * @return The caption of photo
     */
    public String getTitle(){
        return title;
    }

    /**
     * Returns date the photo was taken
     * @return Date of photo
     */
    public Calendar getDate(){
        return date;
    }

    /**
     * Return the date as string
     * @return date as string
     */
    public String getDateString(){
        return (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DAY_OF_MONTH) + "/" +
                date.get(Calendar.YEAR) + "";
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

