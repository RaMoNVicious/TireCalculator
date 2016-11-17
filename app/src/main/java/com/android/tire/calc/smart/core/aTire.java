package com.android.tire.calc.smart.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Locale;

/**
 * Created by danylenko on 27.07.2015.
 */
public class aTire {
    private float Width;
    private float Height;
    private int D;
    private int ET;
    private float Rim;
    private boolean inInch;

    private static final float xINCH = 25.4f;

    public aTire(int srcWidth, int srcHeight, int srcD, int srcET, float srcRim, boolean srcInInch) {
        this.Width = srcWidth;
        this.Height = srcHeight;
        this.D = srcD;
        this.ET = srcET;
        this.Rim = srcRim;
        this.inInch = srcInInch;
    }

    public aTire(String srcSize) {
        this.Width = Float.parseFloat(srcSize.substring(0, srcSize.indexOf("/")));
        this.Height = Float.parseFloat(srcSize.substring(srcSize.indexOf("/") + 1, srcSize.indexOf("R")));
        this.D = Integer.parseInt(srcSize.substring(srcSize.indexOf("R") + 1, srcSize.indexOf("x")));
        this.Rim = Float.parseFloat(srcSize.substring(srcSize.indexOf("x") + 1, srcSize.indexOf("ET")));
        this.ET = Integer.parseInt(srcSize.substring(srcSize.indexOf("ET") + 2));

        if(this.Width < 150) this.inInch = true;
            else this.inInch = false;
    }

    public aTire(Context c, String key) {
        SharedPreferences sp = c.getSharedPreferences("TIRE.CALC.SMART", c.MODE_PRIVATE);
        String size = "";

        if(key.endsWith("1"))
            size = sp.getString(key, "195/65R15x6.5ET35");
        else
            size = sp.getString(key, "205/60R15x7.0ET20");

        //Log.d(TiresCore.DS, "Loaded Tire = " + size);

        if((size.length() == 0) || (size.indexOf("x") == -1) || (size.indexOf("ET") == -1) || (size.indexOf("R") == -1) || (size.indexOf("/") == -1))
            size = "235/50R17x7.5ET25";

        this.Width = Float.parseFloat(size.substring(0, size.indexOf("/")));
        this.Height = Float.parseFloat(size.substring(size.indexOf("/") + 1, size.indexOf("R")));
        this.D = Integer.parseInt(size.substring(size.indexOf("R") + 1, size.indexOf("x")));
        this.Rim = Float.parseFloat(size.substring(size.indexOf("x") + 1, size.indexOf("ET")));
        this.ET = Integer.parseInt(size.substring(size.indexOf("ET") + 2));

        if(this.Width < 150) this.inInch = true;
            else this.inInch = false;
    }

    public void saveSize(Context c, String key) {
        SharedPreferences sp = c.getSharedPreferences("TIRE.CALC.SMART", c.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(key, toSizeString());
        spe.commit();
    }

    public void setWidth(int srcWidth) {
        this.Width = srcWidth;
    }

    public void setWidth(float srcSize, boolean srcInInch) {
        this.Width = srcSize;

        if(inInch != srcInInch) {
            float tmpHeight = 0;

            if(srcInInch) {
                tmpHeight = 0.5f * (int)(2 * (2 * Width * xINCH * Height / 100 + D * xINCH) / xINCH);
            }
            else {
                tmpHeight = 5 * Math.round(100 * xINCH * (Height - D) / 2 / Width / 5);
            }

            setHeight(tmpHeight);
        }

        this.inInch = srcInInch;
    }

    public void setHeight(float srcHeight) {
        this.Height = srcHeight;
    }

    public void setHeight(float srcSize, boolean srcInInch) {
        this.Height = srcSize;

        if(inInch != srcInInch) {
            float tmpWidth = 0;

            if(srcInInch)
                tmpWidth = Math.round(Width / xINCH * 2) / 2;
            else
                tmpWidth = Math.round(xINCH * Width / 5) * 5;

            setWidth((int)tmpWidth);
        }

        this.inInch = srcInInch;
    }

    public void setD(int srcD) {
        this.D = srcD;
    }

    public void setRim(float srcRim) {
        this.Rim = srcRim;
    }

    public void setET(int srcET) {
        this.ET = srcET;
    }

    public boolean getInInch() {
        return inInch;
    }

    public float getWidth() {
        return Width;
    }

    public float getWidthMM() {
        if(inInch)
            return Width * xINCH;
        else
            return Width;
    }

    public float getHeight() {
        return Height;
    }

    public int getD() {
        return D;
    }

    public int getET() {
        return ET;
    }

    public float getRim() {
        return Rim;
    }

    public int getRimMM() {
        return (int)(xINCH * Rim);
    }

    public float getRimHeight() {
        return D * xINCH;
    }

    public float getTireSide() {
        if(!inInch)
            return (float)(getWidth() * getHeight()) / 100;
        else
            return (float) (getTireOverallHeight() - xINCH * getD()) / 2;
    }

    public float getTireOverallHeight() {
        if(!inInch)
            return xINCH * D + 2 * getTireSide();
        else
            return xINCH * getHeight();
    }

    public String getTireLabel() {
        if(!inInch)
            return getWidthString() + "/" + getHeightString() + " R" + D;
        else
            return getHeightString() + "x" + getWidthString() + " R" + D;
    }

    public String getRimLabel() {
        return String.format(Locale.ENGLISH, "%.0f", (float) D)/* + "\""*/ + "x" + String.format(Locale.ENGLISH, "%.1f", (float) Rim)/* + "\""*/ + " ET" + ET;
    }

    public String toString() {
        return "Tire params: W: " + Width +
                ", H: " + Height +
                ", D: " + D +
                ", ET: " + ET +
                ", Rim: " + Rim +
                ", Side H: " + getTireSide() +
                ", Overall H: " + getTireOverallHeight() +
                ", inInch: " + inInch;
    }

    public String toSizeString() {
        return String.valueOf(getWidth()) + "/" + String.valueOf(getHeight()) + "R" + getD() + "x" + getRim() + "ET" + getET();
    }

    public String getWidthString() {
        if(!inInch)
            return String.format(Locale.ENGLISH, "%.0f", (float)Math.round(Width));
        else
            return String.format(Locale.ENGLISH, "%.1f", Width);// + "\"";
    }

    public String getHeightString() {
        if(!inInch)
            return String.format(Locale.ENGLISH, "%.0f", (float)Math.round(Height));
        else
            return String.format(Locale.ENGLISH, "%.1f", Height);// + "\"";
    }
}
