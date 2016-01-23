package es.alejandrolora.devhub.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.models.Course;
import es.alejandrolora.devhub.models.Video;

/**
 * Created by Alejandro on 22/4/15.
 */
public class CustomAdapterVideosTab extends ParseQueryAdapter<Video> {

    public CustomAdapterVideosTab(Context context, final String idCourse) {
        super(context, new ParseQueryAdapter.QueryFactory<Video>() {
            @Override
            public ParseQuery<Video> create() {
                ParseQuery query = ParseQuery.getQuery(Video.class);
                Course temp = new Course();
                temp.setObjectId(idCourse);
                query.whereEqualTo("course", temp);
                query.orderByAscending("lesson");
                return query;
            }
        });
    }

    @Override
    public View getItemView(Video obj, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // ViewHolder Pattern
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.custom_videos_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ParseImageView) convertView.findViewById(R.id.parseImageViewVideoIcon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textViewVideoTitle);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.textViewVideoDuration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Video's title
        viewHolder.title.setText(obj.getTitle());

        // Video's duration
        viewHolder.duration.setText(Util.getDurationFromSeconds(obj.getDuration()));

        // Video's icon
        ParseFile imageFile = null;
        if (imageFile == null) {
            Picasso.with(getContext()).load(obj.getPhoto().getUrl()).resize(400,400).centerInside().into(viewHolder.icon);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView duration;
        public ParseImageView icon;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // ProgressBar will be closed after finish the constructor query
        Util.dismissProgressBar();
    }


}


