package com.example.wayto.sample;

public class DataListCounselor {
    String counselorid, counselorname;

    public DataListCounselor(String counselorid, String counselorname) {
        this.counselorid = counselorid;
        this.counselorname = counselorname;
    }

    public String getCounselorid() {
        return counselorid;
    }

    public void setCounselorid(String counselorid) {
        this.counselorid = counselorid;
    }

    public String getCounselorname() {
        return counselorname;
    }

    public void setCounselorname(String counselorname) {
        this.counselorname = counselorname;
    }
}
