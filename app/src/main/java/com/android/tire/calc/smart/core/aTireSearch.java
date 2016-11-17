package com.android.tire.calc.smart.core;

/**
 * Created by ramon on 05.02.16.
 */
public class aTireSearch {
    private String LabelTire = "";
    private int TireDifferenceW = 0;
    private int TireDifferenceH = 0;
    private String Size = "";

    public aTireSearch(String srcLabelTire, int srcLabelTireDifferenceW, int srcLabelTireDifferenceH, String srcSize) {
        this.LabelTire = srcLabelTire;
        this.TireDifferenceW = srcLabelTireDifferenceW;
        this.TireDifferenceH = srcLabelTireDifferenceH;
        this.Size = srcSize;
    }

    public String getLabelTire() {
        return this.LabelTire;
    }

    public float getTireDifferenceW() {
        return this.TireDifferenceW;
    }

    public float getTireDifferenceH() {
        return  TireDifferenceH;
    }

    public String getSize() {
        return this.Size;
    }
}
