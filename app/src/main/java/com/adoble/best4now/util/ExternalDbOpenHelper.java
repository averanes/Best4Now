package com.adoble.best4now.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

    //Path to the device folder with databases
    public static String DB_PATH;

    //Database file name
    public static String DB_NAME="recomendations.db3";
    public SQLiteDatabase database;
    public final Context context;

    public final String TABLE_NAME="Recomend";
    public SQLiteDatabase getDb() {
        return database;
    }


    //Extracting elements from the database
    public int [] getPrediction(int sexo, int ageRange, int personCunt, int temperature, int weather, int horario) {
       int [] prediction = new int [13];

        String conditionsColumn = "sexo=? AND  ageRange=? AND personCount=? AND temperature=? AND weather=? AND horario=?";
        String[] conditions = {sexo+"",  ageRange+"", personCunt+"", temperature+"", weather+"", horario+""};

        Cursor friendCursor = database.query(TABLE_NAME, null, conditionsColumn, conditions, null, null, null);
        friendCursor.moveToFirst();
        if(!friendCursor.isAfterLast()) {
            do {
                for (int i = 0; i < prediction.length; i++) {
                    prediction[i]=friendCursor.getInt(i);
                }

            } while (friendCursor.moveToNext());
        }
        friendCursor.close();

        return prediction;
    }






    public ExternalDbOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
//Write a full path to the databases of your application
        String packageName = context.getPackageName();
        DB_PATH = String.format("//data//data//%s//databases//", packageName);
        openDataBase();
    }

    //This piece of code will create a database if it’s not yet created
    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Copying error");
                throw new Error("Error copying database!");
            }
        } else {
            Log.i(this.getClass().toString(), "Database already exists");
        }
    }

    //Performing a database existence check
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
           // Log.e(this.getClass().toString(), "Error while checking db");
        }
//Android doesn’t like resource leaks, everything should
        // be closed
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    //Method for copying the database
    private void copyDataBase() throws IOException {
//Open a stream for reading from our ready-made database
//The stream source is located in the assets
        InputStream externalDbStream = context.getAssets().open(DB_NAME);

//Path to the created empty database on your Android device
        String outFileName = DB_PATH + DB_NAME;

//Now create a stream for writing the database byte by byte
        OutputStream localDbStream = new FileOutputStream(outFileName);

//Copying the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
//Don’t forget to close the streams
        localDbStream.close();
        externalDbStream.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {

        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            db.disableWriteAheadLogging();

            try {
                InputStream externalDbStream = context.getAssets().open(DB_NAME);
                String outFileName = DB_PATH + DB_NAME;

                //xxx.db-shm, xxx.db-wal

                new File(outFileName+"-shm").deleteOnExit();
                new File(outFileName+"-wal").deleteOnExit();

            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}