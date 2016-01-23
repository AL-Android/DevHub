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
import es.alejandrolora.devhub.Api.API;
import es.alejandrolora.devhub.R;
import es.alejandrolora.devhub.Util.Util;
import es.alejandrolora.devhub.models.Category;

/**
 * Created by Alejandro on 11/4/15.
 */
public class CustomAdapterCategories extends ParseQueryAdapter<Category> {


    public CustomAdapterCategories(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Category>() {
            @Override
            public ParseQuery<Category> create() {
                ParseQuery query = ParseQuery.getQuery(Category.class);
                return query;
            }
        });
    }

    @Override
    public View getItemView(Category obj, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        // ViewHolder Pattern
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.custom_list_view_categories, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ParseImageView) convertView.findViewById(R.id.parseImageViewIconCategory);
            viewHolder.title = (TextView) convertView.findViewById(R.id.textViewTitleCategory);
            viewHolder.details = (TextView) convertView.findViewById(R.id.textViewDetailsCategory);
            convertView.setTag(viewHolder);
            // Calculating courses' number and videos' number (just one time)
            API.numberCoursesByCategory(getContext(), obj.getId(), viewHolder.details);
            API.numberVideosByCategory(getContext(), obj.getId(), viewHolder.details);
            // Category's title
            viewHolder.title.setText(obj.getName());
            // Category's icon
            ParseFile imageFile = obj.getIcon();
            if (imageFile != null)
                Picasso.with(getContext()).load(imageFile.getUrl()).into(viewHolder.icon);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView details;
        public ParseImageView icon;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // ProgressBar will be closed after finish the constructor query
        Util.dismissProgressBar();
    }
}


