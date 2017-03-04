package com.rohitdeveloper.connectinsight;

/**
 * Created by Administrator on 3/3/2017.
 */

public class Tweets {
    private String tweet_text;
    private Person tweet_person;
    private int tweet_count;

    public Tweets(){

    }

    public Tweets(String tweet_text, Person tweet_person, int tweet_count) {
        this.tweet_text = tweet_text;
        this.tweet_person = tweet_person;
        this.tweet_count = tweet_count;
    }

    public String getTweet_text() {
        return tweet_text;
    }

    public void setTweet_text(String tweet_text) {
        this.tweet_text = tweet_text;
    }

    public Person getTweet_person() {
        return tweet_person;
    }

    public void setTweet_person(Person tweet_person) {
        this.tweet_person = tweet_person;
    }

    public int getTweet_count() {
        return tweet_count;
    }

    public void setTweet_count(int tweet_count) {
        this.tweet_count = tweet_count;
    }
}
