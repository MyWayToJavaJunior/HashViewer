package io.github.kirillf.hashviewer.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import io.github.kirillf.hashviewer.HashViewerActivity;
import io.github.kirillf.hashviewer.R;

public class LoadingFragmentTest extends ActivityInstrumentationTestCase2<HashViewerActivity> {
    private HashViewerActivity hashViewerActivity;
    private FragmentManager supportFragmentManager;


    public LoadingFragmentTest() {
        super(HashViewerActivity.class);
    }

    public LoadingFragmentTest(Class activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        hashViewerActivity = getActivity();
        supportFragmentManager = hashViewerActivity.getSupportFragmentManager();
    }

    public void testPreConditions() {
        assertTrue(supportFragmentManager.getFragments().size() == 1);
        Fragment fragment = supportFragmentManager.getFragments().get(0);
        assertTrue(fragment.getClass().equals(LoadingFragment.class));
    }

    public void testLoadingProgressBarVisible() throws Exception {
        ProgressBar progressBar = (ProgressBar) hashViewerActivity.findViewById(R.id.loading_progress_bar);
        assertTrue(View.VISIBLE == progressBar.getVisibility());
    }

    public void testLoadingProgressLayoutParams() throws Exception {
        ProgressBar progressBar = (ProgressBar) hashViewerActivity.findViewById(R.id.loading_progress_bar);
        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}