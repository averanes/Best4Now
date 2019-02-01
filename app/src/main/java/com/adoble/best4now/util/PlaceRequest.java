package com.adoble.best4now.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.adoble.best4now.domain.Place;
import com.adoble.best4now.ui.MapsFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlaceRequest extends AsyncTask<String, Integer, JSONArray> {

    public static String PLACES_REQUEST = "";
    public static String nextPageToken="";
    public static int requestCount = 0, REQUEST_LIMIT = 4;

    public Context context;




    public PlaceRequest(Context context){
        this.context = context;

    }
    //ProgressDialog progressBar;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*progressBar = new ProgressDialog(context);
        progressBar.setIndeterminate(true);
        progressBar.show();*/


    }

    @Override
    protected JSONArray doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            String line;
            StringBuilder stringBuilder = new StringBuilder("");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            if (jsonObject.has("next_page_token")) {
                nextPageToken = jsonObject.getString("next_page_token");
            } else {
                nextPageToken = "";
            }
            return jsonObject.getJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }


    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        //if(progressBar!=null)progressBar.cancel(); //setVisibility(View.GONE);
        requestCount++;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
                String name = jsonObject.getString("name");
                LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                String id = jsonObject.getString("id");
                String icon = jsonObject.getString("icon");
                String place_id = jsonObject.getString("place_id");
                String vicinity = jsonObject.getString("vicinity");

                Place p = new Place(latLng, icon, id, name, place_id, vicinity);

                JSONArray types = jsonObject.getJSONArray("types");
                List<String> typesString = new ArrayList<String>(types.length());

                for (int j = 0; j < types.length(); j++) {
                    typesString.add(types.getString(j));
                }
                p.setType(typesString);

                MapsFragment.places.add(p);

            }
            //clusterManager.cluster();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MapsFragment.showPlacesInMap();

        if (requestCount < REQUEST_LIMIT && !nextPageToken.equals("")) {
           // progressBar.setVisibility(View.VISIBLE);.
            final String url = PLACES_REQUEST + "&location=" + MapsFragment.mainPlace.getLocation().latitude + "," + MapsFragment.mainPlace.getLocation().longitude+ "&pagetoken=" + nextPageToken;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new PlaceRequest(PlaceRequest.this.context).execute(url);
                }
            }, 2000);


        }
    }
}