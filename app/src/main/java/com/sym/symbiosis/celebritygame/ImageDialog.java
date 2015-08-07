package com.sym.symbiosis.celebritygame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sym.symbiosis.celebritygame.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by symbiosis on 3/29/2015.
 */

public class ImageDialog extends Activity {

    Boolean isLink;
    String photoSrc;
    String photoLink;
    Button knowMore;
    Button askFriend;
    ImageView iv;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String celebName;
    int resID;
    static boolean imageguessdialog=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.LTGRAY);
        setTitle("Celebrity Image");
        setContentView(R.layout.activity_image_dialog);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.i("ImageDialog","Posted success on FB");
                Toast.makeText(ImageDialog.this,"Successfully posted on your Facbook",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Log.i("ImageDialog","Posting canceled on FB");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i("ImageDialog","Error posting on FB");
                e.printStackTrace();
            }

            });

        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setVisibility(View.INVISIBLE);

        knowMore = (Button) findViewById(R.id.know_more_button);
        knowMore.setVisibility(View.INVISIBLE);

        askFriend = (Button) findViewById(R.id.ask_friend_button);


        Intent i = getIntent();

        photoSrc = i.getStringExtra("PHOTO_NAME");
        isLink = i.getBooleanExtra("LINK",false);
        photoLink = i.getStringExtra("PHOTO_LINK");
        if(isLink)
            celebName = i.getStringExtra("CELEB_NAME");

        if(photoSrc==null){
            photoSrc = "cb_question_mark_launcher";
        }
        resID = getResources().getIdentifier(photoSrc, "drawable", getPackageName());
        if(iv!=null){
            ((BitmapDrawable)iv.getDrawable()).getBitmap().recycle();
            iv = (ImageView) findViewById(R.id.celebimageview) ;
            iv.setImageResource(resID);
        }else{
            iv = (ImageView) findViewById(R.id.celebimageview) ;
            iv.setImageResource(resID);
        }

        if(isLink){

            knowMore.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.VISIBLE);
            askFriend.setVisibility(View.INVISIBLE);
        }

    }
    public void onFBShare(View v){
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            Log.i("onFbShare","Inside the if loop");
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("My Game Success")
                    .setContentDescription(
                            "Finished guessing " + celebName + " on CelebrityGuess")
                    .setContentUrl(Uri.parse("https://play.google.com/store?hl=en"))
                    .build();
            //setcontentURL as app download link

            shareDialog.show(linkContent);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onKnowMore(View v){

           /*check connectivity and provide intent to wiki link*/
        if(isNetworkAvailable()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(photoLink));
            startActivity(browserIntent);
        }else
        {
            Toast.makeText(this,"Check your network connectivity !!",Toast.LENGTH_LONG).show();
        }
    }
    public void onAskFriend(View v)
    {
        Bitmap bm = BitmapFactory.decodeResource(getResources(),resID);
        SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bm)
                            .build();

        if (ShareDialog.canShow(SharePhotoContent.class)) {
            Log.i("onAskFriend","Inside the if loop");
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareDialog.show(content);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageguessdialog=true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imageguessdialog=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
