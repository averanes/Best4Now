package com.adoble.best4now.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.adoble.best4now.R;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity {

    MapsActivity maps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {




            SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
            maps = MapsActivity.newInstance(this);
            supportMapFragment.getMapAsync(maps);

            getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();





/*
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(maps);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mapFragment)
                    .commitNow();*/
        }
    }

}
