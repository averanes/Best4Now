package com.adoble.best4now.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.adoble.best4now.R;
import com.adoble.best4now.util.TypePlace;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Place {

    private LatLng location;
    private String icon;
    private String id;
    private String name;
    private String place_id;
    private TypePlace type;
    private String vicinity;
    private Bitmap bmpIcon;

    private int recomended;
    private String description;

    private Marker marker;
    private int searchId;


    public Place(LatLng location, String icon, String id, String name, String place_id, String vicinity, int searchId) {
        this.location = location;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.place_id = place_id;
        this.vicinity = vicinity;
        this.description = name;
        this.searchId = searchId;
       // this.type = new TypePlace();
    }

    public Place(LatLng location){
        this.location = location;
    }

    public void loadIconBitmap(){

        if(bmpIcon!= null) return;

        URL url = null;
        try {
            float iconSize=1;
            switch (recomended) {
                case 1:
                    iconSize = 1.2F; break;
                case 2:
                    iconSize = 1.4F; break;
                case 3:
                    iconSize = 1.6F; break;
            }

            url = new URL(icon);
            bmpIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bmpIcon = Bitmap.createScaledBitmap(bmpIcon, (int)(bmpIcon.getWidth()*iconSize), (int)(bmpIcon.getHeight()*iconSize), false);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void removeMarker() {

        try {
            if(this.marker !=null){
                this.marker.remove();
            }
        }catch (Exception e){}

    }


    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public TypePlace getType() {
        return type;
    }

    public void setType(List<String> types) {

        this.type = new TypePlace(types);
    }


    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Bitmap getBmpIcon() {
        return bmpIcon;
    }

    public int getRecomended() {
        return recomended;
    }

    public String getRecomendedDescription(Context context) {

        return getRecomendedDescription(context, recomended);
    }

    public static String getRecomendedDescription(Context context, int recomended) {


        String[] language = context.getResources().getStringArray(R.array.kind_recommendation_list);

        recomended++;
        if(recomended >= 0 && recomended < language.length)
        return language[recomended];

        return context.getResources().getString(R.string.recommendation_not_available);
    }

    public void setRecomended(int recomended) {
        this.recomended = recomended;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }
}
