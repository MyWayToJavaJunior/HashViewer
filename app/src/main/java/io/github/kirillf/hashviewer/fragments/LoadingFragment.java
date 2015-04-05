package io.github.kirillf.hashviewer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.R;
import io.github.kirillf.hashviewer.events.Event;
import io.github.kirillf.hashviewer.events.EventDispatcher;
import io.github.kirillf.hashviewer.twitter.TwitterController;

public class LoadingFragment extends Fragment implements Handler.Callback {
    private EventDispatcher eventDispatcher;
    private OnFragmentInteractionListener mListener;
    private Handler handler;


    public LoadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        handler = new Handler(this);
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.setHandler(handler);
        TwitterController.getInstance(getActivity()).authenticate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        eventDispatcher.removeHandler(handler);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.WHAT:
                Event event = (Event) msg.obj;
                switch (event.getType()) {
                    case NO_CONNECTION:
                        Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                    case ERROR:
                        Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_LONG).show();
                    case AUTHORIZE:
                        mListener.onFragmentInteraction(SearchFragment.newInstance(), false);
                        break;

                }
        }
        return false;
    }
}
