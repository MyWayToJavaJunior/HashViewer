package io.github.kirillf.hashviewer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.github.kirillf.hashviewer.R;
import io.github.kirillf.hashviewer.adapters.ListViewAdapter;
import io.github.kirillf.hashviewer.events.Event;
import io.github.kirillf.hashviewer.events.EventDispatcher;
import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.twitter.TwitterController;
import io.github.kirillf.hashviewer.twitter.TwitterDataProvider;
import io.github.kirillf.hashviewer.twitter.TwitterDataSource;
import io.github.kirillf.hashviewer.twitter.TwitterObject;

public class SearchFragment extends Fragment implements Handler.Callback {
    private static final int SEARCH = 100;
    private static final int MORE = 101;
    private static final int LIVE_SEARCH = 102;
    private EventDispatcher eventDispatcher;
    private TwitterController twitterController;
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private ListViewAdapter adapter;
    private TwitterDataProvider<TwitterObject> dataProvider;
    private RequestState requestState = RequestState.READY;
    private Handler handler;
    private SearchView searchView;
    private ProgressBar headerProgress;
    private TextView emptyText;
    private ProgressBar footerProgress;
    private ProgressBar temp;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        eventDispatcher = EventDispatcher.getInstance();
        twitterController = TwitterController.getInstance(getActivity());
        twitterController.reset();
        dataProvider = TwitterDataSource.getInstance(eventDispatcher);
        handler = new Handler(this);
        configureSearchView(view);
        configureListView(view);
        temp = createProgressBar();
        return view;
    }

    private void configureListView(View view) {
        listView = (ListView) view.findViewById(R.id.search_results);
        adapter = new ListViewAdapter(getActivity(), R.layout.search_item, dataProvider.getSearchResults());
        listView.setAdapter(adapter);
        emptyText = (TextView) view.findViewById(R.id.empty_list);
        listView.setEmptyView(emptyText);
        View progressView = View.inflate(getActivity(), R.layout.progress_view, null);
        footerProgress = (ProgressBar) progressView.findViewById(R.id.more_tweets_progress);
        footerProgress.setVisibility(View.GONE);
        View headerView = View.inflate(getActivity(), R.layout.header_progress, null);
        headerProgress = (ProgressBar) headerView.findViewById(R.id.header_progress_bar);
        headerProgress.setVisibility(View.GONE);
        listView.addHeaderView(headerView);
        listView.addFooterView(progressView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TwitterObject twitterObject = (TwitterObject) listView.getItemAtPosition(position);
                String url = Constants.TWITTER_URL + twitterObject.getScreenName() + "/statuses/" + twitterObject.getId();
                mListener.onFragmentInteraction(WebViewFragment.newInstance(url), true);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (visibleItemCount != totalItemCount && lastVisibleItem == totalItemCount &&
                        requestState == RequestState.READY) {
                    Message message = handler.obtainMessage(MORE);
                    handler.sendMessage(message);
                }
            }
        });
    }

    private ProgressBar createProgressBar() {
        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        return progressBar;
    }

    private void configureSearchView(View view) {
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Message message = handler.obtainMessage(SEARCH, s);
                handler.sendMessage(message);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 1) {
                    Message message = handler.obtainMessage(LIVE_SEARCH, s);
                    handler.sendMessage(message);
                    return true;
                }
                return false;
            }
        });
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterController.reset();
                searchView.setQuery("", false);
                listView.setEmptyView(emptyText);
            }
        });
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
        eventDispatcher.setHandler(handler);
    }

    @Override
    public void onPause() {
        super.onPause();
        eventDispatcher.removeHandler(handler);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.WHAT:
                headerProgress.setVisibility(View.GONE);
                Event event = (Event) msg.obj;
                switch (event.getType()) {
                    case NO_CONNECTION:
                        Toast.makeText(getActivity(), getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                        break;
                    case DATA_RECEIVED:
                        adapter.notifyDataSetChanged();
                        emptyText.setVisibility(View.GONE);
                        footerProgress.setVisibility(View.VISIBLE);
                        requestState = RequestState.READY;
                        break;
                    case ERROR:
                        Toast.makeText(getActivity(), "Request Failed: " + event.getMessage(), Toast.LENGTH_LONG).show();
                        requestState = RequestState.ERROR;
                        break;
                    case END_OF_DATA:
                        requestState = RequestState.FINISHED;
                        listView.setEmptyView(emptyText);
                        footerProgress.setVisibility(View.GONE);
                        break;
                    case RESET:
                        requestState = RequestState.READY;
                        adapter.clear();
                        break;
                }
                break;
            case SEARCH:
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            case LIVE_SEARCH:
                String queue = (String) msg.obj;
                performSearchRequest(queue);
                break;
            case MORE:
                performMoreTweetsRequest();
                break;
        }
        return false;
    }

    private void performSearchRequest(String queue) {
        if (requestState == RequestState.READY || requestState == RequestState.FINISHED) {
            listView.setEmptyView(temp);
            headerProgress.setVisibility(View.VISIBLE);
            twitterController.reset();
            twitterController.searchTweets(queue);
            requestState = RequestState.BUSY;
        }
    }

    private void performMoreTweetsRequest() {
        twitterController.loadMore();
        requestState = RequestState.BUSY;
    }

    enum RequestState {
        READY,
        BUSY,
        FINISHED,
        ERROR
    }

}
