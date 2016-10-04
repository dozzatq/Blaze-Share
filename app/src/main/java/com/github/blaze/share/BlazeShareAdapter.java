package com.github.blaze.share;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by RondailP on 04.10.2016.
 */
public class BlazeShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppCompatActivity appContext;
    private Intent shareIntent;
    private List<ResolveInfo> activities;
    private PackageManager packageManager;
    private RecyclerView recyclerView;
    private String broadReceiver;

    public BlazeShareAdapter(AppCompatActivity appContext, Intent shareIntent, String broadReceiver) {
        this.appContext = appContext;
        this.shareIntent = shareIntent;
        this.broadReceiver = broadReceiver;

        if (appContext==null)
        {
            throw new NullPointerException("appContext in BlazeShareAdapter not be null!");
        }
        if (shareIntent==null)
        {
            throw new NullPointerException("shareIntent in BlazeShareAdapter not be null!");
        }

        activities = appContext.getPackageManager().queryIntentActivities (shareIntent, 0);
        packageManager = appContext.getPackageManager();
        if (packageManager==null)
        {
            throw new NullPointerException("appContext.getPackageManager in BlazeShareAdapter not be null!");
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BlazeShareHolder blazeShareHolder =  new BlazeShareHolder(LayoutInflater.from(appContext).inflate(R.layout.item_share_blaze, parent, false));

        ColorDrawable colorDrawableSelected =
                new ColorDrawable(ContextCompat.getColor(appContext, R.color.blazeShareSelectedColor));

        // create StateListDrawable object and define its states
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, colorDrawableSelected);
        stateListDrawable.addState(StateSet.WILD_CARD, null);

        // set the StateListDrawable as background of the item view
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            blazeShareHolder.itemView.setBackgroundDrawable(stateListDrawable);
        }
        else {
            blazeShareHolder.itemView.setBackground(stateListDrawable);
        }

        return blazeShareHolder;
    }

    int mCurrentSelectedPosition = -1;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BlazeShareHolder)
        {
            int realPosition = holder.getAdapterPosition();
            final ResolveInfo info = activities.get(realPosition);
            try {
                ((BlazeShareHolder) holder).getShareImageView().setImageDrawable(info.loadIcon(packageManager));
            }
            catch (Throwable e)
            {

            }
            ((BlazeShareHolder) holder).getShareTextView().setText(info.loadLabel(packageManager));

            if (position == mCurrentSelectedPosition) {
                holder.itemView.setSelected(true);
            }
            else {
                holder.itemView.setSelected(false);
            }

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction()==MotionEvent.ACTION_DOWN)
                    {
                        int oldSelectedPosition = mCurrentSelectedPosition;

                        if (holder.getAdapterPosition() != mCurrentSelectedPosition) {
                            mCurrentSelectedPosition = holder.getAdapterPosition();

                            if (oldSelectedPosition != -1) {
                                BlazeShareHolder yourViewHolder = (BlazeShareHolder) recyclerView.findViewHolderForLayoutPosition(oldSelectedPosition);
                                yourViewHolder.itemView.setSelected(false);
                            }

                            BlazeShareHolder yourViewHolder = (BlazeShareHolder) recyclerView.findViewHolderForLayoutPosition(mCurrentSelectedPosition);
                            yourViewHolder.itemView.setSelected(true);
                        }
                    }
                    return false;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ResolveInfo info = activities.get(holder.getAdapterPosition());
                    Intent intent = new Intent(broadReceiver);
                    intent.putExtra(BlazeShare.SEND_SELECTED_ACTION,info.activityInfo.packageName );
                    LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        appContext.finishAfterTransition();
                    }
                    else appContext.finish();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}
