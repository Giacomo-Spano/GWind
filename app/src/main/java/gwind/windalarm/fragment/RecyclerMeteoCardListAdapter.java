/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gwind.windalarm.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import gwind.windalarm.R;
import gwind.windalarm.controls.WindControl;
import gwind.windalarm.controls.WindIndicator;
import gwind.windalarm.data.MeteoStationData;

public class RecyclerMeteoCardListAdapter extends RecyclerView.Adapter<RecyclerMeteoCardListAdapter.ItemViewHolder> {

    private final List<MeteoStationData> mItems = new ArrayList<>();

    OnListener mCallback;
    public interface OnListener {
        void onSpotClick(long spotId);
    }
    public void setListener(OnListener listener) {
        mCallback = listener;
    }


    public RecyclerMeteoCardListAdapter(Context context) {
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (mItems == null ||position < 0 || position > mItems.size())
            return;
        if (mItems.get(position).spotName != null)
            holder.spotNameTextView.setText(mItems.get(position).spotName);
        if (mItems.get(position).direction != null) {
            holder.directionTextView.setText(mItems.get(position).direction);
            holder.windControl.setDirection(mItems.get(position).directionangle, mItems.get(position).direction);
        }
        if (mItems.get(position).speed != -1) {
            holder.speedTextView.setText("" + mItems.get(position).speed);
            holder.windIndicator.setWindSpeed(mItems.get(position).speed, "");
        }
        //if (mItems.get(position).spotName != null)
            holder.speedUnitTextView.setText("km/h");
        if (mItems.get(position).averagespeed != null)
            holder.avspeedTextView.setText(""+mItems.get(position).averagespeed);
        if (mItems.get(position).date != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            holder.dateTextView.setText(df.format(mItems.get(position).date));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onSpotClick(mItems.get(position).spotID);
            }
        });

        // Start a drag whenever the handle view it touched
        /*holder.linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    //mDragStartListener.onStartDrag(holder);
                    mCallback.onSpotClick(mItems.get(position).spotID);
                }


                return false;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setMeteoDataList(List<MeteoStationData> list) {
        mItems.clear();
        for (MeteoStationData data : list) {
            mItems.add(data);
        }
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public final LinearLayout linearLayout;
        public final TextView spotNameTextView;
        public final TextView directionTextView;
        public final TextView speedTextView;
        public final TextView speedUnitTextView;
        public final TextView avspeedTextView;
        public final TextView dateTextView;
        public final WindIndicator windIndicator;
        public final WindControl windControl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.meteocarditem);
            spotNameTextView = (TextView) itemView.findViewById(R.id.spotNameTextView);
            directionTextView = (TextView) itemView.findViewById(R.id.directionTextView);
            speedTextView = (TextView) itemView.findViewById(R.id.speedTextView);
            speedUnitTextView = (TextView) itemView.findViewById(R.id.speedUnitTextView);
            avspeedTextView = (TextView) itemView.findViewById(R.id.avspeedTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            windIndicator = (WindIndicator) itemView.findViewById(R.id.windindicator);
            windControl = (WindControl) itemView.findViewById(R.id.windcontrol);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
