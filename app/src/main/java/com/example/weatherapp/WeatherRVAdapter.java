package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    @NonNull
    private Context context ;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList ;

    public WeatherRVAdapter(@NonNull Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item , parent , false) ;
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           WeatherRVModel model = weatherRVModelArrayList.get(position) ;
           holder.temperatureTV.setText(model.getTemperature()+"°C");
           Picasso.with(context).load("http:".concat(model.getIcon())).into(holder.conditionIV) ;
           holder.windTV.setText(model.getWindspeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd  hh:mm");
        SimpleDateFormat output = new SimpleDateFormat(" hh:mm");
        try{
            Date t = input.parse(model.getTime()) ;
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView windTV , timeTV , temperatureTV ;
        private ImageView conditionIV ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindspeed) ;
            timeTV = itemView.findViewById(R.id.idTVTime);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            conditionIV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
