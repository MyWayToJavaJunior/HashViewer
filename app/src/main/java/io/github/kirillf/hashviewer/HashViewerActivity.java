package io.github.kirillf.hashviewer;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import io.github.kirillf.hashviewer.fragments.LoadingFragment;
import io.github.kirillf.hashviewer.fragments.OnFragmentInteractionListener;


public class HashViewerActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_viewer);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoadingFragment())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Fragment fragment, boolean isAddToBackStack) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if (isAddToBackStack) {
            tx.add(R.id.container, fragment).addToBackStack(null);
        } else {
            tx.replace(R.id.container, fragment);
        }
        tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }
}
