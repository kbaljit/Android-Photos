package photos.photosandroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Baljit Kaur
 * @author Milan Patel
 *
 *Represents an album of Photos
 */
public class Album implements Serializable{
    private String title;
    private ArrayList<Photo> photos;

    /**
     * Initializes Album with title
     * @param title Name of the album
     */
    public Album(String title){
        this.title = title;
        photos = new ArrayList<>();
    }

    /**
     * Adds a photo to the album
     * @param photo The photo being added
     */
    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    /**
     * Return title of the album
     * @return title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Return at photos in album
     * @return ArrayList of photos
     */
    public ArrayList<Photo> getPhotos(){
        return this.photos;
    }

    /**
     * Returns number of photos
     * @return number of photos
     */
    public int getNumPhotos(){
        return photos.size();
    }

    /**
     * Finds photos that match all tags passed as param
     * @param tags Tags being searched for
     * @return All photos with matching tags
     */
    public ArrayList<Photo> getPhotosByTags(ArrayList<Tag> tags){
        ArrayList<Photo> matches = new ArrayList<>();

        for(int i = 0; i < photos.size(); i++){
            if(photos.get(i).searchTags(tags)){
                matches.add(photos.get(i));
            }
        }
        return matches;
    }

    /**
     * Finds photos within the given range
     * @param date1 Start date
     * @param date2 End date
     * @return All photos within the range
     */
    public ArrayList<Photo> getPhotosByDate(Calendar date1, Calendar date2){
        ArrayList<Photo> matches = new ArrayList<>();

        for(int i = 0; i < photos.size(); i++){
            if((photos.get(i).getDate().after(date1) && photos.get(i).getDate().before(date2)) ||
                    (photos.get(i).getDate().compareTo(date1) == 0) ||
                    (photos.get(i).getDate().compareTo(date2) == 0)){
                matches.add(photos.get(i));
            }
        }
        return matches;
    }

    /**
     * Sets title to param
     * @param text New title
     */
    public void setTitle(String text) {
        this.title = text;
    }

    /**
     * Returns the date range of all photos in the album
     * @return String used to represent the date range
     */
    public String dateRange(){
        Calendar min = photos.get(0).getDate();
        Calendar max = photos.get(0).getDate();
        int minP = 0;
        int maxP = 0;
        for(int i = 1; i < photos.size(); i++){
            if(photos.get(i).getDate().before(min)){
                min = photos.get(i).getDate();
                minP = i;
            }
            if(photos.get(i).getDate().after(max)){
                max = photos.get(i).getDate();
                maxP = i;
            }
        }

        return photos.get(minP).getDateString() + " - "+ photos.get(maxP).getDateString();
    }
}

