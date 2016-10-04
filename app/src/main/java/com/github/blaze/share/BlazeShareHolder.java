package com.github.blaze.share;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by RondailP on 04.10.2016.
 */
public class BlazeShareHolder extends RecyclerView.ViewHolder{

    private AppCompatImageView shareImageView;
    private AppCompatTextView shareTextView;

    public BlazeShareHolder(View itemView) {
        super(itemView);
        shareImageView = (AppCompatImageView) itemView.findViewById(R.id.shareImage);
        shareTextView = (AppCompatTextView) itemView.findViewById(R.id.shareAppName);
    }

    public AppCompatTextView getShareTextView() {
        return shareTextView;
    }

    public AppCompatImageView getShareImageView() {
        return shareImageView;
    }
}
