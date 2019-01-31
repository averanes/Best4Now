package com.adoble.best4now.util;

import android.os.AsyncTask;

import com.adoble.best4now.domain.InputDataCriteria;
import com.adoble.best4now.domain.Place;
import com.adoble.best4now.domain.Weather;
import com.adoble.best4now.ui.MainActivity;
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
        Place place = places[0];

        place.setRecomended(-1);
        //busco la mayor recomendacion para este lugar analizando la recomendacion por tipos, si tiene un solo tipo la recomendacion para este
        for (int i = 0; i < place.getType().getTypesGroup().size(); i++) {

            int typeT=place.getType().getTypesGroup().get(i);
            if(typeT == -1) continue; //no esta en los grupos q manejamos

            int recomendedTemp = MainActivity.predictionCalculated[ typeT + 7]; //7 es para descartar las 7 columnas primeras para agrupar

            if(recomendedTemp > place.getRecomended()){
                place.setRecomended(recomendedTemp); ;
            }
        }

        //String descrip=place.getName()+"\n Recomendation:"+place.getRecomended()+"\n[";
        String descrip="[";

        for (String typeT: place.getType().getTypes() ) {
            descrip+=" "+typeT;
        }
        descrip+=" ] ";

        place.setDescription(descrip);



       /* System.out.println("********************************");
        String resul="";
        for (int value:prediction) {
           resul+=value+" ";

        }*/




        if(place.getBmpIcon() == null){
            place.loadIconBitmap();
        }

            return place;
    }



    @Override
    protected void onPostExecute(Place place) {

        super.onPostExecute(place);

        //if(place.getRecomended()<1)return;

        Marker marker = MapsFragment.mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getDescription())
                //.snippet("Bangalore")
                .icon(BitmapDescriptorFactory.fromBitmap(place.getBmpIcon()) ));

        marker.setTag(place);
        place.setMarker(marker);

    }

}