package es.alejandrolora.devhub.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.CircleTransform;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.models.Comment;
import es.alejandrolora.devhub.models.Course;
import es.alejandrolora.devhub.models.User;

/**
 * Created by Alejandro on 22/4/15.
 */
public class CustomAdapterCommentsTab extends ParseQueryAdapter<Comment> {

    public CustomAdapterCommentsTab(Context context,final String idCourse) {
        super(context, new QueryFactory<Comment>() {
            @Override
            public ParseQuery<Comment> create() {
                ParseQuery query = ParseQuery.getQuery(Comment.class);
                Course temp = new Course();
                temp.setObjectId(idCourse);
                query.whereEqualTo("course", temp);
                query.include("owner");
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(Comment obj, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // ViewHolder Pattern
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.custom_comments_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.owner = (TextView) convertView.findViewById(R.id.textViewOwnerComment);
            viewHolder.content = (TextView) convertView.findViewById(R.id.textViewCommentContent);
            viewHolder.date = (TextView) convertView.findViewById(R.id.textViewCommentDate);
            viewHolder.icon = (ParseImageView) convertView.findViewById(R.id.parseImageViewCommentIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Comment's owner
        if(obj.getOwner().getUserName() == null || obj.getOwner().getUserName().equals(""))
            viewHolder.owner.setText(getContext().getString(R.string.by) + " " + obj.getOwner().getEmail());
        else
            viewHolder.owner.setText(getContext().getString(R.string.by) + " " + obj.getOwner().getUserName());


        // Comment's content
        viewHolder.content.setText(obj.getContent());

        // Comment's date
        String formattedDate = Util.getDateFormatted(obj.getCreatedAt(), getContext().getString(R.string.pattern_date_comments));
        formattedDate = new StringBuilder(formattedDate).replace(formattedDate.lastIndexOf(" "), formattedDate.lastIndexOf(" ") + 1," '").toString();
        viewHolder.date.setText(formattedDate);

        // Comment's icon
        if (obj.getOwner().getPhoto() != null){
            Picasso.with(getContext()).load(obj.getOwner().getPhoto().getUrl()).placeholder(R.mipmap.ic_unknown).
                    transform(new CircleTransform()).resize(200,200).centerInside().into(viewHolder.icon);
        }else{
            Picasso.with(getContext()).load(R.mipmap.ic_unknown).resize(300, 300).centerInside().into(viewHolder.icon);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView owner;
        public TextView content;
        public TextView date;
        public ParseImageView icon;
    }
}


