package com.rohitdeveloper.connectinsight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.text.Html;
import android.text.Spanned;
import android.util.FloatProperty;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = "ConnectionActivity";

    private SharedPreferences sharedPref;

    private ProgressBar progressbar;
    private TextView progress_description;

    private Person accountPerson;
    private Tweets accountPersonTimelineTweets;

    private ArrayList<Person> nearbyPerson;

    private ArrayList<Tweets> userTimelineTweets;
    private double latitude,longitude;





    //Twitter GeoTweet Search API credentials
    private String SEARCH_QUERY =null;
    private String SEARCH_RESULT_TYPE =null;
    private int SEARCH_COUNT = 15;
    private Integer SEARCH_DISTANCE=800;
    private Geocode SEARCH_GEO_CODE = null;
    private int totalUserCount;


    //Twitter User Search API credentials
    private long USER_ID ;
    private String USER_SCREEN_NAME =null;
    private int USER_TWEET_COUNT = 8;     //total recent tweets of user
    private int user_count_with_downloaded_tweets=0;

    //Microsoft Cognitive Service RestAPI credentials
    private final static int MICROSOFT_SIMILARITY_API=1;
    private final static int MICROSOFT_SENTIMENT_ANALYSIS_API=2;
    private static ArrayList<Person> bestSimilarPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        sharedPref= getApplicationContext().getSharedPreferences("ConnectInsight", Context.MODE_PRIVATE);
        SEARCH_DISTANCE=Integer.parseInt(sharedPref.getString("GeoDistance", "800"));
        Log.d(TAG,SEARCH_DISTANCE.toString());

        progressbar=(ProgressBar) findViewById(R.id.id_progress_bar);
        progressbar.getIndeterminateDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        progress_description=(TextView) findViewById(R.id.id_progress_description);
        String description = "Please wait,We're learning about you to better connect with "+"<font color='#003566'>similar people</font>";
        progress_description.setText(fromHtml(description));

        latitude = getIntent().getDoubleExtra("Latitude",0);
        longitude =getIntent().getDoubleExtra("Longitude",0);
        SEARCH_QUERY=getIntent().getStringExtra("Query");
        SEARCH_GEO_CODE=new Geocode(latitude,longitude,SEARCH_DISTANCE, Geocode.Distance.KILOMETERS);//Radius of search
        SEARCH_RESULT_TYPE="mixed";

        nearbyPerson=new ArrayList<Person>();
        userTimelineTweets=new ArrayList<Tweets>();
        bestSimilarPerson=new ArrayList<Person>();


        Twitter.getApiClient().getAccountService().verifyCredentials(true, false,new Callback<User>() {
            @Override
            public void failure(TwitterException e) {
                Log.d("TAG", e.getMessage());
            }
            @Override
            public void success(Result<User> userResult) {
                User user = userResult.data;
                accountPerson=new Person(user.idStr,user.name,user.screenName,user.location,user.description,user.profileImageUrl, (float) 0);
                Log.d(TAG, user.name);
                Log.d(TAG, user.profileImageUrl);
                getAccountPersonTweets(); //Get account user tweets
                getNearByUserData();  //find nearby user first

            }
        });

    }



    private void  getNearByUserData(){

        final SearchService service = Twitter.getApiClient().getSearchService();
        service.tweets(SEARCH_QUERY,null, null, null, SEARCH_RESULT_TYPE, SEARCH_COUNT, null, null,
                null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {
                        final List<Tweet> tweets = searchResult.data.tweets;
                        Set<String> hs = new HashSet<String>();

                        for(int i=0;i<tweets.size();i++){
                            if(!(hs.contains(tweets.get(i).user.idStr ))) {
                                Person currentPerson = new Person(tweets.get(i).user.idStr, tweets.get(i).user.name, tweets.get(i)
                                        .user.screenName, tweets.get(i).user.location, tweets.get(i)
                                        .user.description, tweets.get(i).user.profileImageUrl, (float) 0);

                                nearbyPerson.add(currentPerson);
                            }
                            hs.add(tweets.get(i).user.idStr);
                        }
                        totalUserCount=nearbyPerson.size();
                        Log.d(TAG,"Tweets Results: "+nearbyPerson.size());
                        getNearByUserTweets();
                    }

                    @Override
                    public void failure(TwitterException  exception) {
                        Toast.makeText(ConnectionActivity.this,
                                exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }


    //Getting tweet data for each User
    private void  getNearByUserTweets(){

        for(final Person person:nearbyPerson) {

            USER_ID= Long.parseLong(person.getPerson_id());
            USER_SCREEN_NAME=person.getPerson_screen_name();
            Log.d(TAG,USER_SCREEN_NAME);

            Twitter.getApiClient().getStatusesService()
                    .userTimeline(USER_ID, USER_SCREEN_NAME, USER_TWEET_COUNT, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> result) {
                            user_count_with_downloaded_tweets=user_count_with_downloaded_tweets+1;
                            StringBuilder overallTweets = new StringBuilder();
                            int count=0;
                            for (Tweet tweet : result.data) {
                                overallTweets.append(filterTweet(tweet.text));
                                count=count+1;
                            }
                            if(person.getPerson_description()!=null && !(person.getPerson_description().isEmpty())){
                                overallTweets.append(filterTweet(person.getPerson_description()));
                            }
                            Log.d(TAG,"Individual Tweets Count: "+count);
                            //Log.d(TAG,overallTweets.toString());
                            Tweets currentUserTweets=new Tweets(overallTweets.toString(),person,count);
                            userTimelineTweets.add(currentUserTweets);
                            checkUserCountWithDownloadedTweets(user_count_with_downloaded_tweets);
                        }
                        @Override
                        public void failure(TwitterException exception) {
                            Toast.makeText(ConnectionActivity.this,
                                    exception.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }



    private void getAccountPersonTweets(){

        Long account_person_id=Long.parseLong(accountPerson.getPerson_id());
        String account_person_screen_name=accountPerson.getPerson_screen_name();

        Twitter.getApiClient().getStatusesService()
                .userTimeline(account_person_id,account_person_screen_name,USER_TWEET_COUNT, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        StringBuilder overallTweets = new StringBuilder();
                        int count=0;
                        for (Tweet tweet : result.data) {

                            overallTweets.append(filterTweet(tweet.text));
                            count=count+1;
                        }

                        if(accountPerson.getPerson_description()!=null && !(accountPerson.getPerson_description().isEmpty())){
                            overallTweets.append(filterTweet(accountPerson.getPerson_description()));
                        }

                        accountPersonTimelineTweets=new Tweets(overallTweets.toString(),accountPerson,count);
                        Log.d(TAG,"Account Person Tweets available");
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(ConnectionActivity.this,
                                exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String filterTweet(String commentstr)
    {
        commentstr=commentstr.replace("\n", "").replace("\r", "");
        commentstr=commentstr.replaceAll("/[^a-zA-Z]+/","");
        commentstr=commentstr.replaceAll("https?://\\S+\\s?","");
        commentstr=commentstr.replaceAll("[|?*<\":>+\\[\\]/']","");
        commentstr=commentstr.replaceAll("[\\\\W+]","");
        commentstr=commentstr.replaceAll("\\s{2,}", " ").trim();
        String resultStr="";
        for(int index=0;index<commentstr.length();index++){
            char c=commentstr.charAt(index);
            if(c==' ' || Character.isLetter(c) || c=='.' || c=='@')
                resultStr=resultStr+ c;
        }
        resultStr=resultStr.replaceAll("\\s{2,}", " ").trim();
        try {
            resultStr = URLDecoder.decode(URLEncoder.encode(resultStr, "iso8859-1"),"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return resultStr;
    }

    //To check how-many user-tweets have been downloaded
    private void checkUserCountWithDownloadedTweets(int count){
        if(count==totalUserCount){
            microsoftSimilarityAPICall();
        }
    }

    private void microsoftSimilarityAPICall(){

           new microsoftRestAPI().execute(userTimelineTweets);
    }


    //Using Microsoft Rest API (Knowledge API)

    class microsoftRestAPI extends AsyncTask<ArrayList<Tweets>, Void,  ArrayList<NetworkModel>> {

        ArrayList<NetworkModel> similarity_model=new ArrayList<NetworkModel>();
        //ArrayList<String> similarity_model=new ArrayList<String>();
        @Override
        protected  ArrayList<NetworkModel> doInBackground(ArrayList<Tweets>... params) {

            try {
                ArrayList<Tweets> timeLineTweets=params[0];

                for(Tweets tweets:timeLineTweets) {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority("westus.api.cognitive.microsoft.com")
                            .appendPath("academic")
                            .appendPath("v1.0")
                            .appendPath("similarity")
                            .appendQueryParameter("s1",accountPersonTimelineTweets.getTweet_text())
                            .appendQueryParameter("s2", tweets.getTweet_text());
                    String url= builder.build().toString();
                    String subscriptionKey = "c0f995f112e848ba801d7f307ba691c3";
                    HttpClient client = new DefaultHttpClient();
                    //MAKE A GET REQUEST
                    HttpGet request = new HttpGet(url);
                    request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
                    HttpResponse response = client.execute(request);
                    HttpEntity entity = response.getEntity();
                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.d(TAG, "STATUS " + String.valueOf(statusCode));

                    if (entity != null) {
                        NetworkModel networkModel=new NetworkModel(Float.parseFloat(EntityUtils.toString(entity)),tweets.getTweet_person(),statusCode);
                        //similarity_model.add(EntityUtils.toString(entity));
                        similarity_model.add(networkModel);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }

            return  similarity_model;
        }


        protected void onPostExecute(ArrayList<NetworkModel> similarity_network_model) {
            Map<Person,Float> hashmap=new LinkedHashMap<Person,Float>();
            for(NetworkModel networkModel: similarity_network_model){
                Log.d(TAG,networkModel.getStatus_code().toString());
                if(networkModel.getStatus_code()==200) {
                    hashmap.put(networkModel.getPerson_profile(),networkModel.getSimilarity_score());
                }
            }
            getBestSimilarPersonList(hashmap);
        }
    }


    //To check how-many user-tweets have been downloaded
    private void getBestSimilarPersonList(Map<Person,Float> hashmap){

            Log.d(TAG, String.valueOf(hashmap.size()));
            hashmap = MapUtil.sortByValue(hashmap);
            ArrayList<Person> finalSortedPerson=new ArrayList<Person>(hashmap.keySet());
            Collections.reverse(finalSortedPerson);
            Log.d(TAG, String.valueOf(finalSortedPerson.size())+" "+hashmap.keySet().size());
            for(int i=0;i<finalSortedPerson.size();i++){
                Person current=finalSortedPerson.get(i);
                current.setPerson_similarity_score(hashmap.get(finalSortedPerson.get(i)));
                Log.d(TAG,current.getPerson_screen_name()+" "+current.getPerson_similarity_score());
                bestSimilarPerson.add(current);
            }

            Intent mainActivityIntent = new Intent(ConnectionActivity.this,MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainActivityIntent.putExtra("BestSimilarPerson",(ArrayList<Person>) bestSimilarPerson);
            mainActivityIntent.putExtra("AccountPerson",(Person)accountPerson);
            mainActivityIntent.putExtra("Query",SEARCH_QUERY);
            startActivity(mainActivityIntent);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }


}
