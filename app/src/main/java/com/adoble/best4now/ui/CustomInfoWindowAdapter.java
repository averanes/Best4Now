package com.adoble.best4now.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.adoble.best4now.R;
import com.adoble.best4now.domain.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

   Activity activity;
   LatLng latLng;
    public CustomInfoWindowAdapter(Activity activity/**, LatLng latLng*/) {
        this.activity = activity;
        this.latLng = latLng;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = activity.getLayoutInflater().inflate(R.layout.marker_layout, null);

        Place p = (Place)marker.getTag();
        String name=marker.getTitle();

        // Getting the position from the marker
        LatLng latLng = marker.getPosition();
        String recomend=latLng.latitude+" "+latLng.longitude;

        TextView type = (TextView) v.findViewById(R.id.type);
        TextView tvLng = (TextView) v.findViewById(R.id.recomended);
        if(p!=null){
            name = p.getName();
            recomend = p.getRecomendedDescription();

           type.setText(p.getDescription());
           tvLng.setText(recomend);
        }else{
            type.setVisibility(View.GONE);
            tvLng.setVisibility(View.GONE);
        }

        TextView tvLat = (TextView) v.findViewById(R.id.title);
        tvLat.setText(name);




        return v;
    }
}
