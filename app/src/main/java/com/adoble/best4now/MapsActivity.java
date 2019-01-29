package com.adoble.best4now;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.adoble.best4now.domain.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(PlaceRequest.PLACES_REQUEST.isEmpty())
        PlaceRequest.PLACES_REQUEST ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + getString(R.string.google_maps_key);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/



        if(Geocoder.isPresent()){
            try {
                String location = "Italy Calabria Cosenza";
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        Log.d("********LOCATION***** ",a.getLatitude()+" "+a.getLongitude()+" "+a.toString());
                    }
                }

                if(ll.size() > 0){
                    LatLng localization = ll.get(0);
                    mMap.addMarker(new MarkerOptions().position(localization).title(location));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(localization));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(19F));
                   // Log.d("********LOCATION***** ",);
                   // mMap.setCam

                    getLocation(localization);
                }

            } catch (IOException e) {
                // handle the exception
            }
        }


        mMap.getUiSettings().setZoomControlsEnabled(true);




    }


    public static void showPlacesInMap(){

        for(int i = 0; i< PlaceRequest.places.size(); i++){
            Place p = PlaceRequest.places.get(i);

            MarkerOptions mo=new MarkerOptions();
                    mo.position(p.getLocation());
                    mo.title(p.getName());
                    //mo.icon(p.getIcon())

            mMap.addMarker(mo);
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }


    }


    @SuppressLint("MissingPermission")
    public void getLocation(LatLng location){


        String url = PlaceRequest.PLACES_REQUEST + "&radius=500&location=" + location.latitude + "," + location.longitude;
        new PlaceRequest(this).execute(url);
    }



    public Address getAddressFromLatitudeLongitude(double MyLat, double MyLong)throws IOException{

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
        /*String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);*/


        return  (addresses.size() > 0) ? addresses.get(0) : null;
    }




}


