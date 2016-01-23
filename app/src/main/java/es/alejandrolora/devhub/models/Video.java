package es.alejandrolora.devhub.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Alejandro on 9/4/15.
 */

@ParseClassName("Video")
public class Video extends ParseObject {

    private String id;
    private String title;
    private String code;
    private Course course;
    private ParseFile photo;
    private int lesson;
    private int duration;

    public String getId() {
        return getObjectId();
    }

    public String getTitle() {
        return getString("title");
    }

    public String getCode() {
        return getString("code");
    }

    public Course getCourse() {
        return (Course) getParseObject("course");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public int getLesson() {
        return (int) getNumber("lesson");
    }

    public int getDuration() {
        return (int) getNumber("duration");
    }

}
