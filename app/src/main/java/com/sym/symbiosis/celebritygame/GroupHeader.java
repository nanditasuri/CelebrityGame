package com.sym.symbiosis.celebritygame;

/**
 * Created by symbiosis on 5/15/2015.
 */
public class GroupHeader {
    private String mGrpImage;
    private String mGrpName;
    private Boolean mGrpDone;

    public GroupHeader(String mGrpImage, String mGrpName, Boolean mGrpDone) {
        this.mGrpImage = mGrpImage;
        this.mGrpName = mGrpName;
        this.mGrpDone = mGrpDone;
    }

    public String getmGrpImage() {
        return mGrpImage;
    }

    public void setmGrpImage(String mGrpImage) {
        this.mGrpImage = mGrpImage;
    }

    public String getmGrpName() {
        return mGrpName;
    }

    public void setmGrpName(String mGrpName) {
        this.mGrpName = mGrpName;
    }

    public Boolean getmGrpDone() {
        return mGrpDone;
    }

    public void setmGrpDone(Boolean mGrpDone) {
        this.mGrpDone = mGrpDone;
    }

    @Override
    public String toString() {
        return "GroupHeader{" +
                "mGrpImage='" + mGrpImage + '\'' +
                ", mGrpName='" + mGrpName + '\'' +
                ", mGrpDone=" + mGrpDone +
                '}';
    }
}
