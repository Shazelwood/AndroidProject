package com.hazelwood.partypal.Detail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hazelwood.partypal.Party;
import com.hazelwood.partypal.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hazelwood on 11/10/14.
 */
public class Detail_Activity extends Activity {
    public static final String TAG = "DETAILS_TAG";
    static final String ACTION_UPDATE_TAB_TWO = "ACTION_UPDATE_TAB_TWO";
    String objID;
    boolean isOld;
    Button yesBTN, noBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getActionBar().setTitle("Party Details");

        Parse.initialize(this, "xZ8EWHn0jDK7lACkdvPorPp3u6RjP62M5AjgB5iz", "MOYDodRkqbuFCP3t1RVhDCpXuTCn4YNhUkd6G6KU");

        Intent detail = getIntent();
        Party party = (Party) detail.getSerializableExtra("party");
        boolean isOld = detail.getBooleanExtra("isOld", false);

        objID = party.getObjID();




        TextView name = (TextView)findViewById(R.id.party_name);
        final TextView location = (TextView)findViewById(R.id.party_location);
        TextView time = (TextView)findViewById(R.id.party_time);
        TextView price = (TextView)findViewById(R.id.party_price);
        TextView description = (TextView)findViewById(R.id.party_description);

        yesBTN = (Button) findViewById(R.id.yesBTN);
        noBTN = (Button)findViewById(R.id.noBTN);

        if(!isOld){
            yesBTN.setOnClickListener(vote);
            noBTN.setOnClickListener(vote);
            yesBTN.setVisibility(View.INVISIBLE);
            noBTN.setVisibility(View.INVISIBLE);
        }



        name.setText(party.getName());
        location.setText(party.getLocation());
        time.setText(party.getTimeBegin() + " - " + party.getTimeFinish());
        price.setText(party.getPricing());
        description.setText(party.getDescription());

        final String getHost = party.getHost();

        ArrayList<Party> checkArray = getParties(this);

        for (int i = 0; i < checkArray.size(); i++){
            if (checkArray.get(i).getObjID().equals(party.getObjID())){
                Log.d(TAG, party.getName());
                yesBTN.setEnabled(false);
                noBTN.setEnabled(false);
            }
        }

        List<String> hostList = Arrays.asList(getHost.split("\\s*,\\s*"));
        final ArrayList<String> hostArray = new ArrayList<String>();
        for (String s : hostList){
            hostArray.add(s);
        }

        GridView hostGV = (GridView) findViewById(R.id.party_host_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hostArray);
        hostGV.setAdapter(adapter);

        hostGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (hostArray.get(position).contains("@")){
                    Log.d("TAG", "Twitter");
                    try {
                        Intent twitter = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("twitter://user?screen_name=" + hostArray.get(position)));
                        startActivity(twitter);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    Log.d("TAG", "naw");
                }
            }
        });

        ImageView imageView = (ImageView)findViewById(R.id.detail_image);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(party.getImage(), 0, party.getImage().length));

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + location.getText().toString()));
                startActivity(intent);

            }
        });
    }

    View.OnClickListener vote = new View.OnClickListener() {
        int id;
        @Override
        public void onClick(View v) {
            final ProgressDialog progressDialog = new ProgressDialog(Detail_Activity.this);
            progressDialog.setProgressStyle((ProgressDialog.STYLE_HORIZONTAL));
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressNumberFormat("Submitting vote");
            progressDialog.setProgressPercentFormat(null);
            progressDialog.setCancelable(false);
            progressDialog.show();
            id = v.getId();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("PartyInfo");
            query.getInBackground(objID, new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        yesBTN.setEnabled(false);
                        noBTN.setEnabled(false);
                        yesBTN.setFocusable(false);
                        noBTN.setFocusable(false);

                        switch (id){
                            case R.id.yesBTN:
                                Log.d("VOTES", "YES");
                                int YES = parseObject.getInt("voteYes") + 1;
                                parseObject.put("voteYes", YES);
                                getParty(parseObject);

                                Intent updateGOING = new Intent(ACTION_UPDATE_TAB_TWO);
                                sendBroadcast(updateGOING);



                                break;
                            case R.id.noBTN:
                                Log.d("VOTES", "NO");
                                int NO = parseObject.getInt("voteNo") + 1;
                                parseObject.put("voteNo", NO);
                                break;
                            default:
                                break;
                        }

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.dismiss();
                            }
                        });

                    } else {
                        //alert
                    }

                }

            });


        }
    };

    public void getParty(ParseObject object){

        final String name = object.getString("name");
        final String host = object.getString("host");
        final String address = object.getString("address");
        final String timeStart = object.getString("startTime");
        final String timeEnd = object.getString("endTime");
        final String price = object.getString("price");
        final String description = object.getString("description");
        final String objID =object.getObjectId();
        final int voteYES = object.getInt("voteYes");
        final int voteNO = object.getInt("voteNo");
        final long endDate = object.getLong("endDate");
        final String date = object.getString("date");


        int totalVotes = voteYES + voteNO;
        final int votePercentage = (int) Math.floor(((float) voteYES / (float) totalVotes) * 100);

        ParseFile imageFile = (ParseFile)object.get("imageFile");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Party party = new Party(
                        name, host,
                        address, timeStart,
                        timeEnd, price,
                        description, bytes,
                        objID, voteYES,
                        voteNO, votePercentage,
                        endDate, date, 0.0);
                saveToGoing(Detail_Activity.this, party);
            }
        });
    }

    public void saveToGoing(Context context, Party party) {

        ArrayList<Party> data = null;


        try {
            File extFolder = getExternalFilesDir(null);
            File file = new File(extFolder, "PARTY.dat");

            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                data = (ArrayList<Party>) oin.readObject();
                oin.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(data == null) {
            data = new ArrayList<Party>();
        }

        if(!data.contains(party)) {
            data.add(party);
        }

        try {

            File extFolder = getExternalFilesDir(null);
            File extFile = new File(extFolder, "PARTY.dat");
            FileOutputStream fos = new FileOutputStream(extFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Party> getParties(Context context) {
        ArrayList<Party> arrayList = new ArrayList<Party>();

        try {
            File extFolder = context.getExternalFilesDir(null);
            File file = new File(extFolder, "PARTY.dat");

            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                arrayList = (ArrayList<Party>) oin.readObject();
                oin.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return arrayList;
    }
}
