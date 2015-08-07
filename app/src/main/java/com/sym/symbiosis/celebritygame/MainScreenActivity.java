package com.sym.symbiosis.celebritygame;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sym.symbiosis.celebritygame.R;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;


public class MainScreenActivity extends Activity implements View.OnClickListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    CelebrityGameApp app;
    /* client used to interact with google apis*/
    private GoogleApiClient googleApiClient;
    private boolean intentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    Button letsPlay;
    Button signOutButton;
    com.google.android.gms.common.SignInButton signInButton;
    static byte arrayBytes[];
    static int [] savedGamesArr;
    Snapshot snapshot;
    static AsyncTask<Void, Void, Boolean> snapshotTask;
    AsyncTask<Void, Void, Boolean> snapshotTaskSignout;
    Person currentPerson;
    DatabaseHandler dbHandler;
    static int status=0;
    static Boolean res=false;
   // static MediaPlayer mPlayer;
    TextView mSignInText;
    static boolean isMute=false;
    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MSA","--------On CREATE----------");
        Log.i("MSA","--------On CREATE --  ---------- isMute="+isMute);
        if(savedInstanceState!=null){
            Log.i("MSA","--------On CREATE -- RECREATING ---------- isMute="+isMute);
            isMute=savedInstanceState.getBoolean("IS_MUTE");

        }

        setTitle("");
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.LTGRAY);
        setContentView(R.layout.activity_main_screen);
        /*creating app object */
        app = new CelebrityGameApp();
        app = (CelebrityGameApp)getApplication();

        mSignInText = (TextView)findViewById(R.id.signin_text);
        mSignInText.setText("Please Sign In");

        int celebCount =0;
        status =0;
        dbHandler = new DatabaseHandler(this);
        try {
            dbHandler.open();
            celebCount = dbHandler.getCountOfCelebrities();
            dbHandler.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        savedGamesArr = new int[celebCount];

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        letsPlay = (Button)findViewById(R.id.button_start);
        letsPlay.setVisibility(View.INVISIBLE);

        signInButton = (com.google.android.gms.common.SignInButton)findViewById(R.id.sign_in_button);
        signOutButton = (Button)findViewById(R.id.sign_out_button);
        signOutButton.setVisibility(View.INVISIBLE);
        signOutButton.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(googleApiClient==null) {
            Log.i("onRestoreInstanceState","googleapi client is null");

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                    .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                    .build();
        }
    }

    private void resolveSignInError() {
        Log.i("MSA","In resolveSignInError");
       if(mConnectionResult!=null) {
            Log.i("MSA","In resolveSignInError : mConnectionResult is not null");
            if (mConnectionResult.hasResolution()) {

                try {
                    intentInProgress = true;
                    startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                            0, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    intentInProgress = false;
                    googleApiClient.connect();
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

            stopService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MSA","*****onResume ********");

        if(!isMute) {
            Intent i = new Intent(this, MusicService.class);
            startService(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MSA","*****onPause ********");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MSA","*****onStart ********");
        if(isMute && (menuItem!=null)) {
            Log.i("MSA","--------set icon---------");
            menuItem.setIcon(R.drawable.ic_action_volume_muted);
        }
        googleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            intentInProgress = false;

            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
        final String currSaveName;
        if (data != null) {

            if (data.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {

                // Create a new snapshot named with a unique string
                String unique = new BigInteger(281, new Random()).toString(13);
                currSaveName = "snapshotTemp-" + unique;
                Log.i("MSA","onActivityResult --- new snapshot");
                // Create the new snapshot

                snapshotTask = new AsyncTask<Void, Void, Boolean>() {

                        @Override
                    protected void onPreExecute() {
                        Log.i("onPreExecute","Connecting");
                       // googleApiClient.connect();
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Log.i("MSA","onActivityResult --- doin background "+currentPerson.getId());
                        // Open the snapshot, creating if necessary
                        if(googleApiClient==null)
                                return false;
                        if(arrayBytes==null)
                            return  false;
                        Snapshots.OpenSnapshotResult open = Games.Snapshots.open(
                                    googleApiClient, currentPerson.getId(), true).await();

                        Log.i("MSA", "onActivityResult ---  snapshot open");
                        if (!open.getStatus().isSuccess()) {
                            Log.i("MSA", "Could not open Snapshot for migration.");
                            // https://developers.google.com/games/services/android/savedgames#handling_saved_game_conflicts
                            //return false;
                            snapshot=processSnapshotOpenResult(open, 0);
                        }else
                           snapshot = open.getSnapshot();
                        Log.i("MSA","onActivityResult ---  snapshot opened");

                        for(int i=0;i<arrayBytes.length;i=i+4)
                            Log.i(" ","-- >"+arrayBytes[i]+arrayBytes[i+1]+arrayBytes[i+2]+arrayBytes[i+3]);
                        Log.i("MSA","onActivityResult --- write bytes");
                            if(snapshot==null){
                                Log.i("MSA","onActivityResult --- Snapshot is not being able to created");
                                return false;
                            }
                        try {
                            snapshot.getSnapshotContents().writeBytes(arrayBytes);
                            // Change metadata
                            Log.i("MSA","onActivityResult --- after write bytes");
                            SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                                    .fromMetadata(snapshot.getMetadata())
                                    .setDescription("My saved game description")
                                    .build();
                            Log.i("MSA","onActivityResult --- bef commit");
                            Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
                                    googleApiClient, snapshot, metadataChange).await();
                            Log.i("MSA","onActivityResult --- after commit");
                            if (!commit.getStatus().isSuccess()) {
                                Log.i("MSA", "Failed to commit Snapshot.");
                                return false;
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        // No failures
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        res = result;
                        if(result)
                            Log.i("MSA","OnPostExecute is true");
                        else
                            Log.i("MSA","OnPostExecute is false");
                        Plus.AccountApi.clearDefaultAccount(googleApiClient);

                        googleApiClient.disconnect();
                        googleApiClient.connect();

                    }
                };
            }else
                Log.i("MSA","onActivityResult --- something else");

        }
    }
    Snapshot processSnapshotOpenResult(Snapshots.OpenSnapshotResult result, int retryCount) {
        Snapshot mResolvedSnapshot = null;
        retryCount++;

        int status = result.getStatus().getStatusCode();
        Log.i("processSnapshotOpenResult", "Save Result status: " + status);
        Log.i("processSnapshotOpenResult", "Save Result status: " + result.getStatus().toString());
        if (status == GamesStatusCodes.STATUS_OK) {
            Log.i("processSnapshotOpenResult","STATUS_OK");
            return result.getSnapshot();
        } else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE) {
            Log.i("processSnapshotOpenResult","STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE");
            return result.getSnapshot();
        } else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
            Log.i("processSnapshotOpenResult","STATUS_SNAPSHOT_CONFLICT");
            Snapshot snapshot = result.getSnapshot();
            Snapshot conflictSnapshot = result.getConflictingSnapshot();

            // Resolve between conflicts by selecting the newest of the conflicting snapshots.
            mResolvedSnapshot = snapshot;

            if (snapshot.getMetadata().getLastModifiedTimestamp() <
                    conflictSnapshot.getMetadata().getLastModifiedTimestamp()) {
                mResolvedSnapshot = conflictSnapshot;
            }

            Snapshots.OpenSnapshotResult resolveResult = Games.Snapshots.resolveConflict(
                    googleApiClient, result.getConflictId(), mResolvedSnapshot).await();

            if (retryCount < 3) {
                // Recursively attempt again
                return processSnapshotOpenResult(resolveResult, retryCount);
            } else {
                // Failed, log error and show Toast to the user
                String message = "Could not resolve snapshot conflicts";
                Log.e("processSnapshotOpenResult", message);
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }

        }
        Log.i("processSnapshotOpenResult", "failed and returning null");
        // Fail, return null.
        return null;
    }
    private void writeToSnapShot(){
        Log.i("MSA", "______writeToSnapShot");
       /* snapshot.getSnapshotContents().writeBytes(arrayBytes);
        Games.Snapshots.commitAndClose(googleApiClient, snapshot, null);*/
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        menuItem=item;
        int id = item.getItemId();
       // stopService(new Intent(this, MusicService.class));
       // Log.i("MSA","Setting volume mute icon+++++++++++++++++++++");
        //noinspection SimplifiableIfStatement
        Log.i("MSA","OnOptionsMenu called");
        if (id == R.id.action_music) {

            if(isMute) {
                item.setIcon(R.drawable.ic_action_volume_on);
                isMute = false;
                Intent i = new Intent(this, MusicService.class);
                startService(i);

            }
            else {
                item.setIcon(R.drawable.ic_action_volume_muted);
                isMute=true;
                stopService(new Intent(this,MusicService.class));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlay(View view){
        Log.i("MSA","On PlAY");
        savedGamesLoad(currentPerson.getId());
        Intent playScreen = new Intent(this,PlayScreenActivity.class);
        startActivity(playScreen);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("ONCONNECTED","User is connected");
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        signInButton.setVisibility(View.INVISIBLE);
        mSignInText.setText("You Are Signed In");
        signOutButton.setVisibility(View.VISIBLE);
        letsPlay.setVisibility(View.VISIBLE);
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = String.valueOf(currentPerson.getImage());
            String personGooglePlusProfile = currentPerson.getUrl();

        }
        if(status==0) {
            showSavedGamesUI();
            status=1;
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MSA","OnDestroy ------");
       stopService(new Intent(this,MusicService.class));
       if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button
                && !googleApiClient.isConnecting()) {
            mSignInClicked = true;
            Log.i("MSA","in OnClick : Calling resolveSignInError ");
            if(googleApiClient.isConnected())
                Log.i("OnClick","User is already connected");
            else
                googleApiClient.connect();
        }else if (v.getId() == R.id.sign_out_button) {
           /*creating a crash bcause of snapshot task already executing */
            int res=1;
            if(snapshotTask!=null && googleApiClient!=null){
                res=0;
                //fixed for crash : when asyntask is executed more than once
                Log.i("MSA","The status of the snapshot task is "+snapshotTask.getStatus());
                if(snapshotTask.getStatus() == AsyncTask.Status.FINISHED){
                    Plus.AccountApi.clearDefaultAccount(googleApiClient);
                    googleApiClient.disconnect();
                    //googleApiClient.connect();
                }
                else
                    snapshotTask.execute();


            }
            status=0;

            if (googleApiClient.isConnected() && res==1) {

                Plus.AccountApi.clearDefaultAccount(googleApiClient);

                   googleApiClient.disconnect();
                    googleApiClient.connect();


            }

            signOutButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            mSignInText.setText("Please Sign In");
            letsPlay.setVisibility(View.INVISIBLE);
            // show sign-in button, hide the sign-out button



        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("MSA","onConfigurationChanged="+newConfig.toString());
    }

    private void showSavedGamesUI() {
        int maxNumberOfSavedGamesToShow = 5;
        Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(googleApiClient,
                "See My Saves",true, false, maxNumberOfSavedGamesToShow);
        startActivityForResult(savedGamesIntent, 9009);
    }

    public void load(View v){
        Log.i("load","in func");
        savedGamesLoad(currentPerson.getId());
    }

    private void savedGamesLoad(String snapshotName) {
        Log.i("savedGamesLoad ", "entered");
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                googleApiClient, snapshotName, false);
        Log.i("savedGamesLoad ", "opened the snapshot");


        ResultCallback<Snapshots.OpenSnapshotResult> callback =
                new ResultCallback<Snapshots.OpenSnapshotResult>() {
                    @Override
                    public void onResult(Snapshots.OpenSnapshotResult openSnapshotResult) {
                        Log.i("savedGamesLoad ", "onresult");
                        if (openSnapshotResult.getStatus().isSuccess()) {
                            Log.i("savedGamesLoad ", "isSuccess");
                           arrayBytes = new byte[0];
                            try {
                                Log.i("savedGamesLoad ", "try");
                                arrayBytes = openSnapshotResult.getSnapshot().getSnapshotContents().readFully();
                                Log.i("savedGamesLoad ", "read");
                            } catch (IOException e) {
                               Log.i("Exception reading snapshot: ", e.getMessage());
                            }
                            Log.i("Reading", "the data is ----> ");
                            for(int i=0;i<arrayBytes.length;i=i+4) {
                                Log.i(" ","-- >"+arrayBytes[i]+arrayBytes[i+1]+arrayBytes[i+2]+arrayBytes[i+3]);
                            }
                            byteToIntArray();


                        } else {
                            Log.i("savedGamesLoad ", "else");

                        }


                    }
                };
        Log.i("savedGamesLoad ", "set result");
            pendingResult.setResultCallback(callback);
    }

    private void byteToIntArray(){
        IntBuffer intBuf =
                ByteBuffer.wrap(arrayBytes)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        //int[] array = new int[intBuf.remaining()];
        Log.i("MSA","Length of the array is "+intBuf.remaining());
        savedGamesArr = new int[intBuf.remaining()];
        intBuf.get(savedGamesArr);
    }


}
