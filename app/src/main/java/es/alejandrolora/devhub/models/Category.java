package es.alejandrolora.devhub.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Alejandro on 9/4/15.
 */
@ParseClassName("Category")
public class Category extends ParseObject {

    private String id;
    private String name;
    private ParseFile icon;
    private String color;

    public String getId() {
        return getObjectId();
    }

    public String getName() {
        return getString("name");
    }

    public ParseFile getIcon() {
        return getParseFile("icon");
    }

    public String getColor() {
        return getString("color");
    }

}
