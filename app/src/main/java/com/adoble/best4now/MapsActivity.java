package com.adoble.best4now;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adoble.best4now.domain.Place;
import com.adoble.best4now.ui.Permissions;
import com.adoble.best4now.ui.ShowPlaceOnMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;

    private int radius = 500;
    private String language="en";
    private LatLng location;
    private String nameOfLocation ="";

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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        if (Permissions.checkOrAskPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Permissions.location_permission)) {
            mMap.setMyLocationEnabled(true);
        }


        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


       boolean result = showPlaceAndNearbyPlaces("University of Calabria");



           if(this.location == null){
               this.location = new LatLng(39.362136,16.226346);
           }

           MarkerOptions mo= new MarkerOptions().position(this.location);
           if(this.nameOfLocation.isEmpty())
           mo.title(this.nameOfLocation);

           mMap.addMarker(mo);


           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(this.location,
                   19F), 3000, null);

           /*mMap.moveCamera(CameraUpdateFactory.newLatLng(this.location));
           mMap.moveCamera(CameraUpdateFactory.zoomTo(19F));*/





           searchNearbyPlaces();


        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

//para cuando se de clic en un elemento que se muestre en el centro del mapa
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });

    }

    public boolean showPlaceAndNearbyPlaces(String location){

        if(Geocoder.isPresent()){
            try {

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
                    this.location = ll.get(0);
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
                // handle the exception
            }catch (Exception e) {
                e.printStackTrace();
                // handle the exception
            }
        }

        return false;
    }

    public static void showPlacesNameInMap(){

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



    public static void showPlacesInMap(){

        for(int i = 0; i< PlaceRequest.places.size(); i++){
            Place p = PlaceRequest.places.get(i);
            new ShowPlaceOnMap().execute(p);
        }


    }


    @SuppressLint("MissingPermission")
    public void searchNearbyPlaces(){


        String url = PlaceRequest.PLACES_REQUEST + "&radius="+radius+"&language="+language+"&location=" + location.latitude + "," + location.longitude;
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


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        for (int i = 0; i < permissions.length; i++) {
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                return;
            }
        }

        if(requestCode == Permissions.location_permission){
            mMap.setMyLocationEnabled(true);
        }
    }
}


