package com.g.laurent.backtobike.Views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.g.laurent.backtobike.Models.WeatherIcons;
import com.g.laurent.backtobike.R;
import com.g.laurent.backtobike.Utils.WeatherApi.WeatherForecast;


public class WeatherViewHolder extends RecyclerView.ViewHolder{

    public WeatherViewHolder(View itemView) {
        super(itemView);
    }

    public void configureImagesViews(WeatherForecast weather, Context context) {

        ImageView iconWeather = itemView.findViewById(R.id.weather_image);
        TextView tempInfView = itemView.findViewById(R.id.temperature_inf_view);
        TextView tempSupView = itemView.findViewById(R.id.temperature_sup_view);
        TextView dateView = itemView.findViewById(R.id.date_weather);

        // Get Drawable for the icon Weather
        int stringId = getStringIdentifier(context, "c" + weather.getCodeWeather());
        int position = context.getResources().getInteger(stringId);

        if(position >= 0)
            iconWeather.setImageResource(WeatherIcons.weatherIds[position]);

        // Set date and temperatures
        String tempInf = weather.getTemperatureInf() + "°C";
        tempInfView.setText(tempInf);

        String tempSup = weather.getTemperatureSup() + "°C";
        tempSupView.setText(tempSup);

        String date = weather.getDate() + "°C";
        dateView.setText(date);
    }

    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }
}
