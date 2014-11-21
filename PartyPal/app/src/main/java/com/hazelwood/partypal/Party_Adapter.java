package com.hazelwood.partypal;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hazelwood on 11/5/14.
 */
public class Party_Adapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Party> mObjects;

    private static final long ID_CONSTANT = 123456789;

    public Party_Adapter(Context c, ArrayList<Party> objects){
        mContext = c;
        mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Party getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ID_CONSTANT + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        Party item = getItem(position);

        TextView title = (TextView)convertView.findViewById(R.id.item_title);
        TextView description = (TextView)convertView.findViewById(R.id.item_description);
//        TextView distance = (TextView)convertView.findViewById(R.id.item_distance);
        TextView vote = (TextView)convertView.findViewById(R.id.item_vote);
        ImageView image = (ImageView)convertView.findViewById(R.id.item_image);

        int voteYES = item.getVoteYES();
        int voteNO = item.getVoteNO();
        int totalVotes = voteYES + voteNO;
        int votePercentage = (int) Math.floor(((float) voteYES / (float) totalVotes) * 100);


        if (votePercentage == 0) {
            vote.setText("no votes");
            vote.setTextColor(Color.parseColor("#757575"));
        } else if (votePercentage <= 50 ){
            vote.setText(votePercentage + "%");
            vote.setTextColor(Color.parseColor("#F44336"));

        } else if (votePercentage > 50) {
            vote.setText(votePercentage + "%");
            vote.setTextColor(Color.parseColor("#4CAF50"));

        }

        title.setText(item.getName());
        description.setText(item.getDescription());
//        distance.setText("?");

        image.setImageBitmap(BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length));



        return convertView;
    }
}
