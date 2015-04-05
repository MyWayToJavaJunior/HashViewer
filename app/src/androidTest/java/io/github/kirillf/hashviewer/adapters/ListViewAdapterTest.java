package io.github.kirillf.hashviewer.adapters;

import android.test.AndroidTestCase;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.kirillf.hashviewer.R;
import io.github.kirillf.hashviewer.twitter.TwitterObject;

public class ListViewAdapterTest extends AndroidTestCase {
    private ListViewAdapter adapter;

    private TwitterObject object1;
    private TwitterObject object2;

    public ListViewAdapterTest() {
        super();
    }

    @Override
    public void setUp() throws Exception {
        List<TwitterObject> twitterObjects = new ArrayList<>();
        object1 = new TwitterObject(0);
        object1.setUserName("Object1");
        object1.setScreenName("ScreenObject1");
        object1.setText("Object1 text");
        object1.setProfileImageUrl("Object1 image url");
        object1.setTags(new ArrayList<String>());
        object1.setDate(946684800);

        object2 = new TwitterObject(1);
        object2.setUserName("Object2");
        object2.setScreenName("ScreenObject2");
        object2.setText("Object2 text");
        object2.setProfileImageUrl("Object2 image url");
        object2.setTags(new ArrayList<String>());
        object2.setDate(1262304000);


        twitterObjects.add(object1);
        twitterObjects.add(object2);
        adapter = new ListViewAdapter(getContext(), R.layout.search_item, twitterObjects);
    }


    public void testGetItem() throws Exception {
        assertEquals("Expected object1", adapter.getItem(0).getId(), object1.getId());
    }

    public void testUsernameAdapting() throws Exception {
        View view = adapter.getView(0, null, null);

        TextView username = (TextView) view.findViewById(R.id.username);

        assertNotNull("Username textview is null", username);
        assertEquals("Expected object1", object1.getUserName(), username.getText());
    }

    public void testDateAdapting() throws Exception {
        View view = adapter.getView(0, null, null);

        TextView date = (TextView) view.findViewById(R.id.date);

        assertNotNull("Date textview is null", date);
        String formatted = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date(object1.getDate()));
        assertEquals("Expected " + formatted, formatted, date.getText());
    }

    public void testTextAdapting() throws Exception {
        View view = adapter.getView(0, null, null);

        TextView text = (TextView) view.findViewById(R.id.text);

        assertNotNull("Text textview is null", text);
        assertEquals("Expected " + object1.getText(), object1.getText(), object1.getText());
    }

    public void testImageViewAdapting() throws Exception {
        View view = adapter.getView(0, null, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.tweet_image);

        assertNotNull("Tweet imageview is null", imageView);
        assertNotNull("Imageview drawabled is null", imageView.getDrawable());
    }
}