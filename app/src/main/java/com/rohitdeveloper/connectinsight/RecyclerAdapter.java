package com.rohitdeveloper.connectinsight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

import az.plainpie.PieView;

/**
 * Created by Administrator on 2/1/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private ArrayList<Person> bestSimilarPerson;
    private String imageUrl;
    Context context;

    public RecyclerAdapter(ArrayList<Person> bestSimilarPerson ,String imageUrl,Context context) {
        this.bestSimilarPerson=bestSimilarPerson;
        this.imageUrl=imageUrl;
        this.context=context;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView itemProfileImage;
        public TextView itemUserName;
        public TextView itemUserScreeName;
        public TextView itemUserLocation;
        public PieView itemPieView;
        public Button itemTweetButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemProfileImage = (ImageView)itemView.findViewById(R.id.id_list_item_profileImage);
            itemUserName = (TextView)itemView.findViewById(R.id.id_list_item_user_name);
            itemUserScreeName = (TextView)itemView.findViewById(R.id.id_list_item_user_screen_name);
            itemUserLocation=(TextView)itemView.findViewById(R.id.id_list_item_user_location);
            itemPieView=(PieView)  itemView.findViewById(R.id.id_pieView);
            itemTweetButton=(Button)itemView.findViewById(R.id.id_tweet_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_custom, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int index) {
        Picasso.with(context).load(bestSimilarPerson.get(index).getPerson_profile_image_url()).placeholder(R.drawable.nav_profile_avatar).fit().into(viewHolder.itemProfileImage);
        viewHolder.itemUserName.setText(bestSimilarPerson.get(index).getPerson_name());
        viewHolder.itemUserScreeName.setText("@"+bestSimilarPerson.get(index).getPerson_screen_name());
        viewHolder.itemUserLocation.setText(bestSimilarPerson.get(index).getPerson_location());
        Float similarity_score=bestSimilarPerson.get(index).getPerson_similarity_score();
        DecimalFormat value = new DecimalFormat("##.00");
        String score=String.valueOf(value.format(similarity_score));
        viewHolder.itemPieView.setInnerText(score);


        if(similarity_score>=0) {
            viewHolder.itemPieView.setPercentageBackgroundColor(R.color.colorGreen);
            viewHolder.itemPieView.setPercentageTextSize(similarity_score*100);
        }else{
            viewHolder.itemPieView.setPercentageBackgroundColor(R.color.colorRed);
            viewHolder.itemPieView.setPercentageTextSize(similarity_score*100*(-1));
        }

        viewHolder.itemTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LovelyStandardDialog(context)
                        .setTopColorRes(R.color.indigo)
                        .setButtonsColorRes(R.color.colorRed)
                        //.setIcon(R.drawable.twitter_circle)
                        .setTitle("Connect")
                        .setMessage("@"+bestSimilarPerson.get(index).getPerson_screen_name()+" I found we've similar personal insights #ConnectInsight.Let's connect!")
                        .setPositiveButton("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TweetComposer.Builder builder = new TweetComposer.Builder(context)
                                        .text("@"+bestSimilarPerson.get(index).getPerson_screen_name()+" I found we've similar personal insights #ConnectInsight.Let's connect!")
                                        .image(Uri.parse(imageUrl));
                                builder.show();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bestSimilarPerson.size();
    }

}
