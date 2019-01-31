package com.adoble.best4now.util;

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
            if(type.equals("restaurant")){
                return 1;
            }
            if(type.equals("park") || type.equals("rv_park")  || type.equals("zoo")|| type.equals("amusement_park")){
                return 2;
            }
            if(type.equals("movie_theater") || type.equals("movie_rental")){
                return 3;
            }
            if(type.equals("night_club") || type.equals("casino")){
                return 4;
            }
            if(type.equals("museum") || type.equals("art_gallery") ){
                return 5;
            }
        }

        return -1;
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
