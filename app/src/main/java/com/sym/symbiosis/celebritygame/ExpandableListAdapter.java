package com.sym.symbiosis.celebritygame;

/**
 * Created by symbiosis on 3/3/2015.
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sym.symbiosis.celebritygame.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter implements AdapterView.OnItemSelectedListener {

    private Context context;
   // private List<String> listCategories; // header titles
    private ArrayList<GroupHeader> listCategories; // header titles
    private HashMap<String, List<String>> listSubCategories;
    PlayScreenActivity objPSA;
    DatabaseHandler dbHandler;
    String continentSelected;
    TextView lblListHeader;
    static int c=0;
    public ExpandableListAdapter(Context context, List<String> listCategories,
                                 HashMap<String, List<String>> listSubCategories) {
        this.context = context;
        Log.i("ELA","In constructor");

        this.listCategories = new ArrayList<GroupHeader>();
        for(int i=0;i<listCategories.size();i++){
            String imgSrc="gender";
            String headerTitle = listCategories.get(i);
          if(headerTitle.equals("Profession"))
                imgSrc="profession";
            else if(headerTitle.equals("Gender"))
                imgSrc="gender";
            else if(headerTitle.equals("Alive"))
                imgSrc="alivedead";
            else if(headerTitle.equals("Country"))
                imgSrc="country";
            else if(headerTitle.equals("Continent"))
                imgSrc="continent";

            this.listCategories.add(new GroupHeader(imgSrc,listCategories.get(i),Boolean.FALSE));
        }
        this.listSubCategories = listSubCategories;
        continentSelected="";

    }

   @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listSubCategories.get(this.listCategories.get(groupPosition).getmGrpName())
               .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    private void formMessage(String sI){
        String msg,cat;
        if(sI.equals("Male")){

            cat = "Gender";
            msg = "Is celebrity a Male";
            showDialog(cat,sI,msg);
        }else if(sI.equals("Female")){
            cat = "Gender";
            msg = "Is celebrity a Female";
            showDialog(cat,sI,msg);
        }
        else if(sI.equals("Is Alive")){
            cat = "Alive";
            msg = "Is celebrity Alive person";
            showDialog(cat,sI,msg);
        }else if(sI.equals("Is Dead")){
            cat = "Alive";
            msg = "Is celebrity a Dead person";
            showDialog(cat,sI,msg);
        }


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Question Celebrity is ",objPSA.curCeleb.toString());


        String selectedItem = parent.getSelectedItem().toString();
        String headerItem = parent.getItemAtPosition(0).toString();
        Log.i("The header and selected is",headerItem+" "+selectedItem);
        if(headerItem.equals("------ Choose the Gender ------")){
              formMessage(selectedItem);
        }else if(headerItem.equals("------ Choose the Status ------")){
            formMessage(selectedItem);
        }else if(headerItem.equals("------ Select Sport Name ------")){
            if(!selectedItem.equals("------ Select Sport Name ------"))
                showDialog("Sport Name", selectedItem, "Is the Sport name : " + selectedItem);
        }else if(headerItem.equals("------ Select Sport Type ------")) {
            if(!selectedItem.equals("------ Select Sport Type ------"))
                showDialog("Sport Type", selectedItem, "Is it " + selectedItem + " sport");
        }else if(headerItem.equals("------ Select One ------")) {
             if(!selectedItem.equals("------ Select One ------"))
                 showDialog("Active", selectedItem, "Is the Sport person " + selectedItem);
        }else if(headerItem.equals("------ Select Born Year Range ------")) {
            if(!selectedItem.equals("------ Select Born Year Range ------"))
                showDialog("Born Year", selectedItem, "Is the Sport person born in years " + selectedItem);
        }else if(headerItem.equals("------ Select Party Name ------")){
            if(!selectedItem.equals("------ Select Party Name ------"))
                showDialog("Party Name", selectedItem, "Is the Party name : " + selectedItem);
        }else if(headerItem.equals("------ Select Served As ------")){
            if(!selectedItem.equals("------ Select Served As ------"))
                showDialog("Served As", selectedItem, "Did the celebrity served as : " + selectedItem);
        }else if(headerItem.equals("------ Select Occupation ------")){
            if(!selectedItem.equals("------ Select Occupation ------"))
                showDialog("Occupation", selectedItem, "Is the celebrity occupation : " + selectedItem);
        }
        else {

            Cursor cursor=(Cursor)parent.getSelectedItem();
            if(cursor.getCount()>0) {
                selectedItem = cursor.getString(0);
                cursor = (Cursor) parent.getItemAtPosition(0);
                headerItem = cursor.getString(0);
            }
            Log.i("The header and selected after is",headerItem+" "+selectedItem);
            if(headerItem.equals("------ Select Country ------") && (!selectedItem.equals("------ Select Country ------"))){
                 showDialog("Country",selectedItem,"Is the celebrity from "+selectedItem+ " country");
            }else if(headerItem.equals("------ Select Continent ------")&& (!selectedItem.equals("------ Select Continent ------"))){
                showDialog("Continent", selectedItem, "Is the celebrity from " + selectedItem + " continent");
            }else if(headerItem.equals("------ Select Profession ------")&& (!selectedItem.equals("------ Select Profession ------"))){
                showDialog("Profession", selectedItem, "Is the celebrity from " + selectedItem + " profession");
            }
        }
    }



    private void showDialog(final String category, final String selectedItem,String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Your Question is");

        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        boolean res = validateAnswer(category,selectedItem);
                        showToast(res);

                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    private boolean validateAnswer(String cat,String ans){
        GroupHeader grpHeader;
        if(cat.equals("Gender"))
        {   grpHeader = listCategories.get(0);
            if(objPSA.curCeleb.getCelebGender().equals(ans)){
                 objPSA.notifyAnswer(ans);
                 objPSA.disGroup.put(cat, 1);
                 objPSA.exQuestLV.collapseGroup(0);
                Log.i("ELA bef collapse","Name is "+grpHeader.getmGrpName()+" --- vlue is "+grpHeader.getmGrpDone());
                 grpHeader.setmGrpDone(Boolean.TRUE);
                 return  true;
            }else {
                if(ans.equals("Male"))
                    objPSA.notifyAnswer("Female");
                else
                    objPSA.notifyAnswer("Male");

                objPSA.disGroup.put(cat,1);
            }
            objPSA.exQuestLV.collapseGroup(0);
            Log.i("ELA bef collapse","Name is "+grpHeader.getmGrpName()+" --- vlue is "+grpHeader.getmGrpDone());
            grpHeader.setmGrpDone(Boolean.TRUE);

        }else if(cat.equals("Alive")) {
            grpHeader = listCategories.get(1);
            int alive;
            alive = ans.equals("Is Alive") ? 1 : 0;
            if (objPSA.curCeleb.getCelebAlive() == alive) {
                objPSA.notifyAnswer(ans);
                objPSA.disGroup.put(cat,1);
                objPSA.exQuestLV.collapseGroup(1);
                grpHeader.setmGrpDone(Boolean.TRUE);
                return true;

            }else {
            if(ans.equals("Is Alive"))
                objPSA.notifyAnswer("Dead");
            else
                objPSA.notifyAnswer("Alive");
                objPSA.disGroup.put(cat,1);
            }
            objPSA.exQuestLV.collapseGroup(1);
            grpHeader.setmGrpDone(Boolean.TRUE);

    }else if(cat.equals("Country")) {
            grpHeader = listCategories.get(2);
            if (objPSA.curCeleb.getCelebCountry().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(2);
                objPSA.disGroup.put(cat,2);
                grpHeader.setmGrpDone(Boolean.TRUE);
                return true;
            }
        }else if(cat.equals("Continent")) {
            grpHeader = listCategories.get(3);
            if (objPSA.curCeleb.getCelebContinent().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(3);
                objPSA.disGroup.put(cat,3);
                continentSelected = ans;
                Log.i("ELA","Continent selected set to  "+ans);
                grpHeader.setmGrpDone(Boolean.TRUE);
                return true;
            }
        }else if(cat.equals("Profession")) {
            grpHeader = listCategories.get(4);
            if (objPSA.curCeleb.getCelebProfession().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(4);
                objPSA.disGroup.put(cat,4);
                grpHeader.setmGrpDone(Boolean.TRUE);
                populateSpecificData(ans);
                return true;
            }
        }
        /*validating sport specific details*/
        else if(cat.equals("Sport Name")){
            grpHeader = listCategories.get(5);
            if (objPSA.sportCeleb.getSportName().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(5);
                objPSA.disGroup.put(cat, 5);
                grpHeader.setmGrpDone(Boolean.TRUE);
                return true;
            }
        }else if(cat.equals("Sport Type")){
                grpHeader = listCategories.get(6);
                if (objPSA.sportCeleb.getSportType().equals(ans)) {
                    objPSA.notifyAnswer(ans);
                    objPSA.exQuestLV.collapseGroup(6);
                    objPSA.disGroup.put(cat,6);
                    grpHeader.setmGrpDone(Boolean.TRUE);
                    return true;
            }
        }else if(cat.equals("Active")){
            grpHeader = listCategories.get(7);
              boolean ansRight=false;
              if(objPSA.curCeleb.getCelebProfession().equals("Sports")){
                   ansRight = objPSA.sportCeleb.getPlayerActive().equals(ans);
                   if(ansRight){

                       objPSA.exQuestLV.collapseGroup(7);
                       objPSA.disGroup.put(cat, 7);
                       grpHeader.setmGrpDone(Boolean.TRUE);
                       objPSA.notifyAnswer(ans);
                       return true;
                   }
              }else if(objPSA.curCeleb.getCelebProfession().equals("Politician")){
                  ansRight = objPSA.poltCeleb.getPoliticianActive().equals(ans);
                  if(ansRight){

                      objPSA.exQuestLV.collapseGroup(7);
                      objPSA.disGroup.put(cat, 7);
                      objPSA.notifyAnswer(ans);
                      grpHeader.setmGrpDone(Boolean.TRUE);
                      return true;
                  }
              }


        }else if(cat.equals("Born Year")){
            grpHeader = listCategories.get(8);
            boolean ansRight=false;
            if(objPSA.curCeleb.getCelebProfession().equals("Sports")){

                ansRight = objPSA.sportCeleb.getBornYear().equals(ans);
                if(ansRight) {
                    objPSA.notifyAnswer(ans);
                    objPSA.exQuestLV.collapseGroup(8);
                    grpHeader.setmGrpDone(Boolean.TRUE);
                    objPSA.disGroup.put(cat, 8);
                    return true;
                }
            }else if(objPSA.curCeleb.getCelebProfession().equals("Politician")){
                ansRight = objPSA.poltCeleb.getBornYear().equals(ans);
                if(ansRight) {
                    objPSA.notifyAnswer(ans);
                    objPSA.exQuestLV.collapseGroup(8);
                    grpHeader.setmGrpDone(Boolean.TRUE);
                    objPSA.disGroup.put(cat, 8);
                    return true;
                }
            }

        } /* POLITICIAN DETAILS*/
        else if(cat.equals("Party Name")){
            grpHeader = listCategories.get(5);
            if (objPSA.poltCeleb.getPartyName().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(5);
                grpHeader.setmGrpDone(Boolean.TRUE);
                objPSA.disGroup.put(cat, 5);
                return true;
            }
        }else if(cat.equals("Served As")){
            grpHeader = listCategories.get(6);
            if (objPSA.poltCeleb.getServedAs().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(6);
                grpHeader.setmGrpDone(Boolean.TRUE);
                objPSA.disGroup.put(cat, 6);
                return true;
            }
        }else if(cat.equals("Occupation")){
            grpHeader = listCategories.get(9);
            if (objPSA.poltCeleb.getOccupation().equals(ans)) {
                objPSA.notifyAnswer(ans);
                objPSA.exQuestLV.collapseGroup(9);
                grpHeader.setmGrpDone(Boolean.TRUE);
                objPSA.disGroup.put(cat, 9);
                return true;
            }
        }
            return false;
    }
    private void showToast(boolean result){
        if(result) {
            Toast.makeText(context, "You are absolutely right --- BINGO ", Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(context,"Oops .. You are wrong -- Try Again ",Toast.LENGTH_LONG).show();

        }
        objPSA.quesCount--;
        objPSA.updateQuesCounter(objPSA.curCeleb.getCelebName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static class ViewHolderPag
    {
        Spinner spinner;
    }
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolderPag viewHolderPag = new ViewHolderPag();
        dbHandler = new DatabaseHandler(context);

        final String grpText = (String) getGroup(groupPosition);
        Log.i("EPA","Group selected text is "+grpText);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_options_continents, null);
            viewHolderPag.spinner
                        = (Spinner)convertView.findViewById(R.id.liContinents_Countries);
            convertView.setTag(viewHolderPag);
        }
        else{
            viewHolderPag = (ViewHolderPag)convertView.getTag();
        }

        if(grpText.equals("Alive")){
            String[] arr = context.getResources().getStringArray(R.array.Alive);
            ArrayAdapter<String> adapterAlive = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterAlive);
        }
        else if(grpText.equals("Gender")){

            Log.i("EPA","Group gender selected");

            String[] arr = context.getResources().getStringArray(R.array.Gender);
            ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterGender);

        }
        else if(grpText.equals("Continent") ){
            try{
                Cursor c=null;
                dbHandler.open();
                c = dbHandler.getContinents();
                if(c!=null){
                    SimpleCursorAdapter adapterContinent = new SimpleCursorAdapter(context,android.R.layout.simple_list_item_1,
                            c,new String[]{"_CONTINENT"},new int[] {android.R.id.text1},0);
                    viewHolderPag.spinner.setAdapter(adapterContinent);

                }
                dbHandler.close();

            }catch(Exception e){
                e.printStackTrace();
            }

        }
        else if(grpText.equals("Country") ){
            try{
                Cursor c;
                dbHandler.open();
                if(continentSelected=="") {
                    Log.i("ELA","IF Continent selected is "+continentSelected);
                    c = dbHandler.getCountries();
                }
                else {
                    Log.i("ELA","ELSE Continent selected is "+continentSelected);
                    c = dbHandler.getCountriesSelected(continentSelected);
                }
                if(c!=null){
                    Log.i("ELA","C != NULL");
                    SimpleCursorAdapter adapterCountry = new SimpleCursorAdapter(context,android.R.layout.simple_list_item_1,
                            c,new String[]{"_COUNTRY"},new int[] {android.R.id.text1},0);
                    viewHolderPag.spinner.setAdapter(adapterCountry);
                }
                dbHandler.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(grpText.equals("Profession") ){
            try{
                dbHandler.open();
                Cursor c = dbHandler.getProfessions();
                if(c!=null){
                    SimpleCursorAdapter adapterProf = new SimpleCursorAdapter(context,android.R.layout.simple_list_item_1,
                            c,new String[]{"_PROFESSION"},new int[] {android.R.id.text1},0);
                    viewHolderPag.spinner.setAdapter(adapterProf);
                }
                dbHandler.close();

            }catch(Exception e){
                e.printStackTrace();
            }
            /*SPORT specific question population*/
        }else if(grpText.equals("Sport Name")){
            String[] arr = context.getResources().getStringArray(R.array.sportnames);
            ArrayAdapter<String> adapterSportName = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterSportName);
        }
        else if(grpText.equals("Sport Type")){
            String[] arr = context.getResources().getStringArray(R.array.sporttype);
            ArrayAdapter<String> adapterSportType = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterSportType);
        }else if(grpText.equals("Active")){
            String[] arr = context.getResources().getStringArray(R.array.active);
            ArrayAdapter<String> adapterActive = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterActive);
        }else if(grpText.equals("Born Year")){
            String[] arr = context.getResources().getStringArray(R.array.bornyear);
            ArrayAdapter<String> adapterBornYear = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterBornYear);
        }

        /* POLITICIAN specific question population */
        else if(grpText.equals("Party Name")){
            String[] arr = context.getResources().getStringArray(R.array.partynames);
            ArrayAdapter<String> adapterPartyName = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1
                    ,android.R.id.text1,arr);
            viewHolderPag.spinner.setAdapter(adapterPartyName);
        }
        else if(grpText.equals("Served As")) {
            String[] arr = context.getResources().getStringArray(R.array.servedas);
            ArrayAdapter<String> adapterServedAs = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1
                    , android.R.id.text1, arr);
            viewHolderPag.spinner.setAdapter(adapterServedAs);
        }else if(grpText.equals("Occupation")) {
            String[] arr = context.getResources().getStringArray(R.array.occupation);
            ArrayAdapter<String> adapterOccupation = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1
                    , android.R.id.text1, arr);
            viewHolderPag.spinner.setAdapter(adapterOccupation);
        }



        if(viewHolderPag.spinner!=null)
            viewHolderPag.spinner.setOnItemSelectedListener(this);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {


        ArrayList<String> list = (ArrayList)this.listSubCategories.get(this.listCategories.get(groupPosition).getmGrpName());

        if(list!=null) {
            return list.size();
        }
        else
            return 0;



    }

    public void addGroup(GroupHeader grp){

        this.listCategories.add(grp);
        Log.i("ELA","The size is "+this.listCategories.size());
    }
    @Override
    public Object getGroup(int groupPosition) {
        return this.listCategories.get(groupPosition).getmGrpName();
    }

    @Override
    public int getGroupCount() {
        return this.listCategories.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    static class ViewHolderGroupPag{
        ImageView imageView;
        TextView lblListHeader;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        ViewHolderGroupPag vhgp;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_categories, null);
            vhgp = new ViewHolderGroupPag();

            vhgp.imageView = (ImageView)convertView.findViewById(R.id.imageView2);
            vhgp.lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            convertView.setTag(vhgp);
        }
        else
        {
            vhgp = (ViewHolderGroupPag)convertView.getTag();
        }
        LinearLayout specGroup = (LinearLayout) convertView.findViewById(R.id.groupid);
        if((headerTitle.equals("Party Name"))|| (headerTitle.equals("Served As")) || (headerTitle.equals("Active"))||
                (headerTitle.equals("Born Year")) || (headerTitle.equals("Occupation")) || (headerTitle.equals("Sport Name"))
                || (headerTitle.equals("Sport Type")) ){

            specGroup.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));

        }else
            specGroup.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));


        GroupHeader gh = listCategories.get(groupPosition);

        vhgp.lblListHeader.setTypeface(null, Typeface.BOLD);
        vhgp.lblListHeader.setText(gh.getmGrpName());

        if(gh.getmGrpDone()==Boolean.TRUE) {
            vhgp.lblListHeader.setPaintFlags(vhgp.lblListHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else if(gh.getmGrpDone()==Boolean.FALSE){
            vhgp.lblListHeader.setPaintFlags(vhgp.lblListHeader.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        int resID = context.getResources().getIdentifier(gh.getmGrpImage(), "drawable", context.getPackageName());
        vhgp.imageView.setImageResource(resID);


        objPSA.exQuestLV.setDividerHeight(30);

        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addChild(String grp){

        ArrayList<String> arrSubSpecData = new ArrayList<String>();
        Log.i("ELA","Group name is "+grp);
        arrSubSpecData.add("select...");
        this.listSubCategories.put(grp,arrSubSpecData);

    }
    private void populateSpecificData(String prof){
        String [] specData = new String[0];
        if(prof.equals("Sports")) {
            specData = context.getResources().getStringArray(R.array.sports);/*sport category questions*/
            getSportsDataFromDB();

        }else if(prof.equals("Politician")){
            specData = context.getResources().getStringArray(R.array.politics);/*politics category questions*/
            getPoliticianDataFromDB();

        }
        if(specData.length > 0) {
            GroupHeader x;

            for (int i = 0; i < specData.length; i++) {
                String imgSrc="gender";
                String headerTitle = specData[i];
                if(headerTitle.equals("Party Name"))
                    imgSrc="partyname";
                else if(headerTitle.equals("Served As"))
                    imgSrc="servedas";
                else if(headerTitle.equals("Occupation"))
                    imgSrc="occupation";
                else if(headerTitle.equals("Sport Name"))
                    imgSrc="sportname";
                else if(headerTitle.equals("Sport Type"))
                    imgSrc="sporttype";
                else if(headerTitle.equals("Active"))
                    imgSrc="active";
                else if(headerTitle.equals("Born Year"))
                    imgSrc="bornyear";
                x = new GroupHeader(imgSrc,specData[i],Boolean.FALSE);
                addGroup(x);
                addChild(specData[i]);

            }
        }

           ((Activity)context).runOnUiThread(new Runnable() {
               public void run() {
               Log.i ("ELA","Calling notify");
                   objPSA.expQuesListAdapter.notifyDataSetChanged();
               }
           });

    }
    private void getPoliticianDataFromDB(){
        objPSA.poltCeleb = (PoliticianCelebrity)objPSA.curCeleb;
        try {
            dbHandler.open();
            Cursor c = dbHandler.getPoliticianDetails(((PoliticianCelebrity) objPSA.curCeleb).getCelebName());
            Log.i("getPoliticianDataFromDB","get the polictician data");
            if(c.getCount()>0){
                c.moveToFirst();
                Log.i("ELA","Getting the politician data");
                objPSA.poltCeleb.setName(c.getString(0));
                objPSA.poltCeleb.setPartyName(c.getString(1));
                objPSA.poltCeleb.setServedAs(c.getString(2));
                objPSA.poltCeleb.setPoliticianActive(c.getString(3));
                objPSA.poltCeleb.setOccupation(c.getString(4));
                objPSA.poltCeleb.setBornYear(c.getString(5));
                Log.i("ELA","Done Getting the politician data");

            }
            dbHandler.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }
    private void getSportsDataFromDB(){
        objPSA.sportCeleb = (SportsCelebrity)objPSA.curCeleb;
        try {
            dbHandler.open();
            Cursor c = dbHandler.getSportDetails(((SportsCelebrity) objPSA.curCeleb).getCelebName());
            Log.i("getSportsDataFromDB","get the sports data");
            if(c.getCount()>0){
                c.moveToFirst();
                Log.i("ELA","Getting the sports data");
                objPSA.sportCeleb.setName(c.getString(0));
                objPSA.sportCeleb.setSportName(c.getString(1));
                objPSA.sportCeleb.setSportType(c.getString(2));
                objPSA.sportCeleb.setPlayerActive(c.getString(3));
                objPSA.sportCeleb.setBornYear(c.getString(4));
                Log.i("ELA","Done Getting the sports data");

            }
            dbHandler.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.i("ELA","notifyDataSetChanged function");
    }
}
