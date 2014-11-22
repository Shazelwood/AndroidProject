package com.hazelwood.partypal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hazelwood.partypal.Detail.Detail_Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Hazelwood on 11/3/14.
 */
public class Tab_two_fragment extends Fragment {
    ListView listView;
    Party_Adapter party_adapter;
    static final String ACTION_UPDATE_TAB_TWO = "ACTION_UPDATE_TAB_TWO";


    private static final String ARG_ONE = "argument_two_section";
    public static final String TAG = "TAB_TWO_FRAGMENT";

    public static Tab_two_fragment newInstance(ArrayList<Party> parties) {
        Tab_two_fragment fragment = new Tab_two_fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ONE, parties);
        fragment.setArguments(args);
        return fragment;
    }

    public Tab_two_fragment() {
    }

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two_layout, container, false);
        return rootView;
    }


    public static ArrayList<Party> getParties(Context context) {
        ArrayList<Party> parties = new ArrayList<Party>();

        try {
            InputStream is = context.openFileInput("PARTY.dat");
            ObjectInputStream ois = new ObjectInputStream(is);
            ArrayList<Party> data = (ArrayList<Party>)ois.readObject();
            ois.close();
            if(data != null) {
                parties.addAll(data);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return parties;
    }

    @Override
    public void onResume() {
        super.onResume();

        final ArrayList<Party> arrayList;

        try {
            File extFolder = getActivity().getExternalFilesDir(null);
            File file = new File(extFolder, "PARTY.dat");

            if (file.exists()) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                arrayList = (ArrayList<Party>) oin.readObject();
                oin.close();
                long time = System.currentTimeMillis();

                for (int i = 0; i < arrayList.size(); i++){
                    if (arrayList.get(i).getEndDate() < time){
                        saveOldParty(getActivity(), arrayList.get(i));
                    }
                }



                party_adapter = new Party_Adapter(getActivity(), arrayList);
                party_adapter.notifyDataSetChanged();
                listView = (ListView) getActivity().findViewById(R.id.fragment_two_list);
                listView.setAdapter(party_adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent detail = new Intent(getActivity(), Detail_Activity.class);
                        detail.putExtra("party", arrayList.get(position));
                        detail.putExtra("old", true);
                        getActivity().startActivity(detail);

                    }
                });
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }


    public void saveOldParty(Context context, Party party) {

        ArrayList<Party> data = null;


        try {
            File extFolder = getActivity().getExternalFilesDir(null);
            File file = new File(extFolder, "OLD_PARTY.dat");

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

        if(!(data.contains(party))) {
            data.add(party);
        }

        try {

            File extFolder = getActivity().getExternalFilesDir(null);
            File extFile = new File(extFolder, "OLD_PARTY.dat");
            FileOutputStream fos = new FileOutputStream(extFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
