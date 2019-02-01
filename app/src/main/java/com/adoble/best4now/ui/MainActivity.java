package com.adoble.best4now.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.util.ExternalDbOpenHelper;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    private MapsFragment maps;

    private InputDataCriteria InputDC;
    private Weather weather;

    private static ExternalDbOpenHelper dbOpenHelper;

    public static int[] predictionCalculated = new int[13];

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

    public void performPrediction() {

        ExternalDbOpenHelper dbOpenHelper = MainActivity.mainActivity.getConection();
        dbOpenHelper.openDataBase();

        Weather weather = getWeather();
        InputDataCriteria inputC = getInputDataCriteria();

        this.predictionCalculated = dbOpenHelper.getPrediction(inputC.getSex(), inputC.getAge(), inputC.getPersons(), weather.getTemperatureConsideration(), weather.getWeatherConsideration(), weather.getHorarioConsideration());

    }

    public void performPrediction(InputDataCriteria inputC) {

        ExternalDbOpenHelper dbOpenHelper = MainActivity.mainActivity.getConection();
        dbOpenHelper.openDataBase();

        Weather weather = getWeather();

        this.predictionCalculated = dbOpenHelper.getPrediction(inputC.getSex(), inputC.getAge(), inputC.getPersons(), weather.getTemperatureConsideration(), weather.getWeatherConsideration(), weather.getHorarioConsideration());

    }

    public void resetPrediction() {
        predictionCalculated = new int[13];
    }

    public Weather getWeather() {
        if (weather == null)
            weather = new Weather();

        return weather;
    }

    public void setWeather(Weather weatherP) {

        Toast.makeText(this.getApplicationContext(), /*"horary: "+weatherP.getDay().toString()+*/" Weather: " + weatherP.getWeatherDescription() + " temp: " + weatherP.getTemperature(), Toast.LENGTH_LONG).show();
        weather = weatherP;

        performPrediction();
    }

    public InputDataCriteria getInputDataCriteria() {
        if (InputDC == null)
            InputDC = new InputDataCriteria();

        return InputDC;
    }

    public void setInputDataCriteria(InputDataCriteria inputDataCriteria) {
        InputDC = inputDataCriteria;
    }


    public ExternalDbOpenHelper getConection() {
        if (dbOpenHelper == null)
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
            case R.id.action_input_data:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, ImputDataFragment.newInstance()).commit();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_nearby_places:

                if (maps != null) {

                    //maps.updateLastKnowLocation();
                    // maps.showLastKnowLocation();

                    if (InputDC == null) {
                        showMessage("You must complete the data for obtain recomended places.");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, ImputDataFragment.newInstance()).commit();
                    } else {
                        maps.searchNearbyPlaces();
                    }
                }
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.action_language:
                showDialogLanguage();
                return true;

            case R.id.action_recommended_option:
                showDialogRecomendedOption();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    int selectedOption=-1;
    public void showDialogLanguage() {
        String[] languageTem = {"EN", "IT", "ES"};

        SharedPreferences prfs = getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE);
        String lang = prfs.getString("language", "EN");


        switch (lang) {
            case "IT":
                languageTem = new String[]{ "IT", "EN", "ES"};  break;
            case "ES":
                languageTem = new String[]{ "ES", "EN", "IT"};  break;
        }

        final String[] language = languageTem;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //alertDialog.setIcon(R.drawable.dialogopng);
        alertDialog.setTitle("Select Language");



        alertDialog.setSingleChoiceItems(language, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedOption = which;
            }
        });


        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedOption!=-1){
                    showMessage("You must restart de applications");

                    SharedPreferences.Editor edit =  getSharedPreferences("AUTHENTICATION_FILE_NAME", Context.MODE_PRIVATE).edit();

                    edit.putString("language", language[selectedOption]);
                    edit.commit();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                         updateResourcesLocale(MainActivity.this, new Locale(language[selectedOption].toLowerCase()));
                    } else updateResourcesLocaleLegacy(MainActivity.this, new Locale(language[selectedOption].toLowerCase()));
                }

                dialog.cancel();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });


        alertDialog.show();


    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public void showDialogRecomendedOption() {
        final String[] language = getResources().getStringArray(R.array.option_list);




        final boolean[] itemsChecked = new boolean[language.length];
        itemsChecked[1] = true;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //alertDialog.setIcon(R.drawable.dialogopng);
        alertDialog.setTitle("Select Language");

        alertDialog.setMultiChoiceItems(language, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which] = isChecked;
            }
        });

        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Dutch", Toast.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessage("You must restart de applications");

            }
        });
        alertDialog.show();


    }

    public void showMapa() {
        if (maps == null) {
            maps = MapsFragment.newInstance(this);

        }
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        supportMapFragment.getMapAsync(maps);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();
    }

    public void showMessage(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

}
