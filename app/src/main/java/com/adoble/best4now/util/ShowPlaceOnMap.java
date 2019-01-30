package com.adoble.best4now.util;

import android.os.AsyncTask;

import com.adoble.best4now.domain.Place;
import com.adoble.best4now.ui.MapsFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowPlaceOnMap extends AsyncTask<Place, Integer, Place>
{


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

    }
    @Override
    protected Place doInBackground(Place... places) {
            if(places[0].getBmpIcon() == null){
                places[0].loadIconBitmap();
            }

            return places[0];
    }



    @Override
    protected void onPostExecute(Place place) {

        super.onPostExecute(place);

        Marker marker = MapsFragment.mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getName())
                //.snippet("Bangalore")
                .icon(BitmapDescriptorFactory.fromBitmap(place.getBmpIcon()) ));

        place.setMarker(marker);

    }

}