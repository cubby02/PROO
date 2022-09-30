package com.cubbysulotions.proo.Chatbot;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cubbysulotions.proo.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DBController extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String LOGCAT = null;

    public static String tableName = "tblWeather";
    public static String colMonth = "Month";
    public static String colRainfall = "Rainfall";
    public static String colSun = "Sun";

    public static String tableName2 = "tblTestQuestion";
    public static String in = "Input";
    public static String out1 = "Output1";
    public static String out2 = "Output2";
    public static String out3 = "Output3";

    public DBController(Context applicationcontext) {

        super(applicationcontext, "mydb.db", null, 1);  // creating DATABASE

        Log.d(LOGCAT, "Created");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_tbl = "CREATE TABLE IF NOT EXISTS tblWeather(ID INTEGER PRIMARY KEY AUTOINCREMENT, Month TEXT, Rainfall TEXT, Sun TEXT)";
        sqLiteDatabase.execSQL(create_tbl);
        Log.e("create Query", create_tbl);


        String create_tbl2 = "CREATE TABLE IF NOT EXISTS tblTestQuestion(ID INTEGER PRIMARY KEY AUTOINCREMENT, Input TEXT, Output1 TEXT, Output2 TEXT, Output3 TEXT)";
        sqLiteDatabase.execSQL(create_tbl2);
        Log.e("create Query", create_tbl2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tblWeather");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tblTestQuestion");
        onCreate(sqLiteDatabase);
    }

    @SuppressLint("Range")
    public String getResponse(String message){
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT Output1 FROM tblTestQuestion WHERE Input like '%"+ message +"%'", null);
        String output = "";

        if(c.moveToFirst()){
            output = c.getString(c.getColumnIndex("Output1"));
        }

        return output;
    }

    public void convertCSV(Context context){
        db = this.getWritableDatabase();

        db.execSQL("delete from " + DBController.tableName2);

        InputStream is = context.getResources().openRawResource(R.raw.test_question);
        BufferedReader buffer = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        ContentValues contentValues = new ContentValues();
        String line = "";
        db.beginTransaction();

        try {
            buffer.readLine();

            try {
                while ((line = buffer.readLine()) != null) {

                    Log.d("line", line);
                    String[] str = line.split(",", 4);


                    String in = str[0].toString();
                    String o1 = str[1].toString();
                    String o2 = str[2].toString();
                    String o3 = str[3].toString();

                    contentValues.put(DBController.in, in);
                    contentValues.put(DBController.out1, o1);
                    contentValues.put(DBController.out2, o2);
                    contentValues.put(DBController.out3, o3);
                    db.insert(DBController.tableName2, null, contentValues);


                    Log.d("Import", "Successfully Updated Database.");
                }

            } catch (Exception e){
                Log.wtf("MyActivity", "Error reading data file" + line, e);
            }

            db.setTransactionSuccessful();

            db.endTransaction();
        } catch (IOException e){
            Log.wtf("MyActivity", "Error reading data file" + line, e);
            e.printStackTrace();
        }
    }

}
