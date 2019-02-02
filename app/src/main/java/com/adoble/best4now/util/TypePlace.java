package com.adoble.best4now.util;

import com.adoble.best4now.R;
import com.adoble.best4now.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TypePlace {

    private List<String> types;
    private List<Integer> typesGroup;

    public TypePlace(List<String> types) {
        this.types = types;

        typesGroup= new ArrayList<>(6);

        for (String type : types) {
            typesGroup.add(getTypeGroup(type));
        }
    }

    public int getTypeGroup(String type){

        if(!type.isEmpty()){
            if(type.equals("bar")  || type.equals("cafe") || type.equals("liquor_store")){
                return 0;
            }
            if(type.equals("restaurant") || type.equals("food")){
                return 1;
            }
            if(type.equals("park") || type.equals("rv_park")  || type.equals("zoo") || type.equals("amusement_park")  || type.equals("spa")){
                return 2;
            }
            if(type.equals("movie_theater") || type.equals("movie_rental")){
                return 3;
            }
            if(type.equals("night_club") || type.equals("casino") || type.equals("lodging")){
                return 4;
            }
            if(type.equals("museum") || type.equals("art_gallery") ){
                return 5;
            }
        }

        return -1;
    }

    public static String getTypeDescriptionGoogle(int type){

        switch (type){
            case 0: return "bar, cafe, liquor_store";
            case 1: return "restaurant food";
            case 2: return "park, rv_park, zoo, amusement_park, spa";
            case 3: return "movie_theater, movie_rental";
            case 4: return "night_club, casino lodging";
        }
        return "museum, art_gallery";

    }

    public static String getTypeDescription(int type){

        switch (type){
            case 0: return MainActivity.mainActivity.getResources().getString(R.string.bar_coffee_liquor_store);
            case 1: return MainActivity.mainActivity.getResources().getString(R.string.restaurant_food);
            case 2: return MainActivity.mainActivity.getResources().getString(R.string.park_rv_park_zoo_amusement_park);
            case 3: return MainActivity.mainActivity.getResources().getString(R.string.movie_theater_movie_rental);
            case 4: return MainActivity.mainActivity.getResources().getString(R.string.night_club_casino);
        }
        return MainActivity.mainActivity.getResources().getString(R.string.museum_art_gallery);

    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Integer> getTypesGroup() {
        return typesGroup;
    }

    public void setTypesGroup(List<Integer> typesGroup) {
        this.typesGroup = typesGroup;
    }
}
