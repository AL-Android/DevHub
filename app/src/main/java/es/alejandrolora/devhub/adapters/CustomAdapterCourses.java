package es.alejandrolora.devhub.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.models.Category;
import es.alejandrolora.devhub.models.Course;

/**
 * Created by Alejandro on 11/4/15.
 */
public class CustomAdapterCourses extends ParseQueryAdapter<Course> {


    public CustomAdapterCourses(Context context, final String idCategory) {
        super(context, new QueryFactory<Course>() {
            @Override
            public ParseQuery<Course> create() {
                ParseQuery innerQuery = ParseQuery.getQuery(Category.class);
                innerQuery.whereEqualTo("objectId", idCategory);

                ParseQuery<Course> query = ParseQuery.getQuery(Course.class);
                query.whereMatchesQuery("category", innerQuery);
                query.whereEqualTo("isValid", true);
                return query;
            }
        });
    }

    @Override
    public View getItemView(Course course, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        // ViewHolder Pattern
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.custom_list_view_courses, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ParseImageView) convertView.findViewById(R.id.parseImageViewIconCourse);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textViewTitleCourse);
            viewHolder.owner = (TextView) convertView.findViewById(R.id.textViewOwnerCourse);
            viewHolder.reviews = (TextView) convertView.findViewById(R.id.textViewQuantityCourse);
            viewHolder.stars = (ImageView) convertView.findViewById(R.id.imageViewStarsCourse);

            API.getDetailsForCourse(getContext(), course.getOwner().getId(), course.getId(), viewHolder.owner);
            convertView.setTag(viewHolder);

            // Course's photo
            ParseFile imageFile = course.getPhoto();
            if (imageFile != null)
                Picasso.with(getContext()).load(imageFile.getUrl()).resize(400, 400).centerInside().into(viewHolder.icon);
            // Course's title
            viewHolder.title.setText(course.getTitle());
            // Course's reviews
            API.setQuantityReviewsByCourseId(course.getObjectId(), viewHolder.reviews);
            // Course's stars
            API.setStarsByCourseId(course.getObjectId(), viewHolder.stars);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    class ViewHolder {
        public ParseImageView icon;
        public TextView title;
        public TextView owner;
        public TextView reviews;
        public ImageView stars;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // ProgressBar will be closed after finish the constructor query
        Util.dismissProgressBar();
    }
}