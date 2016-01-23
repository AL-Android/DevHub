package es.alejandrolora.devhub.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.adapters.CustomAdapterCommentsTab;

public class CommentsTab extends Fragment implements View.OnClickListener {

    private static View viewRoot;
    private static CustomAdapterCommentsTab adapter;
    private static ListView listViewComments;
    private static String idCourse;
    public static Activity mContextComments;

    private String color;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot = inflater.inflate(R.layout.activity_tab2, container, false);
        mContextComments = getActivity();

        idCourse = this.getArguments().getString("idCourse");
        color = this.getArguments().getString("color");

        listViewComments = (ListView) viewRoot.findViewById(R.id.listViewComments);
        adapter = new CustomAdapterCommentsTab(getActivity(), idCourse);
        listViewComments.setAdapter(adapter);

        showHideIfCommentIsEmpty();

        FloatingActionButton fab = (FloatingActionButton) viewRoot.findViewById(R.id.fab);
        fab.setColorNormal(Color.parseColor(color));
        fab.setColorPressed(Util.DarkerColor(color));
        fab.setColorRipple(Util.LighterColor(color));
        fab.setShadow(true);
        fab.attachToListView(listViewComments);
        fab.setOnClickListener(this);

        return viewRoot;
    }


    @Override
    public void onClick(View v) {

        final String courseId = idCourse;

        new MaterialDialog.Builder(getActivity())
                .title(R.string.new_comment)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .backgroundColor(Color.WHITE)
                .titleColor(Color.parseColor(color))
                .contentColorRes(R.color.colorPrimaryDialogText)
                .positiveColor(Color.parseColor(color))
                .negativeColorRes(R.color.colorButtonCancelLight)
                .widgetColorRes(android.R.color.transparent)
                .inputMaxLength(127)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String comment = dialog.getInputEditText().getText().toString();
                        if (comment.length() > 0)
                            API.addNewComment(comment, courseId);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .input(R.string.new_comment_hint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence newComment) {}
                }).show();

    }

    public static void refreshAdapter(){
        adapter = new CustomAdapterCommentsTab(mContextComments, idCourse);
        listViewComments.setAdapter(adapter);
        showHideIfCommentIsEmpty();
    }

    private static void showHideIfCommentIsEmpty(){
        TextView text = (TextView)viewRoot.findViewById(R.id.textViewNoComments);
        if (API.isCommentEmpty(idCourse))
            text.setVisibility(View.VISIBLE);
        else
            text.setVisibility(View.INVISIBLE);
    }
}