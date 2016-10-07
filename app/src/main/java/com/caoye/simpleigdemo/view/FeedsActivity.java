package com.caoye.simpleigdemo.view;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.caoye.simpleigdemo.ApplicationData;
import com.caoye.simpleigdemo.R;
import com.caoye.simpleigdemo.dataAdapter.FeedAdapter;
import com.caoye.simpleigdemo.dataModel.IGFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedsActivity extends AppCompatActivity {

    private ArrayList<IGFeed> feeds;
    private ListView lvFeeds;
    private FeedAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private static OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        feeds = new ArrayList<>();
        adapter = new FeedAdapter(this, feeds);
        lvFeeds = (ListView)findViewById(R.id.lv_feeds);
        lvFeeds.setAdapter(adapter);

        if(!isNetworkAvailable())
        {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(FeedsActivity.this);
            dlgAlert.setMessage(R.string.no_network_errormsg);
            dlgAlert.setTitle(R.string.error_title);
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.create().show();
            return;
        }

        fetchFeeds();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Rrefresh feed list.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void fetchTimelineAsync(int page) {
        fetchFeeds();
        swipeContainer.setRefreshing(false);
    }

    public void fetchFeeds() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.instagram.com/v1/users/self/media/recent").newBuilder();
        urlBuilder.addQueryParameter("access_token", ApplicationData.ACCESS_TOKEN);
        String url = urlBuilder.build().toString();

        System.out.print(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Read data on the worker thread
                final String jsonData = response.body().string();
                FeedsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(jsonData);
                            JSONArray feedsJson = jObject.getJSONArray("data");

                            for(int i = 0; i < feedsJson.length(); i++) {
                                JSONObject feedJson = feedsJson.getJSONObject(i);
                                String type;
                                type = feedJson.getString("type");
                                IGFeed feed = new IGFeed();
                                feed.setUsername(feedJson.getJSONObject("user").getString("username"));

                                //if (photoJson.optJSONObject("caption") != null)
                                feed.setCaption(feedJson.getJSONObject("caption").getString("text"));

                                if("image".equalsIgnoreCase(type))
                                    feed.setImageUrl(feedJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                                else
                                    feed.setVideoUrl(feedJson.getJSONObject("videos").getJSONObject("standard_resolution").getString("url"));

                                //photo.imageHeight = photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("height");
                                feed.setLikesCount(feedJson.getJSONObject("likes").getInt("count"));
                                feed.setProfile_picture(feedJson.getJSONObject("user").getString("profile_picture"));
                                feed.setTimeStamp(feedJson.getLong("created_time"));
                                feeds.add(0, feed);
                            }
                        }
                        catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
