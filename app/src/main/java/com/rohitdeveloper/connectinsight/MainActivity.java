package com.rohitdeveloper.connectinsight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private SharedPreferences sharedPref;


    private ArrayList<Person> bestSimilarPerson;
    private Person accountPerson;
    private String searchQuery=null;

    //For showing listView
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    //Navigation drawer references
    private ImageView nav_profileImage;
    private TextView nav_user_name, nav_user_screen_name, nav_user_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bestSimilarPerson=new ArrayList<Person>();
        bestSimilarPerson=(ArrayList<Person>)getIntent().getSerializableExtra("BestSimilarPerson");
        accountPerson=(Person) getIntent().getSerializableExtra("AccountPerson");
        searchQuery=getIntent().getStringExtra("Query");

        sharedPref= getApplicationContext().getSharedPreferences("ConnectInsight", Context.MODE_PRIVATE);

        for(Person person:bestSimilarPerson){
            Log.d(TAG,person.getPerson_screen_name()+" "+person.getPerson_similarity_score());
        }

        recyclerView=(RecyclerView) findViewById(R.id.id_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter=new RecyclerAdapter(bestSimilarPerson,accountPerson.getPerson_profile_image_url(),MainActivity.this);
        recyclerView.setAdapter(adapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsActivityIntent = new Intent(MainActivity.this,MapsActivity.class);
                mapsActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mapsActivityIntent.putExtra("BestSimilarPerson",(ArrayList<Person>) bestSimilarPerson);
                startActivity(mapsActivityIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        //(NavigationDrawer) get reference to all
        nav_profileImage = (ImageView) headerView.findViewById(R.id.id_nav_profileImage);
        nav_user_name = (TextView) headerView.findViewById(R.id.id_nav_user_name);
        nav_user_screen_name= (TextView) headerView.findViewById(R.id.id_nav_user_screen_name);
        nav_user_location = (TextView) headerView.findViewById(R.id.id_nav_user_location);
        updateNavigationView();

    }

    private  void updateNavigationView(){
        nav_user_name.setText(accountPerson.getPerson_name());
        nav_user_screen_name.setText("@"+accountPerson.getPerson_screen_name());
        nav_user_location.setText(accountPerson.getPerson_location());
        Picasso.with(MainActivity.this).load(accountPerson.getPerson_profile_image_url()).placeholder(R.drawable.nav_profile_avatar).fit().into(nav_profileImage);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweets) {
            Intent queryTweetActivityIntent = new Intent(MainActivity.this,QueryTweetActivity.class);
            queryTweetActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            queryTweetActivityIntent.putExtra("Query",searchQuery);
            startActivity(queryTweetActivityIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_manage) {
            // Handle the manage action
             getNewDistance();
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getNewDistance(){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.manage_distance_prompt, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Reset Distance");
        builder.setMessage("Enter the distance in Km");
        builder.setView(dialogView);
        final EditText input = (EditText) dialogView.findViewById(R.id.id_prompt_distance);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("GeoDistance",input.getText().toString());
                editor.commit();
                dialog.dismiss();
                Toast.makeText(MainActivity.this,"Distance changed!",Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
