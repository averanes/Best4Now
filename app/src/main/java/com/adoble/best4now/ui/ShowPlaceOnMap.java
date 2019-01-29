package com.adoble.best4now.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.adoble.best4now.MapsActivity;
import com.adoble.best4now.R;
import com.adoble.best4now.domain.Place;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;

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

        MapsActivity.mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getName())
                //.snippet("Bangalore")
                .icon(BitmapDescriptorFactory.fromBitmap(place.getBmpIcon()) ));

    }

}