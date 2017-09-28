package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.R;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.weatherInfoHourly.HourlyForecast;

import java.util.HashMap;
import java.util.List;

/**
 * Created by omcna on 9/28/2017.
 */

class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.ViewHolder> {

    private List<HourlyForecast> list;
    private Context context;
    private String fOrC;
    private int positionWithHighTemp = 0;
    private int positionWithLowTemp;
    private static HashMap<String, String> icons = new HashMap<>();

    //icons
    static {
        icons.put("chanceflurries",String.valueOf(R.string.wi_snow));
        icons.put("chancerain",String.valueOf(R.string.wi_rain));
        icons.put("chancesleet",String.valueOf(R.string.wi_sleet));
        icons.put("chancesnow",String.valueOf(R.string.wi_snow));
        icons.put("chancetstorms",String.valueOf(R.string.wi_storm_showers));
        icons.put("clear",String.valueOf(R.string.wi_day_sunny));
        icons.put("cloudy",String.valueOf(R.string.wi_cloudy));
        icons.put("flurries",String.valueOf(R.string.wi_snow));
        icons.put("fog",String.valueOf(R.string.wi_fog));
        icons.put("hazy",String.valueOf(R.string.wi_day_haze));
        icons.put("mostlycloudy",String.valueOf(R.string.wi_day_cloudy_high));
        icons.put("mostlysunny",String.valueOf(R.string.wi_day_sunny_overcast));
        icons.put("partlycloudy",String.valueOf(R.string.wi_day_cloudy));
        icons.put("sleet",String.valueOf(R.string.wi_day_sleet));
        icons.put("rain",String.valueOf(R.string.wi_rain));
        icons.put("snow",String.valueOf(R.string.wi_snow));
        icons.put("sunny",String.valueOf(R.string.wi_day_sunny));
        icons.put("tstorms",String.valueOf(R.string.wi_thunderstorm));
        icons.put("unknown",String.valueOf(R.string.wi_na));
    }

    //constructor
    HoursAdapter(List<HourlyForecast> list, Context context, String fOrC) {
        this.list = list;
        this.context = context;
        this.fOrC = fOrC;
        //find the position with high and low temperature
        if(list.size() != 0) {
            double maxTemp = Double.parseDouble(list.get(0).getTemp().getEnglish());
            double lowTemp = Double.parseDouble(list.get(0).getTemp().getEnglish());
            for (int i = 1; i < list.size(); i++) {
                if (Double.parseDouble(list.get(i).getTemp().getEnglish()) > maxTemp) {
                    positionWithHighTemp = i;
                    maxTemp = Double.parseDouble(list.get(i).getTemp().getEnglish());
                }
                if (Double.parseDouble(list.get(i).getTemp().getEnglish()) < lowTemp) {
                    positionWithLowTemp = i;
                    lowTemp = Double.parseDouble(list.get(i).getTemp().getEnglish());
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_recycler_hours, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //set the time text
        holder.tvTime.setText(
                list.get(position).getFCTTIME().getHour() +
                        ":" +
                        list.get(position).getFCTTIME().getMin() +
                        " " +
                        list.get(position).getFCTTIME().getAmpm());

        //set icon
        String iconString = list.get(position).getIcon();
        holder.weatherIconView.setIconResource(context.getString(Integer.parseInt(icons.get(iconString))));
        holder.weatherIconView.setIconSize(30);

        //set the color of the icon of the low and high
        if(positionWithHighTemp != positionWithLowTemp) {
            if (position == positionWithHighTemp) {
                holder.weatherIconView.setIconColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
            if (position == positionWithLowTemp) {
                holder.weatherIconView.setIconColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }

        //set the temperature text
        if (fOrC.equals("F")) {
            String temperature = list.get(position).getTemp().getEnglish();
            String temperatureWithDegreeSign = temperature + context.getString(R.string.degree);
            holder.tvTemp.setText(temperatureWithDegreeSign);
        } else {
            String temperature = list.get(position).getTemp().getMetric();
            String temperatureWithDegreeSign = temperature + context.getString(R.string.degree);
            holder.tvTemp.setText(temperatureWithDegreeSign);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime;
        private TextView tvTemp;
        private WeatherIconView weatherIconView;

        ViewHolder(View itemView) {
            super(itemView);

            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvTemp = (TextView) itemView.findViewById(R.id.tvTemperature);
            weatherIconView = (WeatherIconView) itemView.findViewById(R.id.my_weather_icon);

        }
    }
}


