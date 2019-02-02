package com.adoble.best4now.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.domain.Place;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.util.TypePlace;
import com.adoble.best4now.util.WeatherRequest;

import java.util.Calendar;

public class ImputDataFragment extends Fragment {



    public static ImputDataFragment newInstance() {
        return new ImputDataFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);

        final Spinner dropdownSex = view.findViewById(R.id.spinnerSex);
        //create a list of items for the spinner.
        String[] itemsSex = this.getResources().getStringArray(R.array.gender_list);
        ArrayAdapter adapterSex = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsSex);
        //set the spinners adapter to the previously created one.
        dropdownSex.setAdapter(adapterSex);
        dropdownSex.setSelection(MainActivity.mainActivity.getInputDataCriteria().getSex(), true);


        final Spinner dropdownAge = view.findViewById(R.id.spinnerAge);
        String[] itemsAge = this.getResources().getStringArray(R.array.grouped_age_list);
        ArrayAdapter adapterAge = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsAge);
        dropdownAge.setAdapter(adapterAge);
        dropdownAge.setSelection(MainActivity.mainActivity.getInputDataCriteria().getAge(), true);

        final Spinner dropdownPersons = view.findViewById(R.id.spinnerPersons);
        String[] itemsPersons = this.getResources().getStringArray(R.array.amount_person_list);
        ArrayAdapter adapterPersons = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsPersons);
        dropdownPersons.setAdapter(adapterPersons);
        dropdownPersons.setSelection(MainActivity.mainActivity.getInputDataCriteria().getPersons(), true);

        dropdownSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 2 && dropdownPersons.getSelectedItemPosition() == 0){
                    int valueT=MainActivity.mainActivity.getInputDataCriteria().getPersons();
                    if(valueT == 0) valueT = 2;
                    dropdownPersons.setSelection(valueT, true);
                }

                calculateRecomendation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownPersons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0 && dropdownSex.getSelectedItemPosition() == 2){
                    dropdownSex.setSelection(0, true);
                }

                calculateRecomendation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dropdownAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateRecomendation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        view.findViewById(R.id.buttonProcessing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.mainActivity.getInputDataCriteria().setAge(dropdownAge.getSelectedItemPosition());
                MainActivity.mainActivity.getInputDataCriteria().setSex(dropdownSex.getSelectedItemPosition());
                MainActivity.mainActivity.getInputDataCriteria().setPersons(dropdownPersons.getSelectedItemPosition());

                MainActivity.mainActivity.performPrediction();

                MainActivity.mainActivity.showMapaAndSearchNearbyPlaces();
            }
        });

       //MainActivity.weatherRequest.currentWeather(MapsFragment.mainPlace.getLocation(), MainActivity.mainActivity);

       // ((TextView) view.findViewById(R.id.textViewWeather)).setText( MainActivity.weatherRequest.getCurrentWeather().getWeatherConsideration() + " "+MainActivity.weatherRequest.getCurrentWeather().getTemperatureConsideration());

        Weather weather = MainActivity.mainActivity.getWeather();
        if(weather != null && weather.getDay()!=null){

            ((ImageView) view.findViewById(R.id.imageView)).setImageResource(WeatherRequest.getIconByWeatherHorary(MainActivity.mainActivity, weather.getWeatherConsideration(), weather.getHorarioConsideration()));


            String value= this.getResources().getString(R.string.temperature) + ": "+ ((long)weather.getTemperature())+"°C "+Weather.temperatureConsiderationName(weather.getTemperatureConsideration());
            ((TextView) view.findViewById(R.id.textViewTemperature)).setText(value);

            value=this.getResources().getString(R.string.weather) + ": "+weather.getWeatherDescription()+" "+Weather.weatherConsiderationName(weather.getWeatherConsideration());
            ((TextView) view.findViewById(R.id.textViewWeather)).setText(value);

            //SimpleDateFormat s = new SimpleDateFormat("");
            value=this.getResources().getString(R.string.schedule) + ": "+weather.getDay().get(Calendar.HOUR_OF_DAY)+":"+weather.getDay().get(Calendar.MINUTE)+" "+Weather.horarioConsiderationName(weather.getHorarioConsideration());
            ((TextView) view.findViewById(R.id.textViewHour)).setText(value);
        }




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        calculateRecomendation();
    }

    public void calculateRecomendation(){


        InputDataCriteria impC=new InputDataCriteria();

        impC.setAge(((Spinner)getView().findViewById(R.id.spinnerAge)).getSelectedItemPosition());
        impC.setSex(((Spinner)getView().findViewById(R.id.spinnerSex)).getSelectedItemPosition());
        impC.setPersons(((Spinner)getView().findViewById(R.id.spinnerPersons)).getSelectedItemPosition());



        MainActivity m =MainActivity.mainActivity;
        m.setInputDataCriteria(impC);

        m.performPrediction();

        String welcome = "";
        if(MainActivity.mainActivity.account!=null &&  MainActivity.mainActivity.account.getDisplayName() != null ){
            welcome = "Hello "+MainActivity.mainActivity.account.getDisplayName()+", " + this.getResources().getString(R.string.recommended_places) + "\n";
        }else{
            welcome=this.getResources().getString(R.string.recommended_place_with_data) + "\n\n";
        }

        String result= welcome+ this.getResources().getString(R.string.here_some_recommendations) + ":\n\n";
        for (int i = 0; i < 6; i++) {
            result+= TypePlace.getTypeDescription(i) +" ("+Place.getRecomendedDescription(m.getApplicationContext(), m.predictionCalculated[i+7])+")\n";
        }


        ((TextView) getView().findViewById(R.id.textViewRecomendation)).setText(result);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

/*
    public void showMapaAndSearchNearbyPlaces(View view){
        MainActivity.mainActivity.showMapaAndSearchNearbyPlaces(view);

    }*/

    /*public View showMapaAndSearchNearbyPlaces(){
        MainActivity.mainActivity.showMap();

        return null;
    }*/

}
