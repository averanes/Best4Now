package com.adoble.best4now.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adoble.best4now.domain.Weather;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends MapFragment implements OnMapReadyCallback, LocationListener{

    public static GoogleMap mMap;
    private static FragmentActivity main;
    private int radius = 500;
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
            PlaceRequest.PLACES_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + MapsFragment.main.getString(R.string.google_maps_key);


        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


        //boolean result = showPlaceAndNearbyPlaces("University of Calabria");

        //updateLastKnowLocation();
        Place p = new Place(new LatLng(39.362136, 16.226346)); //University of Calabria

        showPositionInMap(p);

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

                if (marker.equals(mainPlace.getMarker()))
                {
                    //handle click here

                    Toast.makeText(MapsFragment.main, "CLIC REALIZAR ACCION", Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });



        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                showPositionInMap(new Place(latLng));
            }
        });

        //showWeatherOverMap();


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
            markerOptions.title(newPlace.getLocation().latitude + " : " + newPlace.getLocation().longitude);
        }

        // Placing a marker on the touched position
        newPlace.setMarker(mMap.addMarker(markerOptions));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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

    @SuppressLint("MissingPermission")
    public void searchNearbyPlaces() {

        String url = PlaceRequest.PLACES_REQUEST + "&radius=" + radius + "&language=" + language + "&location=" + mainPlace.getLocation().latitude + "," + mainPlace.getLocation().longitude;

            removeAlMarkerPlace();

        MapsFragment.places = new ArrayList<Place>();

        new PlaceRequest(MapsFragment.main).execute(url);
    }


    public static void showPlacesInMap() {

        for (int i = 0; i < MapsFragment.places.size(); i++) {
            Place p = MapsFragment.places.get(i);
            new ShowPlaceOnMap().execute(p);
        }

    }

    public void removeAlMarkerPlace(){

        if(MapsFragment.places!=null){

            for (int i = 0; i < MapsFragment.places.size(); i++) {
                MapsFragment.places.get(i).removeMarker();
            }
        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }

        if (requestCode == Permissions.location_permission) {
            mMap.setMyLocationEnabled(true);
        }
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

                showPositionInMap(new Place(latLng));
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

            showPositionInMap(new Place(latLng));
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
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}


