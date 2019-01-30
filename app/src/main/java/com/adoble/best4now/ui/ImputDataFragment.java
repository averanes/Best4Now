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
import com.adoble.best4now.util.WeatherResults;

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
        String[] itemsSex = new String[]{"Male", "Female", "Joint"};
        ArrayAdapter adapterSex = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsSex);
        //set the spinners adapter to the previously created one.
        dropdownSex.setAdapter(adapterSex);


        final Spinner dropdownAge = view.findViewById(R.id.spinnerAge);
        String[] itemsAge = new String[]{"0-10", "11-20", "21-35", "36-45", "46..."};
        ArrayAdapter adapterAge = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsAge);
        dropdownAge.setAdapter(adapterAge);

        final Spinner dropdownPersons = view.findViewById(R.id.spinnerPersons);
        String[] itemsPersons = new String[]{"1", "2", "Group"};
        ArrayAdapter adapterPersons = new ArrayAdapter<String>(MainActivity.mainActivity, android.R.layout.simple_spinner_dropdown_item, itemsPersons);
        dropdownPersons.setAdapter(adapterPersons);


        dropdownSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 2 && dropdownPersons.getSelectedItemPosition() == 0){
                    dropdownPersons.setSelection(2, true);
                }
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        view.findViewById(R.id.buttonProcessing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.InputDC == null){
                    MainActivity.InputDC = new InputDataCriteria();
                }

                MainActivity.InputDC.setAge(dropdownAge.getSelectedItemPosition());
                MainActivity.InputDC.setSex(dropdownSex.getSelectedItemPosition());
                MainActivity.InputDC.setPersons(dropdownPersons.getSelectedItemPosition());
            }
        });

       //MainActivity.weatherResults.currentWeather(MapsFragment.mainPlace.getLocation(), MainActivity.mainActivity);

       // ((TextView) view.findViewById(R.id.textViewWeather)).setText( MainActivity.weatherResults.getCurrentWeather().getWeatherConsideration() + " "+MainActivity.weatherResults.getCurrentWeather().getTemperatureConsideration());



        ((ImageView) view.findViewById(R.id.imageView)).setImageResource(WeatherResults.getIconByWeatherHorary(MainActivity.mainActivity, 3, 2));

        return view;
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
