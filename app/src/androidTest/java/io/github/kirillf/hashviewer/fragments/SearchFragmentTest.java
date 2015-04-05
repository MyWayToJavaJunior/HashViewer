package io.github.kirillf.hashviewer.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;

import io.github.kirillf.hashviewer.HashViewerActivity;
import io.github.kirillf.hashviewer.R;
import io.github.kirillf.hashviewer.events.Event;
import io.github.kirillf.hashviewer.events.EventDispatcher;
public class SearchFragmentTest extends ActivityInstrumentationTestCase2<HashViewerActivity> {
    private HashViewerActivity hashViewerActivity;
    private FragmentManager fragmentManager;

    public SearchFragmentTest() {
        super(HashViewerActivity.class);
    }

    public SearchFragmentTest(Class<HashViewerActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        hashViewerActivity = getActivity();
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        fragmentManager = hashViewerActivity.getSupportFragmentManager();
        Event event = new Event(Event.EventType.AUTHORIZE, "AUTHORIZE");
        eventDispatcher.notify(event);
        getInstrumentation().waitForIdleSync();
    }

    public void testPreConditions() throws Exception {
        assertEquals(fragmentManager.getBackStackEntryCount(), 0);
        Fragment fragment = fragmentManager.getFragments().get(1);
        assertEquals(SearchFragment.class, fragment.getClass());
    }

    public void testSearchViewLayoutPresent() throws Exception {
        FrameLayout searchViewLayout = (FrameLayout) hashViewerActivity.findViewById(R.id.searchView_layout);
        assertNotNull(searchViewLayout);
    }

    public void testSearchViewPresent() throws Exception {
        SearchView searchView = (SearchView) hashViewerActivity.findViewById(R.id.search_view);

        assertNotNull(searchView);
    }

    public void testSearchViewLayoutLayoutParams() throws Exception {
        FrameLayout frameLayout = (FrameLayout) hashViewerActivity.findViewById(R.id.searchView_layout);
        ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();

        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testSearchViewLayoutParams() throws Exception {
        SearchView searchView = (SearchView) hashViewerActivity.findViewById(R.id.search_view);
        ViewGroup.LayoutParams layoutParams = searchView.getLayoutParams();

        assertNotNull(layoutParams);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void testSearchViewHintText() throws Exception {
        SearchView searchView = (SearchView) hashViewerActivity.findViewById(R.id.search_view);
        CharSequence hint = searchView.getQueryHint();

        assertNotNull(hint);
        assertEquals(hashViewerActivity.getString(R.string.search_hint), hint);
    }

    public void testSearchViewIsNotIconifiedByDefault() throws Exception {
        SearchView searchView = (SearchView) hashViewerActivity.findViewById(R.id.search_view);

        assertTrue(!searchView.isIconfiedByDefault());
    }

    public void testListViewPresent() throws Exception {
        ListView listView = (ListView) hashViewerActivity.findViewById(R.id.search_results);

        assertNotNull(listView);
    }

    public void testListViewIsVisible() throws Exception {
        ListView listView = (ListView) hashViewerActivity.findViewById(R.id.search_results);

        assertEquals(View.GONE, listView.getVisibility());
    }

    public void testListViewEmptyViewPresent() throws Exception {
        ListView listView = (ListView) hashViewerActivity.findViewById(R.id.search_results);

        View emptyView = listView.getEmptyView();
        assertNotNull(emptyView);
    }
}