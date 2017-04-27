package photos.photosandroid;

import java.io.Serializable;

/**
 *
 * @author Baljit Kaur
 * @author Milan Patel
 *
 * Represents a Tag on a photo
 */
public class Tag implements Serializable{
    private String tagName;
    private String tagValue;

    /**
     * Initializes Tag object
     * @param tagName Type of tag
     * @param tagValue Value given to tag
     */
    public Tag(String tagName, String tagValue){
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    /**
     * Return tag name
     * @return Tag name
     */
    public String getTagName(){
        return tagName;
    }

    /**
     * Returns tag value
     * @return Tag value
     */
    public String getTagValue(){
        return tagValue;
    }

    /**
     * Sets new tag name
     * @param newTagName New tag name
     */
    public void setTagName(String newTagName){

        this.tagName = newTagName;
    }

    /**
     * Sets new tag value
     * @param newTagValue New tag value
     */
    public void setTagValue(String newTagValue){

        this.tagName = newTagValue;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }

        if(!(o instanceof Tag)){
            return false;
        }

        Tag tag = (Tag) o;

        return ((this.tagName.equals(tag.getTagName())) &&
                (this.tagValue.equals(tag.getTagValue())));
    }
}
