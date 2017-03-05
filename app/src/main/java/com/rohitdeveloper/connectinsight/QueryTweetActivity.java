package com.rohitdeveloper.connectinsight;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.List;

public class QueryTweetActivity extends AppCompatActivity {

    private static final String TAG = "QueryTweetActivity";

    //User search setup
    private String searchQuery=null;
    private boolean flagLoading;
    private boolean endOfSearchResults;
    private TweetViewAdapter adapter;
    private static final String SEARCH_RESULT_TYPE = "mixed";
    private static final int SEARCH_COUNT =50;
    private long maxId;
    ListView SearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_tweet);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);


        searchQuery=getIntent().getStringExtra("Query");
        setProgressBarIndeterminateVisibility(true);
        handleIntent(getIntent());
        adapter = new TweetViewAdapter(QueryTweetActivity.this);
        SearchList = (ListView) findViewById(R.id.search_list);
        SearchList.setAdapter(adapter);
        SearchList.setEmptyView(findViewById(R.id.loading));
        final SearchService service = Twitter.getApiClient().getSearchService();

        service.tweets(searchQuery, null, null, null, SEARCH_RESULT_TYPE, SEARCH_COUNT, null, null,
                maxId, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {
                        setProgressBarIndeterminateVisibility(false);
                        final List<Tweet> tweets = searchResult.data.tweets;
                        adapter.getTweets().addAll(tweets);
                        adapter.notifyDataSetChanged();
                        if (tweets.size() > 0) {
                            maxId = tweets.get(tweets.size() - 1).id - 1;
                        } else {
                            endOfSearchResults = true;
                        }
                        flagLoading = false;
                    }

                    @Override
                    public void failure(TwitterException error) {

                        setProgressBarIndeterminateVisibility(false);
                        Toast.makeText(QueryTweetActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        flagLoading = false;
                    }
                }
        );
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_twitter_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Enter search");

        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, searchQuery);
        }
    }
}
