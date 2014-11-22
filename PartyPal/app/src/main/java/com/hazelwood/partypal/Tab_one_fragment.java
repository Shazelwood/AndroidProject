package com.hazelwood.partypal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hazelwood.partypal.Detail.Detail_Activity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hazelwood on 11/3/14.
 */
public class Tab_one_fragment extends Fragment {
    public static final String TAG = "TABoneFRAGMENT";
    private static final String ARG_ONE = "argument_two_section";
    LocationManager locationManager;
    Double distance;

    public static Tab_one_fragment newInstance(String text) {
        Tab_one_fragment fragment = new Tab_one_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_ONE, text);
        fragment.setArguments(args);
        return fragment;
    }

    public Tab_one_fragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_one_layout, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle((ProgressDialog.STYLE_HORIZONTAL));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressNumberFormat("Loading parties...");
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final ListView listView = (ListView) getActivity().findViewById(R.id.fragment_one_list);
        final ArrayList<Party> parties = new ArrayList<Party>();

        final Party_Adapter party_adapter = new Party_Adapter(getActivity(), parties);

        Log.d(TAG, "" + System.currentTimeMillis());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PartyInfo");
        query.whereGreaterThan("endDate", System.currentTimeMillis());
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++){
                        final String name = objects.get(i).getString("name");
                        final String host = objects.get(i).getString("host");
                        final String address = objects.get(i).getString("address");
                        final String timeStart = objects.get(i).getString("startTime");
                        final String timeEnd = objects.get(i).getString("endTime");
                        final String price = objects.get(i).getString("price");
                        final String description = objects.get(i).getString("description");
                        final String objID =objects.get(i).getObjectId();
                        final int voteYES = objects.get(i).getInt("voteYes");
                        final int voteNO = objects.get(i).getInt("voteNo");
                        final long endDate = objects.get(i).getLong("endDate");
                        final String date = objects.get(i).getString("date");


                        int totalVotes = voteYES + voteNO;
                        final int votePercentage = (int) Math.floor(((float) voteYES / (float) totalVotes) * 100);

                        ParseFile imageFile = (ParseFile)objects.get(i).get("imageFile");
                        imageFile.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {


                                //locTest(address);
                                parties.add(new Party(name, host, address, timeStart, timeEnd, price, description,
                                        bytes, objID, voteYES, voteNO, votePercentage, endDate, date, 0.0));


                                Collections.sort(parties, new Comparator<Party>() {
                                    @Override
                                    public int compare(Party party1, Party party2) {
                                        return party2.getPercent()-party1.getPercent();
                                    }
                                });

                                party_adapter.notifyDataSetChanged();
                                listView.setAdapter(party_adapter);

                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent(getActivity(), Detail_Activity.class);
                detail.putExtra("party", parties.get(position));
                getActivity().startActivity(detail);

            }
        });




    }

    ///////Location////////
    @Override
    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    public void locTest(String string){

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocTask task = new LocTask();
            task.execute();

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    public class LocTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {

            Geocoder coder = new Geocoder(getActivity());
            try {
                ArrayList<Address> addresses = (ArrayList<Address>) coder.getFromLocationName(params[0], 50);
                double lat = 0, lng = 0;
                for(Address add : addresses){
                    lng = add.getLongitude();
                    lat = add.getLatitude();

                    Log.d("TAG", lat + " " + lng);
                }
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                Log.d("TAG", Double.toString(distFrom(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lat, lng)));
                distance = distFrom(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lat, lng);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
