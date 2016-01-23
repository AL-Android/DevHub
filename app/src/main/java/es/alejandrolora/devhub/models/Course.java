package es.alejandrolora.devhub.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Alejandro on 9/4/15.
 */
@ParseClassName("Course")
public class Course extends ParseObject {

    private String id;
    private String title;
    private Category category;
    private User owner;
    private ParseFile photo;
    private ParseFile icon;
    private boolean isValid;


    public String getId() {
        return getObjectId();
    }

    public String getTitle() {
        return getString("title");
    }

    public Category getCategory() {
        return (Category) getParseObject("category");
    }

    public User getOwner() {
        return (User)getParseObject("owner");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public ParseFile getIcon() {
        return getParseFile("icon");
    }

    public boolean isValid() {
        return getBoolean("isValid");
    }

}