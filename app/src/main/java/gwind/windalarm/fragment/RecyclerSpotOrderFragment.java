package gwind.windalarm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gwind.windalarm.Spot;
import gwind.windalarm.helper.OnStartDragListener;
import gwind.windalarm.helper.SimpleItemTouchHelperCallback;

public class RecyclerSpotOrderFragment extends Fragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;

    public RecyclerSpotOrderFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerSpotOrderAdapter adapter = new RecyclerSpotOrderAdapter(getActivity(), this);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    OnSpotOrderClickListener mCallback;

    // Container Activity must implement this interface
    public interface OnSpotOrderClickListener {
        void OnSpotOrderClick(Spot spot);
    }

    public void setListener(OnSpotOrderClickListener listener) {
        mCallback = listener;
    }
}
