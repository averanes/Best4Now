package com.adoble.best4now.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.adoble.best4now.ui.MainActivity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permissions {

    public static final int location_permission = 10;
    public static final int NETWORK_PROVIDER_PERMISSION = 11;
    //public static final int ACCESS_NETWORK_STATE = 12;

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


    public static boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static void showMessageErrorConexion() {
        MainActivity.mainActivity.showMessage("Problemas con la concexion, chequeela.");
    }




}
