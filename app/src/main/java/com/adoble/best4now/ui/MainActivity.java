package com.adoble.best4now.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.util.WeatherResults;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;

    MapsFragment maps;

    public static InputDataCriteria InputDC;
    public static WeatherResults weatherResults = new WeatherResults();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mainActivity = this;

        if (maps == null) {
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            maps = MapsFragment.newInstance(this);
            supportMapFragment.getMapAsync(maps);

            getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();


        }

        ActionBar actionBar = getSupportActionBar();
        //setSupportActionBar(myToolbar);


        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ImputDataFragment.newInstance()).commit();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:

                if(maps!= null){

                    //maps.updateLastKnowLocation();
                   // maps.showLastKnowLocation();

                    maps.searchNearbyPlaces();
                }
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void showMapa(View view){
        if (maps == null) {
            maps = MapsFragment.newInstance(this);

        }
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(maps);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();
    }

}
