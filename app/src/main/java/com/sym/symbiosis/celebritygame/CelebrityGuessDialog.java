package com.sym.symbiosis.celebritygame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sym.symbiosis.celebritygame.R;


public class CelebrityGuessDialog extends Activity {

    EditText celebName;
    String guessCelebName;
    static boolean celebrityguessdialog=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.LTGRAY);
        setTitle("Guess The Celebrity");


        setContentView(R.layout.activity_celebrity_guess_dialog);
        celebName = (EditText)findViewById(R.id.celebName);
        guessCelebName="";

    }

    @Override
    protected void onStart() {
        super.onStart();
        celebrityguessdialog=true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        celebrityguessdialog=true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_celebrity_guess_dialog, menu);
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
    public void onConfirmCelebName(View view){
        guessCelebName = celebName.getText().toString();
        if(guessCelebName=="")
            Toast.makeText(this,"You have not enetered the name",Toast.LENGTH_LONG).show();
        else{
            Intent i = new Intent(this,PlayScreenActivity.class);
            i.putExtra("GUESSED_CELEB_NAME",guessCelebName);
            setResult(RESULT_OK,i);
            finish();
        }


    }
}
