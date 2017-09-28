package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.mainacivity;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.R;
import com.olegsagenadatrytwo.umbrellawalmartchallenge.model.custom.DayData;
import java.util.List;

/**
 * Created by omcna on 9/28/2017.
 */

class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    private List<DayData> listOfDays;
    private Context context;
    private String fOrC;

    DaysAdapter(List<DayData> listOfDays, Context context, String fOrC) {
        this.listOfDays = listOfDays;
        this.context = context;
        this.fOrC = fOrC;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_recycler_days, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //set the title of each day
        if (position == 0) {
            holder.tvDayTitle.setText(context.getString(R.string.Today));
        } else if (position == 1) {
            holder.tvDayTitle.setText(context.getString(R.string.Tomorrow));
        } else {
            holder.tvDayTitle.setText(listOfDays.get(position).getHourlyList().get(0).getFCTTIME().getWeekdayNameUnlang());
        }

        //create an adapter for each day
        holder.adapter = new HoursAdapter(listOfDays.get(position).getHourlyList(), context, fOrC);
        holder.rvHours.setAdapter(holder.adapter);
        holder.adapter.notifyDataSetChanged();


    }

    @Override
    public int getItemCount() {
        return listOfDays.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDayTitle;

        //recycler view for hours
        private RecyclerView rvHours;
        private GridLayoutManager layoutManager;
        private RecyclerView.ItemAnimator itemAnimator;
        private HoursAdapter adapter;


        ViewHolder(View itemView) {
            super(itemView);

            tvDayTitle = (TextView) itemView.findViewById(R.id.tvDayTitle);
            rvHours = (RecyclerView) itemView.findViewById(R.id.rvHours);

            layoutManager = new GridLayoutManager(context, 4);
            itemAnimator = new DefaultItemAnimator();
            rvHours.setLayoutManager(layoutManager);
            rvHours.setItemAnimator(itemAnimator);

        }
    }
}




