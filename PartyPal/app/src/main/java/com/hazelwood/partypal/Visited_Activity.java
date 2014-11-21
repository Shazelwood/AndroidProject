package com.hazelwood.partypal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Hazelwood on 11/18/14.
 */
public class Visited_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited);

        ListView listView = (ListView)findViewById(R.id.visited_list);
        Party_Adapter party_adapter;

        ArrayList<Party> arrayList = new ArrayList<Party>();
        ArrayList<Party> partyArrayList = new ArrayList<Party>();

        try {
            File extFolder = getExternalFilesDir(null);
            File file = new File(extFolder, "OLD_PARTY.dat");

            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                arrayList = (ArrayList<Party>) oin.readObject();
                oin.close();


                party_adapter = new Party_Adapter(this, arrayList);
                party_adapter.notifyDataSetChanged();
                listView.setAdapter(party_adapter);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
