package com.adoble.best4now.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adoble.best4now.util.Permissions;
import com.adoble.best4now.util.PlaceRequest;
import com.adoble.best4now.R;
import com.adoble.best4now.domain.Place;
import com.adoble.best4now.util.ShowPlaceOnMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends MapFragment implements OnMapReadyCallback {

    public static GoogleMap mMap;

    private int radius = 500;
    private String language="en";
    private LatLng location;
    private String nameOfLocation ="";


    private static FragmentActivity main;

    public static MapsActivity newInstance(FragmentActivity activity) {
        MapsActivity.main = activity;
        return new MapsActivity();
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_maps, container, false);


        return v;
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

        if (Permissions.checkOrAskPermissions(MapsActivity.main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Permissions.location_permission)) {
            mMap.setMyLocationEnabled(true);
        }

        if(PlaceRequest.PLACES_REQUEST.isEmpty())
            PlaceRequest.PLACES_REQUEST ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + MapsActivity.main.getString(R.string.google_maps_key);


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

                Geocoder gc = new Geocoder(this.getActivity());
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
        new PlaceRequest(this.getActivity()).execute(url);
    }



    public Address getAddressFromLatitudeLongitude(double MyLat, double MyLong)throws IOException{

        Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
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


