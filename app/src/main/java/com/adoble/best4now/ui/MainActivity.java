package com.adoble.best4now.ui;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.util.ExternalDbOpenHelper;
import com.adoble.best4now.util.WeatherRequest;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    private MapsFragment maps;

    private InputDataCriteria InputDC;
    private Weather weather;

    private static ExternalDbOpenHelper dbOpenHelper;

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


    public Weather getWeather(){
        if(weather == null)
            weather = new Weather();

        return weather;
    }
    public void setWeather(Weather weatherP){

        Toast.makeText(this.getApplicationContext(), "horary: "+weatherP.getDay().toString()+" Weather: "+weatherP.getMainWeather()+" tempC: "+weatherP.getTemperatureConsideration()+" temp: "+weatherP.getTemperature(), Toast.LENGTH_SHORT).show();
        weather = weatherP;
    }

    public InputDataCriteria getInputDataCriteria(){
        if(InputDC == null)
        InputDC = new InputDataCriteria();

        return InputDC;
    }
    public void setInputDataCriteria(InputDataCriteria inputDataCriteria){
         InputDC = inputDataCriteria;
    }



    public ExternalDbOpenHelper getConection(){
        if(dbOpenHelper == null)
        dbOpenHelper = new ExternalDbOpenHelper(this);
        //dbOpenHelper.openDataBase();
        //int[] prediction =dbOpenHelper.getPrediction(1, 2, 2, 1, 0, 2);

       /* System.out.println("********************************");
        String resul="";
        for (int value:prediction) {
           resul+=value+" ";

        }

        System.out.print(resul);*/

       return dbOpenHelper;
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

    public void showMapa(){
        if (maps == null) {
            maps = MapsFragment.newInstance(this);

        }
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(maps);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();
    }

}
