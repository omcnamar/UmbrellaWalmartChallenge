package com.olegsagenadatrytwo.umbrellawalmartchallenge.view.settingsactivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.olegsagenadatrytwo.umbrellawalmartchallenge.R;

import java.util.List;

/**
 * Created by omcna on 9/28/2017.
 */

class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //constants
    private static final String SETTINGS_PREF_FILE = "settings";
    private static final String CURRENT_SETTING = "F/C";
    private static final String ZIP_CODE = "zip_code";

    private List<String> settingsList;
    private Context context;

    /**constructor */
    SettingsAdapter(List<String> settingsList, Context context){
        this.settingsList = settingsList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                final View view = LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.layout_for_settings_recycler_view_zip_code, parent, false);
                return new ZipCodeViewHolder(view);
            case 1:
                final View view2 = LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.layout_for_settings_recycler_view_units, parent, false);
                return new UnitsViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        switch (holder.getItemViewType()) {

            //case for the ZipCodeViewHolder
            case 0:

                //create a ZipCodeViewHolder
                final ZipCodeViewHolder zipCodeViewHolder = (ZipCodeViewHolder)holder;

                //get the Zip code from shared pref
                String zipCode = sharedPreferences.getString(ZIP_CODE, "default");

                //if there is no zip code show dialog
                if(zipCode.equals("default")){
                    showZipCodeDialog(zipCodeViewHolder.tvZipCode);
                }else{
                    zipCodeViewHolder.tvZipCode.setText(zipCode);
                }

                //show the setting title
                zipCodeViewHolder.tvSettingType.setText(settingsList.get(position));
                zipCodeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showZipCodeDialog(zipCodeViewHolder.tvZipCode);
                    }
                });
                break;

            //case for the UnitsViewHolder
            case 1:

                //create a UnitViewHolder
                final UnitsViewHolder unitsViewHolder = (UnitsViewHolder)holder;

                //show setting title
                unitsViewHolder.tvSettingType.setText(settingsList.get(position));

                //get the unit from shared pref
                String fahrenheitOrCelsius = sharedPreferences.getString(CURRENT_SETTING, "default");

                //if there is no unit save fahrenheit as default
                if(fahrenheitOrCelsius.equals("default")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(CURRENT_SETTING, context.getString(R.string.fahrenheit));
                    editor.apply();
                    unitsViewHolder.tvUnits.setText(R.string.fahrenheit);
                }else{
                    unitsViewHolder.tvUnits.setText(fahrenheitOrCelsius);
                }

                //on click  to change between C and F
                unitsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fOrC = sharedPreferences.getString(CURRENT_SETTING, "default");
                        if(fOrC.equals(context.getString(R.string.fahrenheit))){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(CURRENT_SETTING, context.getString(R.string.celsius));
                            editor.apply();
                            unitsViewHolder.tvUnits.setText(context.getString(R.string.celsius));
                        }else{
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(CURRENT_SETTING, context.getString(R.string.fahrenheit));
                            editor.apply();
                            unitsViewHolder.tvUnits.setText(context.getString(R.string.fahrenheit));
                        }
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    private class ZipCodeViewHolder extends RecyclerView.ViewHolder{

        private TextView tvZipCode;
        private TextView tvSettingType;

        ZipCodeViewHolder(View itemView) {
            super(itemView);
            tvZipCode = (TextView) itemView.findViewById(R.id.tvZipCode);
            tvSettingType = (TextView) itemView.findViewById(R.id.tvSettingType);
        }
    }

    private class UnitsViewHolder extends RecyclerView.ViewHolder{

        private TextView tvUnits;
        private TextView tvSettingType;

        UnitsViewHolder(View itemView) {
            super(itemView);
            tvUnits = (TextView) itemView.findViewById(R.id.tvUnit);
            tvSettingType = (TextView) itemView.findViewById(R.id.tvSettingType);
        }
    }

    /**
     * This method will show custom dialog to enter the zipcode
     */
    public void showZipCodeDialog(final TextView tvZipCode) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);

        Button ok = (Button) dialog.findViewById(R.id.btnSubmit);
        final EditText zip = (EditText) dialog.findViewById(R.id.etZipCode);
        //if button is clicked, close the custom dialog
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ZIP_CODE, zip.getText().toString());
                editor.apply();
                tvZipCode.setText(zip.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
