package gwind.windalarm.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.util.Collections;

import gwind.windalarm.MainActivity;
import gwind.windalarm.R;
import gwind.windalarm.SplashActivity;
import gwind.windalarm.Spot;
import gwind.windalarm.SpotList;
import gwind.windalarm.UserProfile;
import gwind.windalarm.fragment.ItemTouchHelperAdapter;

public class RecyclerSpotOrderListAdapter extends RecyclerView.Adapter<RecyclerSpotOrderListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    public final List<Spot> mItems = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public RecyclerSpotOrderListAdapter(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
    }

    public void setSpotList(List<Spot> list) {
        mItems.clear();
        for (Spot spot: list) {
            if (spot.favorites)
                mItems.add(spot/*.spotName*/);
        }
        notifyDataSetChanged();


    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main2, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position).spotName);


        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                } /*else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP) {
                    //mDragStartListener.onStartDrag(holder);
                    int i = 0;
                    i++;
                    return false;
                }*/
                return false;
            }
        });


        holder.deleteImage.setTag(mItems.get(position).id);
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long spotId = (long) view.getTag();
                mDragStartListener.onRemove(spotId);
            }
        });

    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    protected static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;
        ImageView deleteImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            deleteImage = (ImageView) itemView.findViewById((R.id.remove));
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);

            String spotlist = "";
            RecyclerSpotOrderListAdapter adapter = (RecyclerSpotOrderListAdapter) ((RecyclerView)itemView.getParent()).getAdapter();
            for (int i = 0; i < adapter.mItems.size();i++) {
                Spot spot = adapter.mItems.get(i);
                if (i > 0)
                    spotlist += ",";
                spotlist += spot.id;
            }

            UserProfile profile = MainActivity.getUserProfile();

            SpotList mSpotList = new SpotList();
            mSpotList.updateFavorites(SplashActivity.getInstance(),0,profile.personId,SpotList.spotlist_reorder,spotlist);



        }
    }
}
