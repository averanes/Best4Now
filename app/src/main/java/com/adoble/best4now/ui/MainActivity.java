package com.adoble.best4now.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.util.ExternalDbOpenHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static GoogleSignInAccount account;

    public static MainActivity mainActivity;
    private MapsFragment maps;

    private InputDataCriteria InputDC;
    private Weather weather;

    private static ExternalDbOpenHelper dbOpenHelper;

    public static int[] predictionCalculated = new int[13];

    // se pueden almacenar valores como -1, 0, 1, 2 (tipos de recomendacion)
    private List<Integer> selectedRecommendations;

    boolean mapsActive = true;

    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    final int RC_SIGN_IN = 3;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mainActivity = this;

        // la primera vez q se ejecuta
        if (selectedRecommendations == null) {
            selectedRecommendations = new ArrayList<Integer>();

            // la primera vez por default seleccionamos todos
            selectedRecommendations.add(-1);
            selectedRecommendations.add(0);
            selectedRecommendations.add(1);
            selectedRecommendations.add(2);
        }


       /* if (maps == null) {
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            maps = MapsFragment.newInstance(this);
            supportMapFragment.getMapAsync(maps);

            getSupportFragmentManager().beginTransaction().replace(R.id.container, supportMapFragment).commit();


        }*/


        ActionBar actionBar = getSupportActionBar();
        //setSupportActionBar(myToolbar);


        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.


        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
        updateUI(account);
        }
        else{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
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

        weather = weatherP;

        String value="Temperature: "+ ((long)weather.getTemperature())+"°C "+Weather.temperatureConsiderationName(weather.getTemperatureConsideration());

        value+="\nWeather: "+weather.getWeatherDescription()+" "+Weather.weatherConsiderationName(weather.getWeatherConsideration());

       // value+="\nHorary: "+weather.getDay().get(Calendar.HOUR_OF_DAY)+":"+weather.getDay().get(Calendar.MINUTE)+" "+Weather.horarioConsiderationName(weather.getHorarioConsideration());

        showMessage(value);



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

                mapsActive = true;
                return true;

            case R.id.action_nearby_places:

                if (maps != null) {

                    //maps.updateLastKnowLocation();
                    // maps.showLastKnowLocation();

                    if (InputDC == null) {
                        showMessage("You must complete the data for obtain recomended places.");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, ImputDataFragment.newInstance()).commit();
                    }
                        maps.searchNearbyPlaces();

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

            case R.id.action_search_name:
                // Initialize Places.
                Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

// Create a new Places client instance.
                PlacesClient placesClient = Places.createClient(this);


                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                //Place.Field.ADDRESS


                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

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

   // @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public List<Integer> getSelectedRecommendations() {
        return selectedRecommendations;
    }

    public void showDialogRecomendedOption() {

        final String kindRecommendationList[] = getResources().getStringArray(R.array.kind_recommendation_list);
        final boolean[] itemsChecked = new boolean[kindRecommendationList.length];


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //alertDialog.setIcon(R.drawable.dialogopng);
        alertDialog.setTitle(R.string.title_select_kind_recommendation);



        // si la lista es mayor q cero, es pq tiene al menos un elemento
        if (selectedRecommendations.size() > 0) {
            for (int i = 0; i < selectedRecommendations.size(); i++) {
                itemsChecked[selectedRecommendations.get(i) + 1] = true;
            }
        }

        alertDialog.setMultiChoiceItems(kindRecommendationList, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which] = isChecked;
            }
        });

        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Dutch", Toast.LENGTH_SHORT).show();

                boolean atLeastOneSelected = false;

                for (int i = 0; i < itemsChecked.length; i++) {
                    if (itemsChecked[i]) {
                        atLeastOneSelected = true;
                        break;
                    }
                }

                // si no hay ningun elemento seleccionado
                if (!atLeastOneSelected) {
                    showMessage("You must select one recommendation at least");
                }

                // al menos tiene seleccionada una recomendacion
                else {

                    if (selectedRecommendations == null) {
                        selectedRecommendations = new ArrayList<>();
                    } else {
                        selectedRecommendations.clear();
                    }

                    for (int i = 0; i < itemsChecked.length; i++) {
                        if (itemsChecked[i]) {
                            selectedRecommendations.add(i - 1);
                        }
                    }

                    if(mapsActive)
                    maps.searchNearbyPlaces();

                    dialog.cancel();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //showMessage("You must restart de applications");
                dialog.cancel();
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

        mapsActive = true;
    }

    public void showMapaAndSearchNearbyPlaces() {
        showMapa();
        maps.searchNearbyPlaces();
    }

    public void showMessage(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }



    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Best4Now", "Place: " + place.getName() + ", " + place.getId());

                MapsFragment.mainPlace = new com.adoble.best4now.domain.Place(place.getLatLng());
                showMapaAndSearchNearbyPlaces();


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Best4Now", status.getStatusMessage());

                showMessage(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }else // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Best4Now", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount account){

        if(account!=null && !account.getEmail().isEmpty()) {
            this.account = account;
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

            showMessage("" + account.getDisplayName());

            showMapa();
        }
    }

}
