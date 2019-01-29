package com.adoble.best4now.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

import com.google.android.gms.maps.model.LatLng;

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
    private List<String> types;
    private String vicinity;
    private Bitmap bmpIcon;


    public Place(LatLng location, String icon, String id, String name, String place_id, String vicinity) {
        this.location = location;
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.place_id = place_id;
        this.vicinity = vicinity;
        this.types = new ArrayList<String>();
    }


    public void loadIconBitmap(){

        if(bmpIcon!= null) return;

        URL url = null;
        try {
            url = new URL(icon);
            bmpIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bmpIcon = Bitmap.createScaledBitmap(bmpIcon, (int)(bmpIcon.getWidth()*1.5F), (int)(bmpIcon.getHeight()*1.5F), false);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public List<String> getTypes() {
        return types;
    }

    public void setType(String type) {
        this.types.add(type);
    }

    public void setTypes(List<String> types) {
        this.types = types;
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
}
