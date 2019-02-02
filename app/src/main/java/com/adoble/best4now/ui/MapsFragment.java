package com.adoble.best4now.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adoble.best4now.util.Permissions;
import com.adoble.best4now.util.PlaceRequest;
import com.adoble.best4now.R;
import com.adoble.best4now.domain.Place;
import com.adoble.best4now.util.ShowPlaceOnMap;
import com.adoble.best4now.util.WeatherRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends MapFragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener,  GoogleMap.OnInfoWindowLongClickListener{

    public static GoogleMap mMap;
    private static FragmentActivity main;
    private int radius = 5000;
    private String language = "en";
    //private LatLng location;
    //private String nameOfLocation = "";

    public static Place mainPlace;


    //private Marker myMarker;

    public static List<Place> places; //lugares q estan mostrados en el mapa actualmente, proporcionados por google

    private float zoom = 15F;

    public static MapsFragment newInstance(FragmentActivity activity) {
        MapsFragment.main = activity;
        return new MapsFragment();
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

        if (Permissions.checkOrAskPermissions(MapsFragment.main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Permissions.location_permission)) {
            mMap.setMyLocationEnabled(true);
        }


        if (PlaceRequest.PLACES_REQUEST.isEmpty())
            PlaceRequest.PLACES_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + MapsFragment.main.getString(R.string.google_maps_key)+ "&radius=" + radius + "&language=" + language;


        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


        //boolean result = showPlaceAndNearbyPlaces("University of Calabria");

        //updateLastKnowLocation();
        Place p = mainPlace;
        if(p == null)
               p = new Place(new LatLng(39.362136, 16.226346)); //University of Calabria

        showPositionInMapAndSearchWeather(p);

        //searchNearbyPlaces();


        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

//para cuando se de clic en un elemento que se muestre en el centro del mapa
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                if (marker.equals(mainPlace.getMarker()))
                {
                    //handle click here
                // MainActivity.mainActivity.showMessage("CLIC REALIZAR ACCION");

                }


                return true;
            }
        });



        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                showPositionInMapAndSearchWeather(new Place(latLng));
            }
        });

        //showWeatherOverMap();

        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.mainActivity));


        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
    }

    public void showPositionInMapAndSearchWeather(Place newPlace){
        showPositionInMap(newPlace);

        new WeatherRequest().execute(mainPlace.getLocation());

    }

    public void showPositionInMap(Place newPlace){
        if( mainPlace !=null )
            mainPlace.removeMarker();

        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(newPlace.getLocation());

        if(newPlace.getName() !=null && !newPlace.getName().isEmpty()){
            markerOptions.title(newPlace.getName());
        }else{
            markerOptions.title(MainActivity.mainActivity.getString(R.string.center_search_nearby_place));
        }


        Marker m = mMap.addMarker(markerOptions);
        m.showInfoWindow();
        // Placing a marker on the touched position
        newPlace.setMarker(m);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPlace.getLocation(),
                MapsFragment.this.zoom), 3000, null);

        mainPlace = newPlace;

    }

    /**
     This method is not used*/
    public LatLng getPlaceAndNearbyPlacesByAddress(String location) {

        if (Geocoder.isPresent()) {
            try {

                Geocoder gc = new Geocoder(MapsFragment.main);
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        Log.d("********LOCATION***** ", a.getLatitude() + " " + a.getLongitude() + " " + a.toString());
                    }
                }

                if (ll.size() > 0) {
                    return ll.get(0);

                }

            } catch (IOException e) {
                e.printStackTrace();
                // handle the exception
            } catch (Exception e) {
                e.printStackTrace();
                // handle the exception
            }
        }

        return null;
    }

    public static int searchNumber=0;
    @SuppressLint("MissingPermission")
    public void searchNearbyPlaces() {

        searchNumber++;
        String url = PlaceRequest.PLACES_REQUEST  + "&location=" + mainPlace.getLocation().latitude + "," + mainPlace.getLocation().longitude;

        removeAlMarkerPlace();

        showPositionInMap(mainPlace);

        MapsFragment.places = new ArrayList<Place>();

        new PlaceRequest(MapsFragment.main, searchNumber).execute(url);
    }


    public static void showPlacesInMap() {

        for (int i = 0; i < MapsFragment.places.size(); i++) {
            Place p = MapsFragment.places.get(i);
            new ShowPlaceOnMap().execute(p);
        }

    }

    public void removeAlMarkerPlace(){
        if(mMap!=null){
            try {
                mMap.clear();
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        /*if(MapsFragment.places!=null){

            for (int i = 0; i < MapsFragment.places.size(); i++) {
                MapsFragment.places.get(i).removeMarker();
            }
        }*/
    }


    public Address getAddressFromLatitudeLongitude(double MyLat, double MyLong) throws IOException {

        Geocoder geocoder = new Geocoder(MapsFragment.main, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(MyLat, MyLong, 1);
        /*String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);*/


        return (addresses.size() > 0) ? addresses.get(0) : null;
    }




    @SuppressLint("MissingPermission")
    public void showLastKnowLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) this.main.getSystemService(LOCATION_SERVICE);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if(Permissions.checkOrAskPermissions(MapsFragment.main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION},Permissions.NETWORK_PROVIDER_PERMISSION)){

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location!=null){
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                showPositionInMapAndSearchWeather(new Place(latLng));
            }
        }

    }


    @SuppressLint("MissingPermission")
    public void updateLastKnowLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) this.main.getSystemService(LOCATION_SERVICE);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if(Permissions.checkOrAskPermissions(MapsFragment.main, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION},Permissions.NETWORK_PROVIDER_PERMISSION)){

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location!=null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            showPositionInMapAndSearchWeather(new Place(latLng));
        }
     }

    // Creating a criteria object to retrieve provider
    Criteria criteria = new Criteria();

    // Getting the name of the best provider
    String provider = locationManager.getBestProvider(criteria, true);

    locationManager.requestLocationUpdates(provider, 20000, 0, this);
   }


   public void showWeatherOverMap(){

      /* BitmapDescriptor image = BitmapDescriptorFactory.fromResource(WeatherRequest.getIconByWeatherHorary(MainActivity.mainActivity, 3, 2));
       GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
               .image(image)
               .position(mainPlace.getLocation(), 500f, 500f)
               .transparency(0.5f);
       mMap.addGroundOverlay(groundOverlay);*/
   }


@Override
public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(MapsFragment.this.zoom));

        }




    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) {}


    @Override
    public void onInfoWindowClick(Marker marker) {

        Double latitude = marker.getPosition().latitude;
        Double longitude = marker.getPosition().longitude;



        String uri = "geo:" + latitude + ","
                +longitude + "?q=" + latitude
                + "," + longitude;


        if(marker.getTag() != null){
            Place p = (Place) marker.getTag();

           /* String temp = "";
            for (String val:p.getType().getTypes()) {
                temp+="+"+val;
            }
            temp = temp.substring(1);*/



            uri = "https://www.google.com/maps/search/?api=1&query="+Uri.encode(p.getName())+"&query_place_id="+p.getId();

            Uri gmmIntentUri = Uri.parse(uri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(MainActivity.mainActivity.getPackageManager()) != null) {
                MainActivity.mainActivity.startActivity(mapIntent);
            }

        }



        MainActivity.mainActivity.startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Double latitude = marker.getPosition().latitude;
        Double longitude = marker.getPosition().longitude;

        //String uri = "http://maps.google.com/maps?saddr=" +latitude+","+longitude;
        String uri = "http://maps.google.com/maps?q=" +latitude+","+longitude;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = marker.getTitle();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);

        MainActivity.mainActivity.startActivity(Intent.createChooser(sharingIntent, "Share via"/*this.getResources().getString(R.string.share_via)*/));
    }



}


