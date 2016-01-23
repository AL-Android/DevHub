package es.alejandrolora.devhub.Api;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import java.util.LinkedList;
import java.util.List;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.activitys.LoginActivity;
import es.alejandrolora.devhub.activitys.SignUpActivity;
import es.alejandrolora.devhub.fragments.CommentsTab;
import es.alejandrolora.devhub.fragments.VideosTab;
import es.alejandrolora.devhub.models.Category;
import es.alejandrolora.devhub.models.Comment;
import es.alejandrolora.devhub.models.Course;
import es.alejandrolora.devhub.models.Score;
import es.alejandrolora.devhub.models.User;
import es.alejandrolora.devhub.models.Video;

/**
 * Created by Alejandro on 13/4/15.
 */
public class API {

    // Login
    public static void checkLogin(String email, final String pass) {

        ParseUser.logInInBackground(email, pass, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    if (parseUser.getBoolean("emailVerified")) {
                        LoginActivity.LoginOk();
                    } else {
                        LoginActivity.checkLoginEmailNoVerified();
                    }
                } else if(e.getCode() == 100) {
                    LoginActivity.notNetworkAvailable();
                }else{
                    LoginActivity.checkLoginCancel();
                }
            }
        });
    }

    public static void signUpNewUser(String email, String pass) {

        User mUser = new User();
        mUser.setPassword(pass);
        mUser.setEmail(email);

        mUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    SignUpActivity.SignUpOk();
                } else if (e.getCode() == 202) {
                    SignUpActivity.EmailIsRegistered();
                } else {
                    SignUpActivity.SignUpError();
                    // TODO enviarme el codigo de error por correo
                }
            }
        });
    }


    // Users
    public static void deleteCurrentUser(){

        User currentUser = (User) ParseUser.getCurrentUser();

        deleteAllCommentsByUserId(currentUser.getObjectId());
        deleteAllCoursesByUserId(currentUser.getObjectId());
        deleteAllScoresByUserId(currentUser.getObjectId());

        currentUser.deleteInBackground();
        currentUser.logOut();
    }


    // Categories
    public static List<Category> getAllCategoriesFromAPI() {

        final List<Category> list = new LinkedList<Category>();

        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < categories.size(); i++) {
                        list.add(categories.get(i));
                    }
                }
            }
        });
        return list;
    }

    public static void numberCoursesByCategory(final Context context, final String idCategory, final TextView details) {

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        Category categoryTemp = ParseObject.createWithoutData(Category.class, idCategory);
        query.whereEqualTo("category", categoryTemp);
        query.whereEqualTo("isValid", true);
        int number = 0;
        try {
            number = query.count();
            showCoursesByCategory(context, number, details);
        } catch (ParseException e) {
            Log.i("ERROR", "ParseException msg: " + e.getMessage());
            Toast.makeText(context, context.getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
        }
    }

    public static void numberVideosByCategory(final Context context, final String idCategory, final TextView details) {

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        Category catTemp = new Category();
        catTemp.setObjectId(idCategory);
        query.whereEqualTo("category", catTemp);
        query.whereEqualTo("isValid", true);
        query.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> courses, ParseException e) {
                List<String> idCourses = new LinkedList<String>();
                if (e == null) {
                    for (Course course : courses) {
                        idCourses.add(course.getId());
                    }
                    ParseQuery innerQuery = ParseQuery.getQuery(Course.class);
                    innerQuery.whereContainedIn("objectId", idCourses);

                    ParseQuery mQuery = ParseQuery.getQuery(Video.class);
                    mQuery.whereMatchesQuery("course", innerQuery);
                    mQuery.countInBackground(new CountCallback() {
                        @Override
                        public void done(int numberOfVideos, ParseException e) {
                            if (numberOfVideos == 0) {
                                details.setText(details.getText() + "0 " + context.getString(R.string.videos_string));
                            } else {
                                showVideosByCategory(context, numberOfVideos, details);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void showCoursesByCategory(Context context, int number, TextView details) {
        if (details.getText().toString().isEmpty()) {
            if (number == 1) {
                details.setText(number + " " + context.getString(R.string.course_string) + " - ");
            } else {
                details.setText(number + " " + context.getString(R.string.courses_string) + " - ");
            }
        } else {
            if (number == 1) {
                details.setText(number + " " + context.getString(R.string.course_string) + " - " + details.getText());
            } else {
                details.setText(number + " " + context.getString(R.string.courses_string) + " - " + details.getText());
            }
        }
    }

    public static void showVideosByCategory(Context context, int number, TextView details) {
        if (details.getText().toString().isEmpty()) {
            if (number == 1) {
                details.setText(number + " " + context.getString(R.string.video_string));
            } else {
                details.setText(number + " " + context.getString(R.string.videos_string));
            }
        } else {
            if (number == 1) {
                details.setText(details.getText() + "" + number + " " + context.getString(R.string.video_string));
            } else {
                details.setText(details.getText() + "" + number + " " + context.getString(R.string.videos_string));
            }
        }
    }

    // Courses
    public static List<Course> getAllCoursesFromAPI() {

        final List<Course> list = new LinkedList<Course>();

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> courses, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < courses.size(); i++) {
                        list.add(courses.get(i));
                    }
                }
            }
        });
        return list;
    }

    public static Course getCourseById(String id) {

        List<Course> courseReturn = null;

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.whereEqualTo("objectId", id);
        try {
            courseReturn = query.find();
        } catch (ParseException e) {
            // TODO Implement error
        }
        if (courseReturn.size() == 1) {
            return courseReturn.get(0);
        }
        return null;
    }

    public static List<Course> getAllCoursesByCategoryFromAPI(String idCategory) {

        final List<Course> list = new LinkedList<Course>();

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        Category categoryTemp = ParseObject.createWithoutData(Category.class, idCategory);
        query.whereEqualTo("category", categoryTemp);
        query.whereEqualTo("isValid", true);
        query.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> courses, ParseException e) {
                if(e == null){
                    for (Course c : courses) {
                        list.add(c);
                    }
                }
            }
        });
        return list;
    }

    public static boolean isCourseEmpty(String idCategory) {

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        query.whereEqualTo("category", Course.createWithoutData(Category.class, idCategory));
        try {
            if (query.count() > 0)
                return false;
            else
                return true;
        } catch (ParseException e) {
            return true;
        }
    }

    public static void getDetailsForCourse(final Context context, final String idOwner, final String idCourse, final TextView details) {

        final User u = new User();

        ParseQuery<User> q = ParseQuery.getQuery(User.class);
        q.whereEqualTo("objectId", idOwner);
        q.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {

                if (e == null && users.size() == 1) {
                    u.setUserName(users.get(0).getUserName());
                    // Get the number of the videos inside of a course and the Owner name
                    ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
                    Course courseTemp = new Course();
                    courseTemp.setObjectId(idCourse);
                    query.whereEqualTo("course", courseTemp);
                    query.findInBackground(new FindCallback<Video>() {
                        @Override
                        public void done(List<Video> videos, ParseException ex) {
                            int wo = videos.size();
                            if (ex == null) {
                                if (wo == 1) {
                                    details.setText(context.getString(R.string.by) +
                                            " " + u.getUserName() + " - " + wo + " " + context.getString(R.string.video_string));
                                } else {
                                    details.setText(context.getString(R.string.by) +
                                            " " + u.getUserName() + " - " + wo + " " + context.getString(R.string.videos_string));
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public static void deleteAllCoursesByUserId(String id){

        ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
        User temp = ParseObject.createWithoutData(User.class, id);
        query.whereEqualTo("owner", temp);
        query.findInBackground(new FindCallback<Course>() {
            @Override
            public void done(List<Course> list, ParseException e) {
                if (e == null){
                    for (Course c : list){
                        c.deleteInBackground();
                        deleteAllVideosByCourseId(c.getObjectId());
                    }
                }
            }
        });
    }


    // Videos
    public static List<Video> getAllVideosFromAPI() {

        final List<Video> list = new LinkedList<Video>();

        ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
        query.findInBackground(new FindCallback<Video>() {
            @Override
            public void done(List<Video> videos, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < videos.size(); i++) {
                        list.add(videos.get(i));
                    }
                }
            }
        });
        return list;
    }

    public static List<Video> getAllVideosFromCourse(String idCourse) {
        final List<Video> list = new LinkedList<Video>();

        ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
        Course courseTemp = new Course();
        courseTemp.setObjectId(idCourse);
        query.whereEqualTo("course", courseTemp);
        query.orderByAscending("lesson");
        query.findInBackground(new FindCallback<Video>() {
            @Override
            public void done(List<Video> videos, ParseException e) {
                if(e == null){
                    for (int i = 0; i < videos.size(); i++) {
                        list.add(videos.get(i));
                    }
                }
            }
        });
        return list;
    }

    public static void deleteAllVideosByCourseId(String id){

        ParseQuery<Video> query = ParseQuery.getQuery(Video.class);
        Course temp = ParseObject.createWithoutData(Course.class, id);
        query.whereEqualTo("course", temp);
        query.findInBackground(new FindCallback<Video>() {
            @Override
            public void done(List<Video> list, ParseException e) {
                if (e == null) {
                    for (Video v : list) {
                        v.deleteInBackground();
                    }
                }
            }
        });
    }

    // Comments
    public static List<Comment> getAllCommentsFromAPI() {

        final List<Comment> list = new LinkedList<Comment>();

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < comments.size(); i++) {
                        list.add(comments.get(i));
                    }
                }
            }
        });
        return list;
    }

    public static boolean isCommentEmpty(String idCourse) {
        boolean dataReturn = false;

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo("course", Course.createWithoutData(Course.class, idCourse));
        try {
            if (query.count() == 0)
                dataReturn = true;
        } catch (ParseException e) {
            dataReturn = false;
        }
        return dataReturn;
    }

    public static void addNewComment(String comment, String idCourse){

        Comment com = new Comment();
        com.setContent(comment);
        com.setOwner((User) ParseUser.getCurrentUser());

        Course course = new Course();
        course.setObjectId(idCourse);
        com.setCourse(course);

        com.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    CommentsTab.refreshAdapter();
                }
            }
        });

    }

    public static void deleteAllCommentsByUserId(String id){

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        User temp = ParseObject.createWithoutData(User.class, id);
        query.whereEqualTo("owner", temp);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> list, ParseException e) {
                if (e == null) {
                    for (Comment c : list) {
                        c.deleteInBackground();
                    }
                }
            }
        });
    }


    // Scores
    public static Score getScoreByCourseAndUserId(String userId, String courseId) {
        final Score scoreReturn = new Score();

        ParseQuery<Score> query = ParseQuery.getQuery(Score.class);
        User userTemp = ParseObject.createWithoutData(User.class, userId);
        query.whereEqualTo("owner", userTemp);
        Course courseTemp = ParseObject.createWithoutData(Course.class, courseId);
        query.whereEqualTo("course", courseTemp);
        query.findInBackground(new FindCallback<Score>() {
            @Override
            public void done(List<Score> list, ParseException e) {
                if (e == null){
                    if(list.size() > 0){
                        scoreReturn.setOwner(list.get(0).getOwner());
                        scoreReturn.setCourse(list.get(0).getCourse());
                        scoreReturn.setScore(list.get(0).getScore());
                        scoreReturn.setObjectId(list.get(0).getObjectId());
                        VideosTab.updateIconFav(true);
                    }else
                        VideosTab.updateIconFav(false);
                }
            }
        });
        return scoreReturn;
    }

    public static void deleteAllScoresByUserId(String id){

        ParseQuery<Score> query = ParseQuery.getQuery(Score.class);
        User temp = ParseObject.createWithoutData(User.class, id);
        query.whereEqualTo("owner", temp);
        query.findInBackground(new FindCallback<Score>() {
            @Override
            public void done(List<Score> list, ParseException e) {
                if (e == null) {
                    for (Score s : list) {
                        s.deleteInBackground();
                    }
                }
            }
        });
    }

    public static void setQuantityReviewsByCourseId(String courseId, final TextView textView){

        ParseQuery<Score> query = ParseQuery.getQuery(Score.class);
        Course courseTemp = ParseObject.createWithoutData(Course.class, courseId);
        query.whereEqualTo("course", courseTemp);
        query.findInBackground(new FindCallback<Score>() {
            @Override
            public void done(List<Score> list, ParseException e) {
                if (e == null)
                    textView.setText(list.size()+"");
                else
                    textView.setText(0);
            }
        });
    }

    public static void setStarsByCourseId(String courseId, final ImageView imageView){

        ParseQuery<Score> query = ParseQuery.getQuery(Score.class);
        Course courseTemp = ParseObject.createWithoutData(Course.class, courseId);
        query.whereEqualTo("course", courseTemp);
        query.findInBackground(new FindCallback<Score>() {
            @Override
            public void done(List<Score> list, ParseException e) {
                if (e == null){
                    float count = 0;

                    if(list.size() > 0){
                        for (Score score : list){
                            count += score.getScore();
                        }
                        count = count / list.size();
                    }
                    int imgRef = Util.getReferenceStarIconByAvegare(count);
                    imageView.setImageResource(imgRef);
                }
            }
        });
    }
}
