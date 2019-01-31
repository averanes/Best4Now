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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImputDataFragment extends Fragment {

    private ViewModel2 mViewModel;



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
        String[] itemsSex = new String[]{"Female", "Male", "Joint"};
        ArrayAdapter adapterSex = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsSex);
        //set the spinners adapter to the previously created one.
        dropdownSex.setAdapter(adapterSex);
        dropdownSex.setSelection(MainActivity.mainActivity.getInputDataCriteria().getSex(), true);


        final Spinner dropdownAge = view.findViewById(R.id.spinnerAge);
        String[] itemsAge = new String[]{"0-15", "16-45", "46..."};
        ArrayAdapter adapterAge = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsAge);
        dropdownAge.setAdapter(adapterAge);
        dropdownAge.setSelection(MainActivity.mainActivity.getInputDataCriteria().getAge(), true);

        final Spinner dropdownPersons = view.findViewById(R.id.spinnerPersons);
        String[] itemsPersons = new String[]{"1", "2", "Group"};
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

                MainActivity.mainActivity.showMapa();
            }
        });

       //MainActivity.weatherRequest.currentWeather(MapsFragment.mainPlace.getLocation(), MainActivity.mainActivity);

       // ((TextView) view.findViewById(R.id.textViewWeather)).setText( MainActivity.weatherRequest.getCurrentWeather().getWeatherConsideration() + " "+MainActivity.weatherRequest.getCurrentWeather().getTemperatureConsideration());

        Weather weather = MainActivity.mainActivity.getWeather();
        if(weather != null && weather.getDay()!=null){

            ((ImageView) view.findViewById(R.id.imageView)).setImageResource(WeatherRequest.getIconByWeatherHorary(MainActivity.mainActivity, weather.getWeatherConsideration(), weather.getHorarioConsideration()));


            String value="Temperature: "+ ((long)weather.getTemperature())+"°C "+weather.getTemperatureConsideration();
            ((TextView) view.findViewById(R.id.textViewTemperature)).setText(value);

            value="Weather: "+weather.getWeatherDescription()+" "+weather.getWeatherConsideration();
            ((TextView) view.findViewById(R.id.textViewWeather)).setText(value);

            //SimpleDateFormat s = new SimpleDateFormat("");
            value="Horary: "+weather.getDay().get(Calendar.HOUR_OF_DAY)+":"+weather.getDay().get(Calendar.MINUTE)+" "+weather.getHorarioConsideration();
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
        impC.setPersons(((Spinner)getView().findViewById(R.id.spinnerPersons)).getSelectedItemPosition()+1);



        MainActivity m =MainActivity.mainActivity;
        m.setInputDataCriteria(impC);

        m.performPrediction();

        String result= "Place recomendation with this data:\n";
        for (int i = 0; i < 6; i++) {

            result+= TypePlace.getTypeDescription(i) +" ("+Place.getRecomendedDescription(m.predictionCalculated[i+7])+")\n";
        }


        ((TextView) getView().findViewById(R.id.textViewRecomendation)).setText(result);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel2.class);
        // TODO: Use the ViewModel
    }

/*
    public void showMapa(View view){
        MainActivity.mainActivity.showMapa(view);

    }*/

    /*public View showMapa(){
        MainActivity.mainActivity.showMap();

        return null;
    }*/

}
