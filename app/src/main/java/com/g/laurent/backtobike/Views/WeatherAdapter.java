package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;
import java.util.List;


public class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    private List<WeatherForecast> listWeather;
    private Context context;

    public WeatherAdapter(List<WeatherForecast> listWeather, Context context) {
        this.listWeather = listWeather;
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weather_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.configureImagesViews(listWeather.get(holder.getAdapterPosition()), context);
    }

    @Override
    public int getItemCount() {
        if(listWeather!=null)
            return listWeather.size();
        else
            return 0;
    }

}
