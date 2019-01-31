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

        ExternalDbOpenHelper dbOpenHelper = MainActivity.mainActivity.getConection();
        dbOpenHelper.openDataBase();

        Weather weather = MainActivity.mainActivity.getWeather();
        InputDataCriteria inputC = MainActivity.mainActivity.getInputDataCriteria();

        int[] prediction =dbOpenHelper.getPrediction(inputC.getSex(), inputC.getAge(), inputC.getPersons(), weather.getTemperatureConsideration(), weather.getWeatherConsideration(), weather.getHorarioConsideration());


        place.setRecomended(-1);
        for (int i = 0; i < place.getType().getTypesGroup().size(); i++) {

            int recomendedTemp = prediction[place.getType().getTypesGroup().get(i) + 7]; //7 es para descartar las 7 columnas primeras para agrupar

            if(recomendedTemp > place.getRecomended()){
                place.setRecomended(recomendedTemp); ;
            }
        }

        //String descrip=place.getName()+"\n Recomendation:"+place.getRecomended()+"\n[";
        String descrip="R: "+place.getRecomended()+"\n[";

        for (String typeT: place.getType().getTypes() ) {
            descrip+=" "+typeT;
        }
        descrip+=" ]";

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

        Marker marker = MapsFragment.mMap.addMarker(new MarkerOptions()
                .position(place.getLocation())
                .title(place.getDescription())
                //.snippet("Bangalore")
                .icon(BitmapDescriptorFactory.fromBitmap(place.getBmpIcon()) ));

        place.setMarker(marker);

    }

}