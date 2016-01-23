package es.alejandrolora.devhub.models;
import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by Alejandro on 9/4/15.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject{

    private String id;
    private String content;
    private Course course;
    private ParseUser owner;


    public String getId() {
        return getObjectId();
    }

    public String getContent() {
        return getString("content");
    }

    public void setContent(String content) {
        put("content", content);
    }

    public Course getCourse() {
        return (Course)getParseObject("course");
    }

    public void setCourse(Course course) {
        put("course", course);
    }

    public User getOwner() {
        return (User)getParseObject("owner");
    }

    public void setOwner(User owner) {
        put("owner", owner);
    }


}
