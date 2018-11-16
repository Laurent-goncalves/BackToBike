package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.WeatherIcons;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.UtilsTime;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;


public class WeatherViewHolder extends RecyclerView.ViewHolder{

    public WeatherViewHolder(View itemView) {
        super(itemView);
    }

    public void configureViews(WeatherForecast weather, int position, Context context) {

        ImageView iconWeather = itemView.findViewById(R.id.weather_image);
        TextView tempInfView = itemView.findViewById(R.id.temperature_inf_view);
        TextView tempSupView = itemView.findViewById(R.id.temperature_sup_view);
        TextView dateView = itemView.findViewById(R.id.date_weather);

        itemView.setBackgroundColor(context.getResources().getColor(R.color.colorWeather));

        // Get Drawable for the icon Weather
        int stringId = getIntegerIdentifier(context, "c" + weather.getCodeWeather());
        int weatherPosition = context.getResources().getInteger(stringId);

        if(position >= 0)
            iconWeather.setImageResource(WeatherIcons.weatherIds[weatherPosition]);

        // Set date and temperatures
        tempInfView.setText(weather.getTemperatureInf());
        tempSupView.setText(weather.getTemperatureSup());
        dateView.setText(UtilsTime.getDateWeather(context, weather.getDate()));
    }

    public static int getIntegerIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "integer", context.getPackageName());
    }
}
