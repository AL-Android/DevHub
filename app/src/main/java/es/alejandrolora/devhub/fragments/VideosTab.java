package es.alejandrolora.devhub.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.List;
import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.activitys.YouTubeActivity;
import es.alejandrolora.devhub.adapters.CustomAdapterVideosTab;
import es.alejandrolora.devhub.models.Course;
import es.alejandrolora.devhub.models.Score;
import es.alejandrolora.devhub.models.User;
import es.alejandrolora.devhub.models.Video;

public class VideosTab extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener,
        RatingBar.OnRatingBarChangeListener {

    private CustomAdapterVideosTab adapter;
    private ListView listView;

    private String idCourse;
    private String color;
    private Course course;
    private Score score;
    private String currentUserId;
    private float scoreByUser;

    private List<Video> listVideos;
    private static FloatingActionButton fabFav;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_tab1, container, false);

        idCourse = this.getArguments().getString("idCourse");
        color = this.getArguments().getString("color");
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        Util.showProgressBar(getActivity(), color);

        listView = (ListView) v.findViewById(R.id.listViewVideos);

        adapter = new CustomAdapterVideosTab(getActivity(), idCourse);

        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        listVideos = API.getAllVideosFromCourse(idCourse);
        course = API.getCourseById(idCourse);
        score = API.getScoreByCourseAndUserId(currentUserId, idCourse);

        fabFav = (FloatingActionButton) v.findViewById(R.id.fab);
        fabFav.setColorNormal(Color.parseColor(color));
        fabFav.setColorPressed(Util.DarkerColor(color));
        fabFav.setColorRipple(Util.LighterColor(color));
        fabFav.setShadow(true);
        fabFav.attachToListView(listView);
        fabFav.setOnClickListener(this);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i = new Intent(getActivity(), YouTubeActivity.class);
        i.putExtra("code", listVideos.get(position).getCode());
        startActivity(i);
    }

    @Override
    public void onClick(View v) {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(course.getTitle())
                .titleColor(Color.parseColor(color))
                .customView(R.layout.rating_bar_layout, true)
                .backgroundColor(Color.WHITE)
                .positiveText(R.string.rate)
                .positiveColorRes(R.color.colorWhite)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .negativeText(R.string.delete)
                .negativeColorRes(R.color.colorButtonCancel)
                .neutralText(R.string.cancel)
                .neutralColorRes(R.color.colorButtonCancelLight)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        updateIconFav(true);
                        confirmChangesRatingBar();
                        score.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    new SnackBar(getActivity(), getActivity().getString(R.string.rate_send_successfully), null, null).show();
                                else
                                    new SnackBar(getActivity(), getActivity().getString(R.string.rate_unexpected_error), null, null).show();
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        updateIconFav(false);
                        score.setScore(0);
                        score.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    new SnackBar(getActivity(), getActivity().getString(R.string.rate_delete_successfully), null, null).show();
                                else
                                    new SnackBar(getActivity(), getActivity().getString(R.string.rate_unexpected_error), null, null).show();
                            }
                        });
                    }
                }).build();

        RatingBar rating = (RatingBar) dialog.getCustomView().findViewById(R.id.ratingBarCourse);
        rating.setRating(score.getScore());
        rating.setOnRatingBarChangeListener(this);
        dialog.show();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        scoreByUser = rating;
    }

    private void confirmChangesRatingBar() {
        if (score.getOwner() == null || score.getCourse() == null) {
            score.setOwner(ParseObject.createWithoutData(User.class, currentUserId));
            score.setCourse(ParseObject.createWithoutData(Course.class, idCourse));
        }
        score.setScore(scoreByUser);
    }

    public static void updateIconFav(boolean fav){
        if(fav)
            fabFav.setImageResource(R.mipmap.ic_fab_fav_fill);
        else
            fabFav.setImageResource(R.mipmap.ic_fab_fav_empty);
    }
}
