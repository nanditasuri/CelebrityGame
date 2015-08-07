package com.sym.symbiosis.celebritygame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sym.symbiosis.celebritygame.R;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/*
Once the user enters the play screen
1-- get count of personalities in database
2-- pick a random number/personality from the count
3-- get the personality details
*/

public class PlayScreenActivity extends Activity implements ExpandableListView.OnGroupClickListener,MediaPlayer.OnCompletionListener{

    DatabaseHandler dbHandler;
    int celebCount;
    static Celebrity curCeleb; //save
    static SportsCelebrity sportCeleb;
    static PoliticianCelebrity poltCeleb;
    static int quesCount=10;//save
    static ExpandableListView exQuestLV;//save
    Resources res;
    static ExpandableListAdapter expQuesListAdapter;
    ArrayList<String> arrCategories;
    static HashMap<String,List<String>> mapSubCat;
    static EditText quesCounter;
    ListView ansLV;
    static ArrayList<String> listAns;
    static ArrayAdapter<String> ansAdapter;
    static Context ctx;
    static HashMap<String,Integer> disGroup;
    static HashMap<String,ArrayList <String>> celebNameMatch;
    static Button imageGuess;
    static TextView ansTextView;
    static String ansList;
    MainScreenActivity msa;
    MediaPlayer player;
    ShareDialog mShareDialog;
    ShareButton mShareButton;
    ImageDialog imgd;
    CelebrityGuessDialog cgd;

    int celebId;
    int c=0;
    Intent musicIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("***********PSA*********","----------ON CREATE----------");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play_screen);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.LTGRAY);

        ctx = this;

        mShareDialog = new ShareDialog(this);
        mShareButton = (ShareButton)findViewById(R.id.fb_share_button);

        populateData();

        quesCounter = (EditText) findViewById(R.id.et_score);
        quesCounter.setText("10");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        quesCounter.setKeyListener(null);
        quesCounter.setFocusable(false);

        imageGuess = (Button)findViewById(R.id.buttonImage);
        imageGuess.setEnabled(false);
        imageGuess.setVisibility(View.INVISIBLE);

        ansTextView = (TextView)findViewById(R.id.ansTextView);
        ansList="";

        celebNameMatch = new HashMap<String,ArrayList<String>>();
        disGroup = new HashMap<String,Integer>();
        dbHandler = new DatabaseHandler(ctx);
        generateCelebrityId();

       // savedGamesArr = new int[celebCount];


    }
    static public void updateAnsList(){
        ansTextView.setText(ansList);
    }
    private  void generateCelebrityId(){
        try {
            dbHandler.open();
            celebCount = 0;
            celebCount = dbHandler.getCountOfCelebrities();
            Log.i("PlayScreenActivity", "---->Celeb count is " + celebCount);

            Random rNum = new Random();
            celebId = 0;
            quesCount = 10;
            while(celebId==0) {

                celebId = rNum.nextInt(celebCount);
            }
            if(msa.savedGamesArr.length>0){
                while(msa.savedGamesArr[celebId-1]==1) {
                    Log.i("PSA","*****You already played celebId = "+celebId);
                    celebId = rNum.nextInt(celebCount);
                }

            }
            boolean idPresent = collectData(celebId);
            Log.i("PlayScreenActivity", "---->Celeb ID is " + celebId);
            dbHandler.close();

        }catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("*************PSA*************","----------ON RESUME --------");

       // msa.mPlayer.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
      ///  msa.playscreenactivity=false;
        Log.i("*************PSA*************","----------ON STOP --------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cgd.celebrityguessdialog==false && imgd.imageguessdialog==false) {
            stopService(new Intent(this, MusicService.class));
            //msa.isPlaying=false;
        }
        Log.i("*************PSA*************","----------ON PAUSE --------");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("*************PSA*************","----------ON SAVE INSTANCE --------");
        // answers summary //celeb id // quescounter
        // curceleb
        outState.putString("ANSWERS_SUMMARY",ansTextView.getText().toString());
        outState.putInt("CELEB_ID",celebId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(this,MusicService.class);
       /* startService(i);
        msa.isPlaying=true;
        msa.playscreenactivity=true;*/
        Log.i("*************PSA*************","----------ON RESTART --------");
    }

    @Override
    protected void onStart() {
        super.onStart();
       // msa.playscreenactivity=true;
        Log.i("*************PSA*************","----------ON START --------");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("*************PSA*************","----------ON RESTORE INSTANCE --------");
        ansTextView.setText(savedInstanceState.getString("ansTextView"));
    }

    private void populateData(){
        /*populate categories */
        res= this.getResources();

        arrCategories = new ArrayList<String>();
        mapSubCat = new HashMap<String,List<String>>();
        String [] categories;
        categories = res.getStringArray(R.array.categories);

        for(int i=0;i<categories.length;i++){

            ArrayList<String> arrSubCat = new ArrayList<String>();
            arrCategories.add(categories[i]);

            if(categories[i].equals("Gender")){
                 arrSubCat.clear();
                 arrSubCat.add("Choose the Gender..");
                 mapSubCat.put("Gender",arrSubCat);

            }//GENDER
            else if(categories[i].equals("Alive")){
                arrSubCat.clear();
                arrSubCat.add("Choose the Status..");
                mapSubCat.put("Alive",arrSubCat);
            }//ALIVE
            else if(categories[i].equals("Continent")){
                arrSubCat.clear();
                arrSubCat.add("Choose the Continent..");
                mapSubCat.put("Continent",arrSubCat);
            }//CONTINENT
            else if(categories[i].equals("Country")){
                arrSubCat.clear();
                arrSubCat.add("Choose the Country..");
                mapSubCat.put("Country",arrSubCat);
            }//COUNTRY
            else if(categories[i].equals("Profession")){
                arrSubCat.clear();
                arrSubCat.add("Choose the Profession..");
                mapSubCat.put("Profession",arrSubCat);
            }//PROFESSION
        }
        Log.i("After", String.valueOf(mapSubCat.get("Gender")));
        Log.i("After", String.valueOf(mapSubCat.get("Alive")));
        Log.i("After", String.valueOf(mapSubCat.get("Continent")));
        Log.i("After", String.valueOf(mapSubCat.get("Country")));

        exQuestLV = (ExpandableListView)findViewById(R.id.expandableListView);
        expQuesListAdapter = new ExpandableListAdapter(this,arrCategories,mapSubCat);
        Log.i("PSA","after adapter created");

        exQuestLV.setAdapter(expQuesListAdapter);
        exQuestLV.setOnGroupClickListener(this);

    }
    private boolean collectData(int celebId){
        Cursor celebData = dbHandler.getCelebrity(celebId);
        if(celebData.getCount()>0){
            celebData.moveToFirst();
            if(celebData.getString(6).equals("Sports")) {
                Log.i("PSA","SportCelebrity reference is created");
                curCeleb = new SportsCelebrity();

            }else if(celebData.getString(6).equals("Politician")) {
                Log.i("PSA","Politician reference is created");
                curCeleb = new PoliticianCelebrity();

            }
            else {
                curCeleb = new Celebrity();
                Log.i("PSA","Celebrity reference is created");
            }

            curCeleb.setCelebName(celebData.getString(1));
            curCeleb.setCelebGender(celebData.getString(2));
            curCeleb.setCelebAlive(celebData.getInt(3));
            curCeleb.setCelebContinent(celebData.getString(4));
            curCeleb.setCelebCountry(celebData.getString(5));
            curCeleb.setCelebProfession(celebData.getString(6));
            curCeleb.setCelebPhoto(celebData.getString(7));
            curCeleb.setCelebLink(celebData.getString(8));
           /* adding the celeb name to hashmap*/
            ArrayList<String> possNames = new ArrayList<String>();

            Cursor celebNames = dbHandler.getPossibleNames(curCeleb.getCelebName());
            if(celebNames.getCount()>0){
                celebNames.moveToFirst();

                for(int i=0;i<celebNames.getCount();i++){
                      possNames.add(celebNames.getString(1));
                      celebNames.moveToNext();
                }
            }
            Log.i("DATA","Setting the celeb in hashmap "+curCeleb.getCelebName());
            celebNameMatch.put(curCeleb.getCelebName(),possNames);
            return true;
        }
         return false;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("PSA","onConfigurationChanged="+newConfig.toString());
    }
    public static void updateQuesCounter(String celebName){
        Log.i("updateQuesCounter","in the funfction.. value is "+quesCount );
        quesCounter.setText(String.valueOf(quesCount));
        if(quesCount==1){
            Toast.makeText(ctx,"You can guess the personality with this photo",Toast.LENGTH_LONG).show();
            imageGuess.setEnabled(true);
            imageGuess.setVisibility(View.VISIBLE);
        }
        /* Once the questions count reach max
        the user is indicated .
         */
        if(quesCount==0){
            Toast.makeText(ctx,"Oops... you have reached the questions limit !!",Toast.LENGTH_LONG).show();

            /*todo-*/
                /*
                   -- reset the counter, celebId,timer
                 */
            quesCount = 10;
            imageGuess.setVisibility(View.INVISIBLE);
            updateQuesCounter(curCeleb.getCelebName());
            disGroup.clear();
            ((PlayScreenActivity)ctx).generateCelebrityId();
            ((PlayScreenActivity)ctx).populateData();
            ansList="";
            updateAnsList();



        }
    }
    public void onImageGuess(View v){

        Intent i = new Intent(this,ImageDialog.class);
        i.putExtra("PHOTO_NAME",curCeleb.getCelebPhoto());

        i.putExtra("LINK",false);
        startActivity(i);
    }
    public static void notifyAnswer(String answer){

        Log.i("notifyAnswer","In the function");
       /*
        listAns.add(answer);
        ansAdapter.notifyDataSetChanged();*/
        ansList = ansList+" --> "+answer;
        updateAnsList();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.action_music) {
            if(msa.isPlaying==true) {
                stopService(new Intent(this, MusicService.class));
                msa.isPlaying=false;
            }else{
                msa.isPlaying=true;
                Intent i = new Intent(this,MusicService.class);
                startService(i);
            }
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    public static void updateQuestions(){
        expQuesListAdapter.notifyDataSetChanged();
        expQuesListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        Log.i("ONGROUPCLICK","the group pos is "+groupPosition);


        if(disGroup.size()>0) {
            Log.i("the sizw of the is ",String.valueOf(disGroup.size()));

            if (groupPosition==0){
                if(disGroup.containsKey("Gender")) {

                    return true;
                }
            }
            else if((groupPosition==1)){
                if(disGroup.containsKey("Alive"))
                    return true;
            }
            else if((groupPosition==2)){
                if(disGroup.containsKey("Country"))
                    return true;
            }
            else if((groupPosition==3)){
                if(disGroup.containsKey("Continent"))
                    return true;
            }
            else if((groupPosition==4)){
                if(disGroup.containsKey("Profession"))
                    return true;
            }
            else if((groupPosition==5)){
                if((disGroup.containsKey("Party Name")) ||(disGroup.containsKey("Sport Name")) )
                    return true;
            }else if((groupPosition==6)){
                if((disGroup.containsKey("Sport Type")) ||(disGroup.containsKey("Served As")) )
                    return true;
            }else if((groupPosition==7)){
                if((disGroup.containsKey("Active")))
                    return true;
            }else if((groupPosition==8)){
                if((disGroup.containsKey("Born Year")))
                    return true;

            }else if((groupPosition==9)){
                if(disGroup.containsKey("Occupation"))
                    return true;
            }
            /* collapse the additional groups*/
        }

        return false;
    }

    public void onGuessCelebrity(View view){
        Log.i("PSA","OnGuessCelebrity -- clicked");
        Log.i("PSA","OnGuessCelebrity -- celebName is "+curCeleb.getCelebName());

        Intent i = new Intent(this,CelebrityGuessDialog.class);
        startActivityForResult(i,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == 2) && (resultCode == RESULT_OK)){
            Bundle bundle = data.getExtras();
            String celebGuessName = bundle.getString("GUESSED_CELEB_NAME");
            Log.i("PSA","onActivityResult -- Guessed celeb name is "+celebGuessName);
            if(validateName(celebGuessName)) {
                /*todo-*/
                /* --increase the points counter
                   --add the celeb ID to profile id
                   -- reset the counter, celebId,timer,answers list
                 */

                player = MediaPlayer.create(PlayScreenActivity.this, R.raw.cheering);
                player.setOnCompletionListener(this);
                player.start();



                Toast.makeText(this,"BINGO... You got it right ",Toast.LENGTH_LONG).show();

                /* -- display the right picture
                   -- give the link to wikipedia
                 */

                if(msa.savedGamesArr.length>0) {
                    Log.i("PSA","The saved games array is > 0");
                    msa.savedGamesArr[celebId - 1] = 1;
                }
                else {
                    msa.savedGamesArr = new int[celebCount];
                    msa.savedGamesArr[celebId - 1] = 1;
                    Log.i("PSA", "The saved games array is < 0");
                }
                Log.i("PSA","The saved games array has--->");
                for(int a=0;a<msa.savedGamesArr.length;a++)
                {
                    Log.i("PSA","**** "+msa.savedGamesArr[a]+" *********");
                }
                Intent i = new Intent(this,ImageDialog.class);
                i.putExtra("PHOTO_NAME",curCeleb.getCelebPhoto()+"_orig");
                i.putExtra("PHOTO_LINK",curCeleb.getCelebLink());
                i.putExtra("CELEB_NAME",curCeleb.getCelebName());
                Log.i("onActivityResult", "The photo link is "+curCeleb.getCelebLink());
                i.putExtra("LINK",true);
                startActivity(i);
                quesCount = 10;
                updateQuesCounter(curCeleb.getCelebName());
                imageGuess.setVisibility(View.INVISIBLE);
                disGroup.clear();
                generateCelebrityId();
                populateData();
                ansList="";
                updateAnsList();
                Log.i("onActivityResult", "The name matches -- resest the game to new ID");

            }
            else {
                quesCount--;
                updateQuesCounter(curCeleb.getCelebName());
                Toast.makeText(this,"OOPS... You did not get it right ",Toast.LENGTH_LONG).show();

            }
            Log.i("PSA","Resetting the quesCount,timer and celebId");


        }
    }
    private boolean validateName(String celebGuessName){
        Log.i("PSA","celebGuessName--->"+celebGuessName);
        ArrayList<String> arrayList;

        if(celebNameMatch.containsKey(curCeleb.getCelebName())){
            Log.i("PSA","contains the key "+curCeleb.getCelebName());
        }else{
            Log.i("PSA","does not contains the key "+curCeleb.getCelebName());
            return false;
        }

        arrayList = celebNameMatch.get(curCeleb.getCelebName());
        arrayList.add(curCeleb.getCelebName().replaceAll("\\s+",""));

        for(int i=0;i<arrayList.size();i++){
            Log.i("VALIDATE NAME"," the name is "+arrayList.get(i));
            if(arrayList.get(i).equalsIgnoreCase(celebGuessName)){
                Log.i("VALIDATE NAME","YES, the name is found and is right");
                return true;

            }
        }
        return false;
    }
            private void intToByteArray(){
        Log.i("PSA","in intToByteArray - length = "+msa.savedGamesArr.length);

        ByteBuffer byteBuffer = ByteBuffer.allocate(msa.savedGamesArr.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(msa.savedGamesArr);

         msa.arrayBytes = byteBuffer.array();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("************PSA***********", "--------ON DESTROY---------");
        //msa.mPlayer.pause();
      //
      //
      // (new Intent(this,MusicService.class));
        if(player!=null){
            player.release();
            player=null;

        }

        intToByteArray();

    }
    public void onShare(View v){
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.i("onShare","Inside the if loop");
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Playing CelebrityGame")
                    .setContentDescription(
                            "Having fun guessing the celebrity")
                    .setContentUrl(Uri.parse("https://play.google.com/store?hl=en"))
                    .build();
            //setcontentURL as app download link

            mShareDialog.show(linkContent);
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
