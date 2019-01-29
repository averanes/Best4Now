package com.adoble.best4now.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permissions {

    public static final int location_permission = 10;

    public static boolean checkOrAskPermissions(Activity context, String[] permissions, int requesCode){

        List<String> permissionsTemp= new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++) {

            if (ContextCompat.checkSelfPermission(context, permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {

                permissionsTemp.add(permissions[i]);
            }
        }

        if(permissionsTemp.isEmpty())return true;
        else{
            String[] permArray = new String[permissionsTemp.size()];
            for (int i = 0; i < permissionsTemp.size(); permArray[i] =  permissionsTemp.get(i++));

            ActivityCompat.requestPermissions(context,
                    permArray,
                    requesCode);

        }

        return false;
    }
}