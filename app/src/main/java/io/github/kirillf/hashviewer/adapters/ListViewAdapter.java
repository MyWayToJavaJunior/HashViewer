package io.github.kirillf.hashviewer.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.kirillf.hashviewer.R;
import io.github.kirillf.hashviewer.twitter.TwitterObject;
import io.github.kirillf.hashviewer.utils.images.ImageLoader;

public class ListViewAdapter extends ArrayAdapter<TwitterObject> {
    private Context context;
    private ImageLoader imageLoader;
    private Bitmap placeholder;
    private SimpleDateFormat dateFormatter;

    public ListViewAdapter(Context context, int resource, Collection<TwitterObject> twitterObjectList) {
        super(context, resource, (List<TwitterObject>) twitterObjectList);
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        placeholder = BitmapFactory.decodeResource(context.getResources(), R.drawable.placeholder);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.date = (TextView) itemView.findViewById(R.id.date);
            viewHolder.username = (TextView) itemView.findViewById(R.id.username);
            viewHolder.text = (TextView) itemView.findViewById(R.id.text);
            viewHolder.imageView = (ImageView) itemView.findViewById(R.id.tweet_image);
            itemView.setTag(viewHolder);
        }
        TwitterObject object = getItem(position);

        ViewHolder viewHolder = (ViewHolder) itemView.getTag();
        viewHolder.username.setText(object.getUserName());
        viewHolder.text.setText(object.getText());
        Date date = new Date(object.getDate());
        viewHolder.date.setText(dateFormatter.format(date));
        imageLoader.loadImage(context, object.getProfileImageUrl(), viewHolder.imageView, R.drawable.placeholder, placeholder);
        return itemView;
    }

    private class ViewHolder {
        TextView date;
        TextView username;
        TextView text;
        ImageView imageView;
    }
}
