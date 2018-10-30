package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.g.laurent.backtobike.Models.BikeEvent;
import com.g.laurent.backtobike.Models.CallbackMainActivity;
import com.g.laurent.backtobike.R;
import java.util.List;


public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private Context context;
    private List<BikeEvent> listBikeEvent;
    private String userId;
    private CallbackMainActivity callbackMainActivity;

    public EventAdapter(Context context, List<BikeEvent> listBikeEvent, String userId, CallbackMainActivity callbackMainActivity) {
        this.context = context;
        this.listBikeEvent = listBikeEvent;
        this.userId=userId;
        this.callbackMainActivity=callbackMainActivity;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bike_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.configureViews(listBikeEvent.get(holder.getAdapterPosition()), userId, context);
        //holder.itemView.setOnClickListener(v -> callbackMainActivity.showEvent(listBikeEvent.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        if(listBikeEvent!=null)
            return listBikeEvent.size();
        else
            return 0;
    }
}
