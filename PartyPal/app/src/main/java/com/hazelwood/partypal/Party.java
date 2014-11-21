package com.hazelwood.partypal;

import java.io.Serializable;

/**
 * Created by Hazelwood on 11/5/14.
 */
public class Party implements Serializable, Comparable<Party> {

    public static final long serialVersionUID = 1234567890L;
    String name, host, location, timeBegin, timeFinish, pricing, description, objID, date;
    int voteYES, voteNO, percent;
    long endDate;
    byte[] image;

    public Party(String _name, String _host, String _loc, String _timeBegin, String _timeFin, String _price, String _descr, byte[] bytes, String id, int yes, int no, int _percent, long end_date,String _date){
        name = _name;
        host = _host;
        location = _loc;
        timeBegin = _timeBegin;
        timeFinish = _timeFin;
        pricing = _price;
        description = _descr;
        image = bytes;
        objID = id;
        voteNO = no;
        voteYES = yes;
        percent = _percent;
        endDate = end_date;
        date = _date;
    }

    public Party(){

    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public void setVoteNO(int voteNO) {
        this.voteNO = voteNO;
    }

    public void setVoteYES(int voteYES) {
        this.voteYES = voteYES;
    }

    public void setObjID(String objID) {
        this.objID = objID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public void setTimeFinish(String timeFinish) {
        this.timeFinish = timeFinish;
    }

    public String getDate() {
        return date;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getPercent() {
        return percent;
    }

    public int getVoteNO() {
        return voteNO;
    }

    public int getVoteYES() {
        return voteYES;
    }

    public String getObjID() {
        return objID;
    }

    public byte[] getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getHost() {
        return host;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPricing() {
        return pricing;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public String getTimeFinish() {
        return timeFinish;
    }

    @Override
    public int compareTo(Party party) {
        return this.getPercent()-(party.getPercent());
    }
}
