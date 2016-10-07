package com.caoye.simpleigdemo.dataAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.caoye.simpleigdemo.dataModel.IGFeed;
import com.caoye.simpleigdemo.R;
import com.caoye.simpleigdemo.view.FeedsActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.media.MediaPlayer;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;

/**
 * Created by admin on 10/6/16.
 */
public class FeedAdapter extends ArrayAdapter<IGFeed>{

    // implement the ViewHolder pattern
    private static class ViewHolder {
        TextView userName;
        ImageView userProfile;
        TextView date;
        ImageView photo;
        TextView likes;
        TextView caption;
        VideoView video;
    }
    List<IGFeed> feeds;

    public FeedAdapter(Context context, List<IGFeed> feeds) {
        //this.feeds = feeds;
        super(context, 0, feeds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IGFeed feed  = getItem(position);
        final ViewHolder viewHolder;

        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_feed, parent, false);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_userid);
            viewHolder.userProfile = (ImageView) convertView.findViewById(R.id.iv_profile);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tv_timestamp);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.iv_photo);
            viewHolder.video = (VideoView) convertView.findViewById(R.id.vv_video);
            viewHolder.likes = (TextView) convertView.findViewById(R.id.tv_likes);
            viewHolder.caption = (TextView) convertView.findViewById(R.id.tv_caption);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //set Details
        viewHolder.userName.setText(feed.getUsername());
        viewHolder.date.setText(DateUtils.getRelativeTimeSpanString(feed.getTimeStamp() * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        viewHolder.caption.setText(feed.getCaption());
        DecimalFormat formatter = new DecimalFormat("#,###");
        viewHolder.likes.setText(formatter.format(feed.getLikesCount()) + " likes");

        //Set profile image
        viewHolder.userProfile.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(35)
                .oval(true)
                .build();

        Picasso.with(getContext())
                .load(feed.getProfile_picture())
                .resize(120, 0)
                .transform(transformation)
                .into(viewHolder.userProfile);

        //Set image resource
        if(feed.getVideoUrl() == null) {
            viewHolder.video.setVisibility(View.INVISIBLE);
            viewHolder.photo.setVisibility(View.VISIBLE);
            viewHolder.photo.setImageResource(0);
            transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(25)
                    .build();

            final String imageUrl = feed.getImageUrl();
            Picasso.with(getContext())
                    .load(imageUrl)
                    .transform(transformation)
                    .placeholder(R.drawable.photo_placeholder)
                    .into(viewHolder.photo);

            BitmapDrawable drawable = (BitmapDrawable) viewHolder.photo.getDrawable();
            Bitmap bmp = drawable.getBitmap();

            File sdCardDirectory = Environment.getExternalStorageDirectory();
            File image = new File(sdCardDirectory, System.currentTimeMillis() + ".png");

            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(image);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                outStream.flush();
                outStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //set video
            viewHolder.photo.setVisibility(View.INVISIBLE);
            viewHolder.video.setVisibility(View.VISIBLE);

            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels - 50;
            int height = displayMetrics.heightPixels - 610;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.video.getLayoutParams();

            layoutParams.height = height;
            layoutParams.width = width;
            viewHolder.video.setLayoutParams(layoutParams);

            viewHolder.video.setVideoPath(feed.getVideoUrl());
            MediaController mediaController = new MediaController(getContext());
            viewHolder.video.setMediaController(mediaController);
            viewHolder.video.requestFocus();
            viewHolder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    viewHolder.video.start();
                }
            });
        }

        return convertView;
    }
}
