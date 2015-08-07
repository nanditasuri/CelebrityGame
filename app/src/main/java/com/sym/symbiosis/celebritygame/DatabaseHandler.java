package com.sym.symbiosis.celebritygame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLException;

/**
 * Created by symbiosis on 3/1/2015.
 */
public class DatabaseHandler {

    private static String DB_PATH = "/data/data/com.sym.symbiosis.celebritygame/databases/";

    /* Database name and table name*/
    static final String DB_NAME = "CDB";
    static final String DB_CELEB_NAME = "CelebrityGameDB";

    static final int DB_VER = 1;

    static final String CELEBRITIES_TABLE_NAME = "Celebrity";
    static final String SPORTS_TABLE_NAME = "Sports";
    static final String POLITICIANS_TABLE_NAME = "Politicians";
    static final String NAMES_TABLE_NAME = "Names";



    /* key names of the table Celebrity*/
    static final String CELEBRITY_ID = "_ID";
    static final String CELEBRITY_NAME = "_NAME";
    static final String CELEBRITY_GENDER = "_GENDER";
    static final String CELEBRITY_ALIVE = "_ALIVE";
    static final String CELEBRITY_CONTINENT = "_CONTINENT";
    static final String CELEBRITY_COUNTRY = "_COUNTRY";
    static final String CELEBRITY_PROFESSION = "_PROFESSION";


    /* queries on celebrity table*/
    static final String GET_CELEBRITY_COUNT_QUERY = "select * from Celebrity";
    static final String GET_CELEBRITY_QUERY = "select * from Celebrity where _ID=";
    static final String GET_CELEBRITY_COUNTRIES_QUERY = "select distinct _COUNTRY,1 _id from Celebrity UNION select \"------ Select Country ------\" as _COUNTRY,9999 as _id ORDER BY _country";
    static final String GET_CELEBRITY_CONTINENTS_QUERY = "select distinct _CONTINENT,1 _id from Celebrity UNION select \"------ Select Continent ------\" as _CONTINENT,9999 as _id ORDER BY _CONTINENT";
    static final String GET_CELEBRITY_PROFESSION_QUERY = "select distinct _PROFESSION,1 _id from Celebrity UNION select \"------ Select Profession ------\" as _PROFESSION,9999 as _id ORDER BY _PROFESSION";
    static final String GET_CELEBRITY_NAMES_QUERY = "select _NAME from Celebrity";
    static final String GET_CELEBRITY_COUNTRIES_SELECTED_QUERY = "select distinct _COUNTRY,1 _id from Celebrity where _CONTINENT=";
    static final String UNION_QUERY = " UNION select \"------ Select Country ------\" as _COUNTRY,9999 as _id ORDER BY _country";
    static final String GET_CELEBRITY_POSSIBLE_NAMES= "select * from Names where _NAME=";

    /* queries on sports table*/
    static final String GET_CELEBRITY_SPORT_DETAILS = "select * from Sports where _NAME=";
    /*queries on politicans table*/
    static final String GET_CELEBRITY_POLITICIAN_DETAILS = "select * from Politicians where _NAME=";


    /* creating objects of helper class , db class and a context*/
    final Context context;
    CelebritySqliteHelper helper;
    SQLiteDatabase db;

    /* constructor for initializing the context and creating the helper object with the context*/
    public DatabaseHandler(Context c){

        this.context = c;
        if(context==null){
            Log.i("ELA","CONTXT IS NULL");
        }
        helper = new CelebritySqliteHelper(context);

    }
    /* Helper private class to have methods onCreate and onUpgrade*/
    private class CelebritySqliteHelper extends SQLiteAssetHelper {


        public CelebritySqliteHelper(Context context) {

            super(context, DB_CELEB_NAME, context.getExternalFilesDir(null).getAbsolutePath(),null, DB_VER);
            Log.i("Path is------------>",context.getExternalFilesDir(null).getAbsolutePath());
        }

    }/*close of inner class*/

    /* to open the database*/
public DatabaseHandler open() throws SQLException {

    db=helper.getReadableDatabase();

    return this;
}

    /* to close the database*/
    public void close()
    {
        db.close();
    }

    public int getCountOfCelebrities(){

        Cursor c = null;
        if(db!=null)
            c = db.rawQuery(GET_CELEBRITY_COUNT_QUERY,null);
        else
            Log.i("DBHANDLER","Db is null");

        if (c!=null) {
            Log.i("DBHANDLER","Count is ---------> "+c.getCount());
            return c.getCount();
        }
        else
            Log.i("DBHandler", "getCountOfCelebrities-> Cursor is NULL");

        return 0;
    }

    public Cursor getCelebrity(long id){

        Cursor c = db.rawQuery(GET_CELEBRITY_QUERY+id,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getCelebrity-> Cursor is NULL");


        return null;
    }


    public Cursor getCountriesSelected(String selectedContinent){
        //select _COUNTRY from Celebrity where _CONTINENT=\"North America\";
        Cursor c = db.rawQuery(GET_CELEBRITY_COUNTRIES_SELECTED_QUERY+"\""+selectedContinent+"\""+UNION_QUERY,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getCountriesSelected-> Cursor is NULL");

        return null;
    }

    public Cursor getCountries(){
        Cursor c = db.rawQuery(GET_CELEBRITY_COUNTRIES_QUERY,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getCountries-> Cursor is NULL");

        return null;
    }

    public Cursor getContinents(){
        Cursor c = db.rawQuery(GET_CELEBRITY_CONTINENTS_QUERY,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getContinents-> Cursor is NULL");

        return null;
    }

    public Cursor getProfessions(){
        Cursor c = db.rawQuery(GET_CELEBRITY_PROFESSION_QUERY,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getProfessions-> Cursor is NULL");

        return null;
    }

    public Cursor getCelebrityNames(){

        Cursor c = db.rawQuery(GET_CELEBRITY_NAMES_QUERY,null);
        if (c!=null) {
            return c;
        }
        else
            Log.i("DBHandler", "getCelebrityNames-> Cursor is NULL");

        return  null;
    }

    public Cursor getSportDetails(String name){

        Log.i("DH","The name of the sport person is "+name);
        Log.i("DH","The query is "+ GET_CELEBRITY_SPORT_DETAILS+name);
        Cursor c = db.rawQuery(GET_CELEBRITY_SPORT_DETAILS+"\""+name+"\"",null);
        if (c!=null) {
            Log.i("DH","C is not null");
            return c;

        }
        else
            Log.i("DBHandler", "getCelebrityNames-> Cursor is NULL");

        return null;
    }

    public Cursor getPoliticianDetails(String name){

        Log.i("DH","The name of the politician person is "+name);
        Log.i("DH","The query is "+ GET_CELEBRITY_POLITICIAN_DETAILS+name);
        Cursor c = db.rawQuery(GET_CELEBRITY_POLITICIAN_DETAILS+"\""+name+"\"",null);
        if (c!=null) {
            Log.i("DH","C is not null");
            return c;

        }
        else
            Log.i("DBHandler", "getCelebrityNames-> Cursor is NULL");

        return null;
    }
    public Cursor getPossibleNames(String name){

        Cursor c = db.rawQuery(GET_CELEBRITY_POSSIBLE_NAMES+"\""+name+"\"",null);
        if (c!=null) {
            Log.i("DH","C is not null");
            return c;

        }
        else
            Log.i("DBHandler", "getCelebrityNames-> Cursor is NULL");

        return null;
    }
}
