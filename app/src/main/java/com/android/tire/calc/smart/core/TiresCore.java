package com.android.tire.calc.smart.core;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.Html;
import android.widget.ImageView;
import android.content.Context;
import android.widget.TextView;
import android.util.Log;

import com.android.tire.calc.smart.R;
import com.android.tire.calc.smart.core.aTire;
import com.android.tire.calc.smart.core.aTireSearch;

import java.util.ArrayList;

/**
 * Created by danylenko on 27.07.2015.
 */
public class TiresCore {

    /* new one */

    /*  some   */

    public static final String DS = "tc_debug";
    private static final String SAVE_KEY_ROOT = "TIRE.CALC.SMART";
    private static final String SAVE_KEY_TIRE = "TYRE_SIZE_No_";
    private static final String SAVE_KEY_UNITS = "UNITS";
    private static final String SAVE_KEY_SEARCH_LOCK_WIDTH = "SEARCH_LOCK_WIDTH";
    private static final String SAVE_KEY_SEARCH_LOCK_HEIGHT = "SEARCH_LOCK_HEIGHT";
    private static final String SAVE_KEY_SEARCH_LOCK_RIM = "SEARCH_LOCK_RIM";
    private static final String SAVE_KEY_SEARCH_LOCK_RIM_SIZE = "SEARCH_LOCK_RIM_SIZE";
    private static final String SAVE_KEY_SEARCH_LOCK_OFFROAD = "SEARCH_OFFROAD";

    private static final String SAVE_KEY_PRESETS = "PRESETS";

    public static final int SIZE_TO_SELECT_WIDTH = 0;
    public static final int SIZE_TO_SELECT_HEIGHT = 1;
    public static final int SIZE_TO_SELECT_RIM_D = 2;
    public static final int SIZE_TO_SELECT_RIM_W = 3;
    public static final int SIZE_TO_SELECT_RIM_ET = 4;


    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_UP = 2;
    public static final int DIRECTION_DOWN = 3;
    public static final int DIRECTION_CENTER_H = 4;
    public static final int DIRECTION_CENTER_V = 5;

    public static final float WARNING_HEIGHT_MAX = 50.0f;
    public static final float WARNING_HEIGHT_MIN = -50.0f;
    public static final float WARNING_WIDTH_MAX = 40.0f;

    public static final int SEARCH_STEP_MAX_WIDTH = 1;
    public static final int SEARCH_STEP_MAX_HEIGHT = 1;
    public static final int SEARCH_STEP_MAX_D = 1;

    private static boolean dimensionsMetric = true;

    public static boolean searchLockWidth = false;
    public static boolean searchLockHeight = false;
    public static boolean searchLockRim = true;
    public static int searchLockRimSize = -1;
    public static boolean searchOffroad = false;


    private static ArrayList<String> tiresSaved = new ArrayList<>();
    private static String presetsSplit = ";";


    private static aTire tire1;
    private static aTire tire2;

    public static void setMetric(boolean metric, Context c) {
        dimensionsMetric = metric;

        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SAVE_KEY_UNITS, dimensionsMetric);
        spe.commit();
    }

    public static boolean getMetric(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);
        dimensionsMetric = sp.getBoolean(SAVE_KEY_UNITS, true);

        return dimensionsMetric;
    }

    public static void init(Context c) {
        if(!isLoaded()) {
            loadSearchOptions(c);

            tire1 = new aTire(c, SAVE_KEY_TIRE + "1");
            tire2 = new aTire(c, SAVE_KEY_TIRE + "2");

            //tire1 = new aTire("195/50R15x6.5ET35");
            //tire2 = new aTire(155, 70, 13, 38, 9.0f);

            if(searchLockRimSize == -1) {
                String[] sD = c.getResources().getStringArray(R.array.array_diameter);
                for(int i = 0; i < sD.length; i ++)
                    if(Integer.parseInt(sD[i].substring(1)) == tire1.getD())
                        searchLockRimSize = i;
            }
        }

        dimensionsMetric = getMetric(c);
        tiresSaved = loadPresets(c);

        //Log.d(DS, tire1.toString());
        //Log.d(DS, tire2.toString());
    }

    public static void save(Context c) {
        tire1.saveSize(c, SAVE_KEY_TIRE + "1");
        tire2.saveSize(c, SAVE_KEY_TIRE + "2");

        String out = "";
        for(int i = 0; i < tiresSaved.size(); i ++) {
            out += tiresSaved.get(i);

            if(i < tiresSaved.size() - 1)
                out += presetsSplit;
        }
        savePresets(c, out);
    }

    public static void saveSearchOptions(Context c) {
        //Log.d(DS, "lockW = " + searchLockWidth + ", lockH = " + searchLockHeight + ", lockD = " + searchLockRim + ", off-road = " + searchOffroad);

        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(SAVE_KEY_SEARCH_LOCK_WIDTH, searchLockWidth);
        spe.putBoolean(SAVE_KEY_SEARCH_LOCK_HEIGHT, searchLockHeight);
        spe.putBoolean(SAVE_KEY_SEARCH_LOCK_RIM, searchLockRim);
        spe.putInt(SAVE_KEY_SEARCH_LOCK_RIM_SIZE, searchLockRimSize);
        spe.putBoolean(SAVE_KEY_SEARCH_LOCK_OFFROAD, searchOffroad);
        spe.commit();
    }

    private static void savePresets(Context c, String presets)
    {
        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putString(SAVE_KEY_PRESETS, presets);
        spe.commit();
    }

    public static void loadSearchOptions(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);

        searchLockWidth = sp.getBoolean(SAVE_KEY_SEARCH_LOCK_WIDTH, false);
        searchLockHeight = sp.getBoolean(SAVE_KEY_SEARCH_LOCK_HEIGHT, false);
        searchLockRim = sp.getBoolean(SAVE_KEY_SEARCH_LOCK_RIM, false);
        searchLockRimSize = sp.getInt(SAVE_KEY_SEARCH_LOCK_RIM_SIZE, -1);
        searchOffroad = sp.getBoolean(SAVE_KEY_SEARCH_LOCK_OFFROAD, false);

    }

    private static ArrayList<String> loadPresets(Context c)
    {
        SharedPreferences sp = c.getSharedPreferences(SAVE_KEY_ROOT, c.MODE_PRIVATE);
        String presets = sp.getString(SAVE_KEY_PRESETS, "195/50R15x6.0ET35;205/70R15x6.5ET38;8.0/24.5R16x7.0ET25");

        Log.d(DS, "loading presets = " + presets);

        ArrayList<String> res = new ArrayList<>();

        if(presets != "") {
            String[] tmp = presets.split(presetsSplit);
            for(int i = 0; i < tmp.length; i ++) {
                res.add(tmp[i]);
                Log.d(DS, "loading presets[" + i + "] = " + tmp[i]);
            }
        }
        Log.d(DS, "loading presets count = " + res.size());

        return res;
    }

    private static boolean isLoaded() {
        if(tire1 != null)
            return true;
        else
            return false;
    }

    public static aTire getTire(int i) {
        if(i == 1) return tire1;
        else return tire2;
    }

    public static void setTire2(String srcSize) {
        tire2 = new aTire(srcSize);
    }

    public static ArrayList<aTireSearch> findTireSizeIN(Context c) {
        String[] sD = c.getResources().getStringArray(R.array.array_diameter);
        ArrayList<Integer> tD = new ArrayList<>();
        int iD = -1;
        for(int i = 0; i < sD.length; i ++) {
            tD.add(Integer.parseInt(sD[i].substring(1)));
            if(Integer.parseInt(sD[i].substring(1)) == tire1.getD())
                iD = i;
        }

        float xINCH = 25.4f;
        float inWidth = 0;
        float inWidthReal = 0;
        float inHeight = 0;

        if(!tire1.getInInch()) {
            inWidth = Math.round(2 * tire1.getWidth() / xINCH) / 2f;
            inWidthReal = tire1.getWidth() / xINCH;
            inHeight = Math.round(2 * tire1.getTireOverallHeight() / xINCH) / 2f;
        }
        else {
            inWidth = tire1.getWidth();
            inWidthReal = inWidth;
            inHeight = tire1.getHeight();
        }

        //Log.d(DS, "findTireSizeIN: inWidth = " + inWidth + " (" + tire1.getWidth() / xINCH + "), inHeight = " + inHeight + "");
        ArrayList<aTireSearch> res = new ArrayList<>();

        for(int startD = 0; startD < sD.length; startD ++) {
            float startW = inWidth - 1;

            while (startW <= inWidth + 1) {
                float startH = inHeight - 4;

                while (startH <= inHeight + 4) {
                    float overallHeight = startH;
                    float dOverallHeight = xINCH * (overallHeight - inHeight);
                    float dWidth = xINCH * (startW - inWidthReal);

                    if((dOverallHeight >= WARNING_HEIGHT_MIN) && (dOverallHeight <= WARNING_HEIGHT_MAX))
                    {
                        boolean isAdd = true;

                        if((searchLockWidth) && (startW != inWidth))
                            isAdd = false;

                        if(!searchLockRim)
                            if((startD < iD - 1) || (startD > iD + 1))
                                isAdd = false;

                        if((searchLockRim) && (startD != searchLockRimSize))
                            isAdd = false;

                        if(isAdd)
                            res.add(new aTireSearch(startH + "x" + startW + sD[startD], (int)dWidth, (int)dOverallHeight,
                                    Float.valueOf(startW) + "/" + startH + sD[startD] + "x" + tire1.getRim() + "ET" + tire1.getET()));
                    }
                    startH += 0.5f;
                }
                startW += 0.5f;
            }
        }

        //Log.d(DS, "findTireSizeIN: find count = " + res.size());

        return res;
    }

    public static ArrayList<aTireSearch> findTireSizeMM(Context c) {
        String[] sW = c.getResources().getStringArray(R.array.array_width);
        String[] sH = c.getResources().getStringArray(R.array.array_height);
        String[] sD = c.getResources().getStringArray(R.array.array_diameter);

        ArrayList<Integer> tW = new ArrayList<>();
        ArrayList<Integer> tH = new ArrayList<>();
        ArrayList<Integer> tD = new ArrayList<>();

        float srcOverallHeight = tire1.getTireOverallHeight();
        float xINCH = 25.4f;

        float mmWidth = 0;
        float mmWidthReal = 0;
        if(!tire1.getInInch()) {
            mmWidth = tire1.getWidth();
            mmWidthReal = mmWidth;
        }
        else {
            mmWidth = Math.round(tire1.getWidth() * xINCH / 5) * 5;
            mmWidthReal = tire1.getWidth() * xINCH;
        }

        int iW = -1;
        int iD = -1;

        for(int i = 0; i < sW.length; i ++) {
            tW.add(Integer.parseInt(sW[i]));
            if(Integer.parseInt(sW[i]) == (int)mmWidth)
                iW = i;
        }

        for(int i = 0; i < sH.length; i ++)
            tH.add(Integer.parseInt(sH[i]));

        for(int i = 0; i < sD.length; i++) {
            tD.add(Integer.parseInt(sD[i].substring(1)));
            if(Integer.parseInt(sD[i].substring(1)) == tire1.getD())
                iD = i;
        }

        //Log.d(DS, "findTireSize: W[" + iW + "] = " + sW[iW] /*+ ", H[" + iH + "] = " + sH[iH]*/ + ", D[" + iD + "] = " + sD[iD]);
        ArrayList<aTireSearch> res = new ArrayList<>();

        for(int startD = 0; startD < sD.length; startD ++) {
            int startW = iW - 2;
            if(startW < 0) startW = 0;

            while(startW <= iW + 2) {
                for(int i = 0; i < tH.size(); i ++) {
                    float overallHeight = 2 * tW.get(startW) * tH.get(i) / 100 + xINCH * tD.get(startD);
                    float dOverallHeight = overallHeight - srcOverallHeight;
                    float dWidth = (int)(tW.get(startW) - mmWidthReal);

                    if((dOverallHeight >= WARNING_HEIGHT_MIN) && (dOverallHeight <= WARNING_HEIGHT_MAX))
                    {
                        boolean isAdd = true;

                        if((searchLockWidth) && (startW != iW))
                            isAdd = false;

                        if(!searchLockRim)
                            if((startD < iD - 1) || (startD > iD + 1))
                                isAdd = false;

                        if((searchLockRim) && (startD != searchLockRimSize))
                            isAdd = false;

                        if(isAdd)
                            res.add(new aTireSearch(sW[startW] + "/" + sH[i] + sD[startD], (int)dWidth, (int)dOverallHeight,
                                    Float.valueOf(sW[startW]) + "/" + Float.valueOf(sH[i]) + sD[startD] + "x" + tire1.getRim() + "ET" + tire1.getET()));

                    }
                }
                startW ++;
            }
        }
        //Log.d(DS, "findTireSize: find count = " + res.size());
        return res;
    }

    public static void renderTireInfo(Context c, TextView txt1, TextView txt2, TextView txtInfo) {
        float xINCH = 25.4f;
        boolean isOk = true;
        float dH = tire2.getTireOverallHeight() - tire1.getTireOverallHeight();
        float dW = tire2.getWidthMM() - tire1.getWidthMM();
        float dR = (tire2.getRim() - tire1.getRim()) * xINCH;
        float dET = tire2.getET() - tire1.getET();

        String outI = "";
        String outT1 = "";
        String outT2 = "";

        outI += c.getString(R.string.text_info_tire_width) + "<br>";
        outI += c.getString(R.string.text_info_tire_side_height) + "<br>";
        outI += c.getString(R.string.text_info_overall_height) + "<br>";
        outI += c.getString(R.string.text_info_circumference) + "<br>";
        outI += c.getString(R.string.text_info_clearance) + "<br>";
        outI += c.getString(R.string.text_info_arc_gap) + "<br>";
        outI += c.getString(R.string.text_info_suspension_gap) + "<br>";
        if(dimensionsMetric) {
            outI += c.getString(R.string.text_info_speed_at_100) + "<br>";
            outI += c.getString(R.string.text_info_revs_at_km);// + "\n";
        }
        else {
            outI += c.getString(R.string.text_info_speed_at_60) + "<br>";
            outI += c.getString(R.string.text_info_revs_at_mile);// + "\n";
        }

        outT1 += getSize(tire1.getWidthMM(), c) + "<br>";
        outT1 += getSize(tire1.getTireSide(), c) + "<br>";
        outT1 += getSize(tire1.getTireOverallHeight(), c) + "<br>";
        outT1 += getSize((float)(tire1.getTireOverallHeight() * Math.PI), c) + "<br>";
        outT1 += c.getString(R.string.text_info_clearance_default) + "<br>";
        outT1 += 0 + "<br>";
        outT1 += 0 + "<br>";
        if(dimensionsMetric) {
            outT1 += c.getString(R.string.text_info_tire_speed_default_km) + "<br>";
            outT1 += String.format("%.1f", (float) (1000000 / tire1.getTireOverallHeight() * Math.PI));// + "\n";
        }
        else {
            outT1 += c.getString(R.string.text_info_tire_speed_default_miles) + "<br>";
            outT1 += String.format("%.1f", (float) (63360 / tire1.getTireOverallHeight() * Math.PI * 25.4));// + "\n";
        }

        // tire width
        if((dW > 40))
            outT2 += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" + getSize(tire2.getWidthMM(), c) + "</font><br>";
        else
            outT2 += getSize(tire2.getWidthMM(), c) + "<br>";

        // tire side height
        outT2 += getSize(tire2.getTireSide(), c) + "<br>";

        // tire overall height
        if((dH > 50) || (dH < -50))
            outT2 +=  "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" + getSize(tire2.getTireOverallHeight(), c) + "</font><br>";
        else
            outT2 += getSize(tire2.getTireOverallHeight(), c) + "<br>";

        // wheel circumference
        outT2 += getSize((float) (tire2.getTireOverallHeight() * Math.PI), c) + "<br>";

        // clearance
        if(dH < -50) {
            outT2 += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    getSize(dH / 2, c) + "</font><br>";
        }
        else {
            if(dH > 0)
                outT2 += "+" + getSize(dH / 2, c) + "<br>";
            else
                outT2 += getSize(dH / 2, c) + "<br>";
        }

        // to arch
        //float dArc = (float)Math.sqrt(Math.pow(dH, 2) + Math.pow(dET, 2));// - dET;
        float dArc = dW / 2 - dET;
        if(dArc > 30) {
            outT2 += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    getSize(-1 * dArc, c) + "</font><br>";
        }
        else {
            if(dArc >= 0)
                outT2 += getSize(-1 * dArc, c) + "<br>";
            else
                outT2 += "+" + getSize(-1 * dArc, c) + "<br>";
        }

        // to suspension
        float dSusp = dW / 2 + dET;
        //if(dW > )
        if(dSusp > 30) {
            outT2 += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    getSize(-1 * dSusp, c) + "</font><br>";
        }
        else {
            if(dSusp >= 0)
                outT2 += getSize(-1 * dSusp, c) + "<br>";
            else
                outT2 += "+" + getSize(-1 * dSusp, c) + "<br>";
        }


        // speed at 100kmph & revs per km
        if(dimensionsMetric) {
            outT2 += String.format("%.1f", (float)(100 * tire2.getTireOverallHeight() / tire1.getTireOverallHeight())) + "<br>";
            outT2 += String.format("%.1f", (float)(1000000 / tire2.getTireOverallHeight() * Math.PI));// + "\n";
        }
        else {
            outT2 += String.format("%.1f", (float)(60 * tire2.getTireOverallHeight() / tire1.getTireOverallHeight())) + "<br>";
            outT2 += String.format("%.1f", (float)(63360 / tire2.getTireOverallHeight() * Math.PI * 25.4));// + "\n";
        }

        txtInfo.setText(Html.fromHtml(outI));
        txt1.setText(Html.fromHtml(outT1));
        txt2.setText(Html.fromHtml(outT2));
    }

    public static void renderTips(Context c, TextView txtTyp) {
        boolean isOk = true;
        float dH = tire2.getTireOverallHeight() - tire1.getTireOverallHeight();
        float dW = tire2.getWidthMM() - tire1.getWidthMM();

        String txtT = "";

        //txtT += "<b>" + c.getString(R.string.text_tips) + "</b><br>";


        if(dH > 50) {
            txtT += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    "<b>" + c.getString(R.string.text_tip_to_wheel_arc_title) + " </b>" +
                    c.getString(R.string.text_tip_to_wheel_arc_warning) +
                    "</font><br>";
        }
        else {
            txtT += "<b>" + c.getString(R.string.text_tip_to_wheel_arc_title) + " </b>" +
                    c.getString(R.string.text_tip_to_wheel_arc_ok) + "<br>";
        }

        if(dH < -50) {
            txtT += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    "<b>" + c.getString(R.string.text_tip_clearance_title) + " </b>" +
                    c.getString(R.string.text_tip_clearance_warning) +
                    "</font><br>";
        }
        else {
            txtT += "<b>" + c.getString(R.string.text_tip_clearance_title) + " </b>" +
                    c.getString(R.string.text_tip_clearance_ok) + "<br>";
        }

        if(dW > 40) {
            txtT += "<font color=" + String.format("#%06X", (0xFFFFFF & c.getResources().getColor(R.color.blueprint_dimensions_warning))) + ">" +
                    "<b>" + c.getString(R.string.text_tip_to_suspension_title) + " </b>" +
                    c.getString(R.string.text_tip_to_suspension_warning) +
                    "</font><br>";
        }
        else {
            txtT += "<b>" + c.getString(R.string.text_tip_to_suspension_title) + " </b>" +
                    c.getString(R.string.text_tip_to_suspension_ok) + "<br>";
        }

        txtTyp.setText(Html.fromHtml(txtT));
    }

    public static void renderTiresFace(Context c, ImageView srcImg) {
        float dP = c.getResources().getDisplayMetrics().density;
        int imgW;
        int imgH;
        if(c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            imgW = c.getResources().getDisplayMetrics().widthPixels;
            imgH = imgW;
        }
        else {
            imgH = c.getResources().getDisplayMetrics().heightPixels - (int)(dP * c.getResources().getDimension(R.dimen.app_input_min_height));
            imgW = imgH;
        }
        int cX = imgW/2;
        int cY = imgH/2;
        int cX1 = imgW/4;
        int cX2 = imgW/4 + cX;
        int marg = (int)(c.getResources().getDimension(R.dimen.blueprint_padding));
        int margH = (int)(marg / 2);
        boolean isOk = true;

        Bitmap bitmap = Bitmap.createBitmap(imgW, imgH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float dMax = 0;
        if(tire1.getTireOverallHeight() > tire2.getTireOverallHeight())
            dMax = tire1.getTireOverallHeight();
        else
            dMax = tire2.getTireOverallHeight();

        float scale = (float) (0.7 * imgW / dMax);
        if(tire1.getWidthMM() * scale > 0.6 * imgW / 2)
            scale = (float)(0.6 * imgW / 2 / tire1.getWidthMM());
        if(tire1.getRimMM() * scale > 0.6 * imgW / 2)
            scale = (float)(0.6 * imgW / 2 / tire1.getRimMM());
        if(tire2.getWidthMM() * scale > 0.6 * imgW / 2)
            scale = (float)(0.6 * imgW / 2 / tire2.getWidthMM());
        if(tire2.getRimMM() * scale > 0.6 * imgW / 2)
            scale = (float)(0.6 * imgW / 2 / tire2.getRimMM());

        if(Math.abs(tire1.getTireOverallHeight() - tire2.getTireOverallHeight()) > 50)
            isOk = false;

        float rT1 = tire1.getTireOverallHeight() * scale / 2;
        float rT2 = tire2.getTireOverallHeight() * scale / 2;
        float rR1 = tire1.getRimHeight() * scale / 2;
        float rR2 = tire2.getRimHeight() * scale / 2;
        float wT1 = tire1.getWidthMM() * scale;
        float wT2 = tire2.getWidthMM() * scale;
        float wR1 = tire1.getRimMM() * scale;
        float wR2 = tire2.getRimMM() * scale;
        float oR1 = tire1.getET() * scale;
        float oR2 = tire2.getET() * scale;
        float etPl = 120 * scale;

        float dTW = tire2.getWidthMM() - tire1.getWidthMM();
        float dTS = tire2.getTireSide() - tire1.getTireSide();
        float dTH = tire2.getTireOverallHeight() - tire1.getTireOverallHeight();
        float dRW = tire2.getRimMM() - tire1.getRimMM();
        float dRH = tire2.getRimHeight() - tire1.getRimHeight();
        int dToSuspension = 0;
        int dToFenders = 0;
        int dToArcs = 0;
        int dToGround = 0;


        //----- draw tires side
        RectF tO1 = new RectF(cX - rT1, cY - rT1, cX + rT1, cY + rT1);
        RectF tO2 = new RectF(cX - rT2, cY - rT2, cX + rT2, cY + rT2);
        RectF tI1 = new RectF(cX - rR1, cY - rR1, cX + rR1, cY + rR1);
        RectF tI2 = new RectF(cX - rR2, cY - rR2, cX + rR2, cY + rR2);

        paint.setStrokeWidth(1 * dP);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(c.getResources().getColor(R.color.blueprint_tire_side));
        canvas.drawArc(tO1, 90, 180, false, paint);
        canvas.drawArc(tO2, 270, 180, false, paint);
        canvas.drawArc(tI2, 270, 180, false, paint);
        canvas.drawArc(tI1, 90, 180, false, paint);



        //--------------- draw tires
        float strokeTire = 3 * dP;
        float tRounding = 20 * scale;
        int rounding = (int)(20 * scale);
        Path tph;

        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(0));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeTire);
        paint.setColor(c.getResources().getColor(R.color.blueprint_tire_stroke));


        if(wT1 - wR1 > 0) {
            tph = new Path();
            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2, cY - rR1 - strokeTire - tRounding / 2);
            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2, cY + rR1 + strokeTire + tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);


            tph = new Path();

            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 - strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wT1 / 2 - strokeTire / 2, cY - rR1 - (rT1 - rR1) / 2);
            tph.lineTo(cX1 - wT1 / 2, cY - rT1);
            tph.lineTo(cX1 + wT1 / 2, cY - rT1);
            tph.lineTo(cX1 + wT1 / 2 + strokeTire / 2, cY - rR1 - (rT1 - rR1) / 2);
            tph.lineTo(cX1 + wR1 / 2 + strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire - tRounding / 2);

            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 - strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wT1 / 2 - strokeTire / 2, cY + rR1 + (rT1 - rR1) / 2);
            tph.lineTo(cX1 - wT1 / 2, cY + rT1);
            tph.lineTo(cX1 + wT1 / 2, cY + rT1);
            tph.lineTo(cX1 + wT1 / 2 + strokeTire / 2, cY + rR1 + (rT1 - rR1) / 2);
            tph.lineTo(cX1 + wR1 / 2 + strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire + tRounding / 2);

            paint.setPathEffect(new CornerPathEffect(rounding));
            canvas.drawPath(tph, paint);


            tph = new Path();
            tph.moveTo(cX1 + wR1 / 2, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire + tRounding / 2);
            tph.moveTo(cX1 + wR1 / 2, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire - tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);
        }
        else {
            tph = new Path();
            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);


            tph = new Path();

            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire / 2, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 - wT1 / 2, cY - rT1);
            tph.lineTo(cX1 + wT1 / 2, cY - rT1);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire / 2, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire - tRounding / 2);

            tph.moveTo(cX1 - wR1 / 2 + strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wR1 / 2 + strokeTire / 2, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 - wT1 / 2, cY + rT1);
            tph.lineTo(cX1 + wT1 / 2, cY + rT1);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire / 2, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire + tRounding / 2);

            paint.setPathEffect(new CornerPathEffect(rounding));
            canvas.drawPath(tph, paint);


            tph = new Path();
            tph.moveTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire - tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY - rR1 - strokeTire + tRounding / 2);
            tph.moveTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire + tRounding / 2);
            tph.lineTo(cX1 + wR1 / 2 - strokeTire, cY + rR1 + strokeTire - tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);
        }

        if(wT2 - wR2 > 0) {
            tph = new Path();
            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2, cY - rR2 - strokeTire - tRounding / 2);
            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2, cY + rR2 + strokeTire + tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);


            tph = new Path();

            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 - strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wT2 / 2 - strokeTire / 2, cY - rR2 - (rT2 - rR2) / 2);
            tph.lineTo(cX2 - wT2 / 2, cY - rT2);
            tph.lineTo(cX2 + wT2 / 2, cY - rT2);
            tph.lineTo(cX2 + wT2 / 2 + strokeTire / 2, cY - rR2 - (rT2 - rR2) / 2);
            tph.lineTo(cX2 + wR2 / 2 + strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire - tRounding / 2);

            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 - strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wT2 / 2 - strokeTire / 2, cY + rR2 + (rT2 - rR2) / 2);
            tph.lineTo(cX2 - wT2 / 2, cY + rT2);
            tph.lineTo(cX2 + wT2 / 2, cY + rT2);
            tph.lineTo(cX2 + wT2 / 2 + strokeTire / 2, cY + rR2 + (rT2 - rR2) / 2);
            tph.lineTo(cX2 + wR2 / 2 + strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire + tRounding / 2);

            paint.setPathEffect(new CornerPathEffect(rounding));
            canvas.drawPath(tph, paint);


            tph = new Path();
            tph.moveTo(cX2 + wR2 / 2, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire + tRounding / 2);
            tph.moveTo(cX2 + wR2 / 2, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire - tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);
        }
        else {
            tph = new Path();
            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);


            tph = new Path();

            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire / 2, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 - wT2 / 2, cY - rT2);
            tph.lineTo(cX2 + wT2 / 2, cY - rT2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire / 2, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire - tRounding / 2);

            tph.moveTo(cX2 - wR2 / 2 + strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wR2 / 2 + strokeTire / 2, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 - wT2 / 2, cY + rT2);
            tph.lineTo(cX2 + wT2 / 2, cY + rT2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire / 2, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire + tRounding / 2);

            paint.setPathEffect(new CornerPathEffect(rounding));
            canvas.drawPath(tph, paint);


            tph = new Path();
            tph.moveTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire - tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY - rR2 - strokeTire + tRounding / 2);
            tph.moveTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire + tRounding / 2);
            tph.lineTo(cX2 + wR2 / 2 - strokeTire, cY + rR2 + strokeTire - tRounding / 2);
            paint.setPathEffect(new CornerPathEffect(0));
            canvas.drawPath(tph, paint);
        }




        // ---- draw rims
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeTire);
        paint.setColor(c.getResources().getColor(R.color.blueprint_rim_fill));
        canvas.drawLine(cX1 - wR1 / 2, cY - rR1, cX1 - wR1 / 2, cY + rR1, paint);
        canvas.drawLine(cX1 + wR1 / 2, cY - rR1, cX1 + wR1 / 2, cY + rR1, paint);
        canvas.drawLine(cX2 - wR2 / 2, cY - rR2, cX2 - wR2 / 2, cY + rR2, paint);
        canvas.drawLine(cX2 + wR2 / 2, cY - rR2, cX2 + wR2 / 2, cY + rR2, paint);

        paint.setColor(c.getResources().getColor(R.color.blueprint_rim_stroke));
        Path pth = new Path();
        pth.moveTo(cX1 - wR1 / 2, cY - rR1 - tRounding / 2);
        pth.lineTo(cX1 - wR1 / 2, cY - rR1 + tRounding / 2);
        pth.lineTo(cX1 + wR1 / 2, cY - rR1 + tRounding / 2);
        pth.lineTo(cX1 + wR1 / 2, cY - rR1 - tRounding / 2);

        pth.moveTo(cX1 - wR1 / 2, cY + rR1 + tRounding / 2);
        pth.lineTo(cX1 - wR1 / 2, cY + rR1 - tRounding / 2);
        pth.lineTo(cX1 + wR1 / 2, cY + rR1 - tRounding / 2);
        pth.lineTo(cX1 + wR1 / 2, cY + rR1 + tRounding / 2);

        pth.moveTo(cX2 + wR2 / 2, cY - rR2 - tRounding / 2);
        pth.lineTo(cX2 + wR2 / 2, cY - rR2 + tRounding / 2);
        pth.lineTo(cX2 - wR2 / 2, cY - rR2 + tRounding / 2);
        pth.lineTo(cX2 - wR2 / 2, cY - rR2 - tRounding / 2);

        pth.moveTo(cX2 + wR2 / 2, cY + rR2 + tRounding / 2);
        pth.lineTo(cX2 + wR2 / 2, cY + rR2 - tRounding / 2);
        pth.lineTo(cX2 - wR2 / 2, cY + rR2 - tRounding / 2);
        pth.lineTo(cX2 - wR2 / 2, cY + rR2 + tRounding / 2);

        canvas.drawPath(pth, paint);

        RectF rR;
        pth = new Path();
        float rL = rR1 / 10;

        pth.moveTo(cX1 - oR1, cY - rR1 + tRounding / 2);
        rR = new RectF(cX1 - oR1 - rL, cY - rR1 + tRounding / 2, cX1 - oR1 + rL, cY - etPl / 2);
        pth.arcTo(rR, -90, -180);
        pth.lineTo(cX1 - oR1, cY + etPl / 2);
        rR = new RectF(cX1 - oR1 - rL, cY + etPl / 2, cX1 - oR1 + rL, cY + rR1 - tRounding / 2);
        pth.arcTo(rR, -90, -180);

        rL = rR2 / 10;
        pth.moveTo(cX2 + oR2, cY - rR2 + tRounding / 2);
        rR = new RectF(cX2 + oR2 - rL, cY - rR2 + tRounding / 2, cX2 + oR2 + rL, cY - etPl / 2);
        pth.arcTo(rR, -90, 180);
        pth.lineTo(cX2 + oR2, cY + etPl / 2);
        rR = new RectF(cX2 + oR2 - rL, cY + etPl / 2, cX2 + oR2 + rL, cY + rR2 - tRounding / 2);
        pth.arcTo(rR, -90, 180);


        paint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawPath(pth, paint);



        //--------------- draw dimensions
        paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));
        paint.setStrokeWidth(1 * dP);
        paint.setStyle(Paint.Style.STROKE);

        // ---- draw overall heigth
        canvas.drawLine(cX1, cY - rT1, marg, cY - rT1, paint);
        canvas.drawLine(cX1, cY + rT1, marg, cY + rT1, paint);
        canvas.drawLine(3 * margH, cY - rT1, 3 * margH, cY + rT1, paint);
        drawArrow(3 * margH, cY - rT1, DIRECTION_UP, true, canvas, c);
        drawArrow(3 * margH, cY + rT1, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire1.getTireOverallHeight(), c), 3 * margH, cY, DIRECTION_CENTER_V, true, canvas, c);

        String tireDifference = getSize(dTH, c);
        String tireSideDifference = getSize(dTS, c);
        if((dTH) >= 0) tireDifference = "+" + tireDifference;
        if(dTS >= 0) tireSideDifference = "+" + tireSideDifference;

        canvas.drawLine(cX2, cY - rT2, imgW - margH, cY - rT2, paint);
        canvas.drawLine(cX2, cY + rT2, imgW - margH, cY + rT2, paint);
        if(!isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
        canvas.drawLine(imgW - 2 * margH, cY - rT2, imgW - 2 * margH, cY + rT2, paint);
        drawArrow(imgW - 2 * margH, cY - rT2, DIRECTION_UP, isOk, canvas, c);
        drawArrow(imgW - 2 * margH, cY + rT2, DIRECTION_DOWN, isOk, canvas, c);
        if(dTH != 0)
            drawText(getSize(tire2.getTireOverallHeight(), c) + " (" + tireDifference + ")", imgW - 2 * margH, cY, DIRECTION_CENTER_V, isOk, canvas, c);
        else
            drawText(getSize(tire2.getTireOverallHeight(), c), imgW - 2 * margH, cY, DIRECTION_CENTER_V, isOk, canvas, c);

        paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));

        // ---- draw tire width
        String tireWd = getSize(dTW, c);
        if((dTW) >= 0) tireWd = "+" + tireWd;

        canvas.drawLine(cX1 - wT1 / 2, cY + rT1, cX1 - wT1 / 2, imgH, paint);
        canvas.drawLine(cX1 + wT1 / 2, cY + rT1, cX1 + wT1 / 2, imgH, paint);
        canvas.drawLine(cX1 - wT1 / 2, imgH - margH, cX1 + wT1 / 2, imgH - margH, paint);
        drawArrow(cX1 - wT1 / 2, imgH - margH, DIRECTION_LEFT, true, canvas, c);
        drawArrow(cX1 + wT1 / 2, imgH - margH, DIRECTION_RIGHT, true, canvas, c);
        drawText(getSize(tire1.getWidthMM(), c), cX1, imgH - 2 * margH, DIRECTION_CENTER_H, true, canvas, c);

        canvas.drawLine(cX2 - wT2 / 2, cY + rT2, cX2 - wT2 / 2, imgH, paint);
        canvas.drawLine(cX2 + wT2 / 2, cY + rT2, cX2 + wT2 / 2, imgH, paint);
        canvas.drawLine(cX2 - wT2 / 2, imgH - margH, cX2 + wT2 / 2, imgH - margH, paint);
        drawArrow(cX2 - wT2 / 2, imgH - margH, DIRECTION_LEFT, true, canvas, c);
        drawArrow(cX2 + wT2 / 2, imgH - margH, DIRECTION_RIGHT, true, canvas, c);
        if(dTW != 0)
            drawText(getSize(tire2.getWidthMM(), c) + " (" + tireWd + ")", cX2, imgH - 2 * margH, DIRECTION_CENTER_H, true, canvas, c);
        else
            drawText(getSize(tire2.getWidthMM(), c), cX2, imgH - 2 * margH, DIRECTION_CENTER_H, true, canvas, c);


        // ---- draw rim height
        String rimHd = getSize(dRH, c);
        if(dRH >= 0) rimHd = "+" + rimHd;

        canvas.drawLine(cX1 + wR1 / 2, cY - rR1, cX - margH, cY - rR1, paint);
        canvas.drawLine(cX1 + wR1 / 2, cY + rR1, cX - margH, cY + rR1, paint);
        canvas.drawLine(cX - 2 * margH, cY - rR1, cX - 2 * margH, cY + rR1, paint);
        drawArrow(cX - 2 * margH, cY - rR1, DIRECTION_UP, true, canvas, c);
        drawArrow(cX - 2 * margH, cY + rR1, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire1.getRimHeight(), c), cX - 2 * margH, cY, DIRECTION_CENTER_V, true, canvas, c);

        canvas.drawLine(cX2 - wR2 / 2, cY - rR2, cX + 2 * margH, cY - rR2, paint);
        canvas.drawLine(cX2 - wR2 / 2, cY + rR2, cX + 2 * margH, cY + rR2, paint);
        canvas.drawLine(cX + 3 * margH, cY - rR2, cX + 3 * margH, cY + rR2, paint);
        drawArrow(cX + 3 * margH, cY - rR2, DIRECTION_UP, true, canvas, c);
        drawArrow(cX + 3 * margH, cY + rR2, DIRECTION_DOWN, true, canvas, c);
        if(dRH != 0)
            drawText(getSize(tire2.getRimHeight(), c) + " (" + rimHd + ")", cX + 3 * margH, cY, DIRECTION_CENTER_V, true, canvas, c);
        else
            drawText(getSize(tire2.getRimHeight(), c), cX + 3 * margH, cY, DIRECTION_CENTER_V, true, canvas, c);


        // ---- draw offset (ET)
        canvas.drawLine(cX1, cY - rR1 - margH, cX1, cY + rR1 + margH, paint);
        canvas.drawLine(cX1 - wR1 / 2 - margH, cY, cX1 + wR1 / 2 + margH, cY, paint);
        canvas.drawLine(cX2, cY - rR2 - margH, cX2, cY + rR2 + margH, paint);
        canvas.drawLine(cX2 - wR2 / 2 - margH, cY, cX2 + wR2 / 2 + margH, cY, paint);

        if(tire1.getET() > 0) {
            drawArrow(cX1 - oR1, cY, DIRECTION_RIGHT, true, canvas, c);
            drawArrow(cX1, cY, DIRECTION_LEFT, true, canvas, c);
            drawText(getSize(tire1.getET(), c), cX1 + 4 * margH + margH / 2, cY + margH / 2, DIRECTION_UP, true, canvas, c);
        }
        else {
            drawArrow(cX1 - oR1, cY, DIRECTION_LEFT, true, canvas, c);
            drawArrow(cX1, cY, DIRECTION_RIGHT, true, canvas, c);
            drawText(getSize(tire1.getET(), c), cX1 - 2 * margH, cY + margH / 2, DIRECTION_UP, true, canvas, c);
        }


        if(tire2.getET() < 0) {
            drawArrow(cX2 + oR2, cY, DIRECTION_RIGHT, true, canvas, c);
            drawArrow(cX2, cY, DIRECTION_LEFT, true, canvas, c);
            drawText(getSize(tire2.getET(), c), cX2 + 4 * margH, cY + margH / 2, DIRECTION_UP, true, canvas, c);
        }
        else {
            drawArrow(cX2 + oR2, cY, DIRECTION_LEFT, true, canvas, c);
            drawArrow(cX2, cY, DIRECTION_RIGHT, true, canvas, c);
            drawText(getSize(tire2.getET(), c), cX2 - 2 * margH, cY + margH / 2, DIRECTION_UP, true, canvas, c);
        }


        // ---- draw tire side height
        canvas.drawLine(cX - margH, cY - rT1, cX1, cY - rT1, paint);
        canvas.drawLine(cX - 2 * margH, cY - rR1, cX - 2 * margH, cY - rT1 - 3 * marg, paint);
        drawArrow(cX - 2 * margH, cY - rT1, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire1.getTireSide(), c), cX - 2 * margH, cY - rT1 - 3 * margH, DIRECTION_RIGHT, true, canvas, c);

        canvas.drawLine(cX + 2 * margH, cY - rT2, cX2, cY - rT2, paint);
        canvas.drawLine(cX + 3 * margH, cY - rR2, cX + 3 * margH, cY - rT2 - 3 * marg, paint);
        drawArrow(cX + 3 * margH, cY - rT2, DIRECTION_DOWN, true, canvas, c);
        if(dTS != 0)
            drawText(getSize(tire2.getTireSide(), c) + " (" + tireSideDifference + ")", cX + 3 * margH, cY - rT2 - 3 * margH, DIRECTION_LEFT, true, canvas, c);
        else
            drawText(getSize(tire2.getTireSide(), c), cX + 3 * margH, cY - rT2 - 3 * margH, DIRECTION_LEFT, true, canvas, c);


        // ---- draw rims width
        String rimWd = getSize(dRW, c);
        if(dRW >= 0) rimWd = "+" + rimWd;

        canvas.drawLine(cX1 - wR1 / 2 - 3 * margH, cY + rR1 / 2 + etPl / 4, cX1 + wR1 / 2 + 3 * margH, cY + rR1 / 2 + etPl / 4, paint);
        drawArrow(cX1 - wR1 / 2, cY + rR1 / 2 + etPl / 4, DIRECTION_RIGHT, true, canvas, c);
        drawArrow(cX1 + wR1 / 2, cY + rR1 / 2 + etPl / 4, DIRECTION_LEFT, true, canvas, c);
        drawText(getSize(tire1.getRimMM(), c), cX1, cY + rR1 / 2 + etPl / 4 - margH, DIRECTION_CENTER_H, true, canvas, c);

        canvas.drawLine(cX2 - wR2 / 2 - 3 * margH, cY + rR2 / 2 + etPl / 4, cX2 + wR2 / 2 + 3 * margH, cY + rR2 / 2 + etPl / 4, paint);
        drawArrow(cX2 - wR2 / 2, cY + rR2 / 2 + etPl / 4, DIRECTION_RIGHT, true, canvas, c);
        drawArrow(cX2 + wR2 / 2, cY + rR2 / 2 + etPl / 4, DIRECTION_LEFT, true, canvas, c);
        if(dRW != 0)
            drawText(getSize(tire2.getRimMM(), c) + " (" + rimWd + ")", cX2, cY +  rR2 / 2 + etPl / 4 - margH, DIRECTION_CENTER_H, true, canvas, c);
        else
            drawText(getSize(tire2.getRimMM(), c), cX2, cY +  rR2 / 2 + etPl / 4 - margH, DIRECTION_CENTER_H, true, canvas, c);

        //--------------- draw axes
        paint.setColor(c.getResources().getColor(R.color.blueprint_axes));
        paint.setStrokeWidth(dP);
        canvas.drawLine(imgW / 2, 0, imgW / 2, imgH, paint);

        srcImg.setImageBitmap(bitmap);
    }

    public static void renderTiresSide(Context c, ImageView srcImg) {
        float dP = c.getResources().getDisplayMetrics().density;
        int imgW;
        int imgH;
        if(c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            imgW = c.getResources().getDisplayMetrics().widthPixels;
            imgH = imgW;
        }
        else {
            imgH = c.getResources().getDisplayMetrics().heightPixels - (int)(dP * c.getResources().getDimension(R.dimen.app_input_min_height));
            imgW = imgH;
        }
        int cX = imgW/2;
        int cY = imgH/2;
        int marg = (int)(dP * c.getResources().getDimension(R.dimen.app_marging_main));
        int margH = (int)(marg / 2);
        boolean isOk = true;
        //Log.d(DS, "screen width=" + imgW + ", denesty=" + dP);

        Bitmap bitmap = Bitmap.createBitmap(imgW, imgH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float dMax = 0;
        if(tire1.getTireOverallHeight() > tire2.getTireOverallHeight())
            dMax = tire1.getTireOverallHeight();
        else
            dMax = tire2.getTireOverallHeight();

        float scale = (float) (0.8 * imgW / dMax);

        if(Math.abs(tire1.getTireOverallHeight() - tire2.getTireOverallHeight()) > 50)
            isOk = false;

        int rT1 = (int) (tire1.getTireOverallHeight() * scale / 2);
        int rT2 = (int) (tire2.getTireOverallHeight() * scale / 2);
        int rR1 = (int) (tire1.getRimHeight() * scale / 2);
        int rR2 = (int) (tire2.getRimHeight() * scale / 2);

        //----- draw tires
        RectF tO1 = new RectF();
        RectF tO2 = new RectF();
        RectF tI1 = new RectF();
        RectF tI2 = new RectF();

        tO1.set(cX - rT1, cY - rT1, cX + rT1, cY + rT1);
        tO2.set(cX - rT2, cY - rT2, cX + rT2, cY + rT2);
        tI1.set(cX - rR1, cY - rR1, cX + rR1, cY + rR1);
        tI2.set(cX - rR2, cY - rR2, cX + rR2, cY + rR2);

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(c.getResources().getColor(R.color.blueprint_tire_fill));
        canvas.drawArc(tO1, 90, 180, false, paint);
        canvas.drawArc(tO2, 270, 180, false, paint);

        paint.setColor(c.getResources().getColor(R.color.blueprint_background));
        canvas.drawArc(tI2, 270, 180, false, paint);
        canvas.drawArc(tI1, 90, 180, false, paint);

        paint.setStrokeWidth(3 * dP);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(c.getResources().getColor(R.color.blueprint_tire_stroke));
        canvas.drawArc(tO1, 90, 180, false, paint);
        canvas.drawArc(tO2, 270, 180, false, paint);
        canvas.drawArc(tI2, 270, 180, false, paint);
        canvas.drawArc(tI1, 90, 180, false, paint);

        //--------------- draw dimensions
        paint.setStrokeWidth(dP);
        paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));

        //----- tire dimens overall
        canvas.drawLine(cX, cY - rT1, marg, cY - rT1, paint);
        canvas.drawLine(cX, cY + rT1, marg, cY + rT1, paint);
        canvas.drawLine(3 * margH, cY - rT1 - 2 * marg, 3 * margH, cY + rT1, paint);
        drawArrow(3 * margH, cY - rT1, DIRECTION_UP, true, canvas, c);
        drawArrow(3 * margH, cY + rT1, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire1.getTireOverallHeight(), c), 3 * margH, cY - rT1 - marg, DIRECTION_LEFT, true, canvas, c);


        String tireDifference = getSize(tire2.getTireOverallHeight() - tire1.getTireOverallHeight(), c);
        if((tire2.getTireOverallHeight() - tire1.getTireOverallHeight()) >= 0) tireDifference = "+" + tireDifference;

        if(!isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
        canvas.drawLine(cX, cY - rT2, imgW - marg, cY - rT2, paint);
        canvas.drawLine(cX, cY + rT2, imgW - marg, cY + rT2, paint);
        canvas.drawLine(imgW - 3 * margH, cY - rT2 - 2 * marg, imgW - 3 * margH, cY + rT2, paint);
        drawArrow(imgW - 3 * margH, cY - rT2, DIRECTION_UP, isOk, canvas, c);
        drawArrow(imgW - 3 * margH, cY + rT2, DIRECTION_DOWN, isOk, canvas, c);
        drawText(getSize(tire2.getTireOverallHeight(), c) + " (" + tireDifference + ")", imgW - 3 * margH, cY - rT2 - marg, DIRECTION_RIGHT, isOk, canvas, c);

        //----- tire dimens side height
        paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));
        canvas.drawLine(cX, cY + rR1, cX - rT1, cY + rR1, paint);
        canvas.drawLine(cX - rT1 + margH, cY + rR1, cX - rT1 + margH, cY + rT1 + 2 * marg, paint);
        drawArrow(cX - rT1 + margH, cY + rR1, DIRECTION_UP, true, canvas, c);
        drawArrow(cX - rT1 + margH, cY + rT1, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire1.getTireSide(), c), cX - rT1 + margH, cY + rT1 + 3 * margH, DIRECTION_LEFT, true, canvas, c);

        canvas.drawLine(cX, cY + rR2, cX + rT2, cY + rR2, paint);
        canvas.drawLine(cX + rT2 - margH, cY + rR2, cX + rT2 - margH, cY + rT2 + 2 * marg, paint);
        drawArrow(cX + rT2 - margH, cY + rR2, DIRECTION_UP, true, canvas, c);
        drawArrow(cX + rT2 - margH, cY + rT2, DIRECTION_DOWN, true, canvas, c);
        drawText(getSize(tire2.getTireSide(), c), cX + rT2 - margH, cY + rT2 + 3 * margH, DIRECTION_RIGHT, true, canvas, c);

        //----- rim dimens
        canvas.drawLine(cX - marg, cY, cX - rR1, cY, paint);
        canvas.drawLine(cX + marg, cY, cX + rR2, cY, paint);
        drawArrow(cX - rR1, cY, DIRECTION_LEFT, true, canvas, c);
        drawArrow(cX + rR2, cY, DIRECTION_RIGHT, true, canvas, c);
        drawText("ø" + getSize(tire1.getRimHeight(), c), cX, cY - marg, DIRECTION_RIGHT, true, canvas, c);
        drawText("ø" + getSize(tire2.getRimHeight(), c), cX, cY - marg, DIRECTION_LEFT, true, canvas, c);


        //--------------- draw axes
        paint.setColor(0xff555555);
        paint.setStrokeWidth(dP);
        canvas.drawLine(imgW / 2, 0, imgW / 2, imgH, paint);

        srcImg.setImageBitmap(bitmap);
    }

    private static void drawText(String txt, float x, float y, int direction, boolean isOk, Canvas canvas, Context c) {
        float dP = c.getResources().getDisplayMetrics().density;
        int marg = (int)(dP * c.getResources().getDimension(R.dimen.app_marging_main) / 2);
        int txtSize = (int) c.getResources().getDimension(R.dimen.app_text_img_size);
        int lift = txtSize / 4;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(txtSize);


        if(direction == DIRECTION_LEFT) {
            paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_shadow));
            canvas.drawText(txt, x + marg, y + lift + 1, paint);

            if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_text));
            else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
            canvas.drawText(txt, x + marg, y + lift, paint);
        }
        else if(direction == DIRECTION_RIGHT) {
            int txtW = (int) paint.measureText(txt);

            paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_shadow));
            canvas.drawText(txt, x - txtW - marg, y + lift + 1, paint);

            if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));
            else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
            canvas.drawText(txt, x - txtW - marg, y + lift, paint);
        }
        else if(direction == DIRECTION_CENTER_H) {
            int txtW = (int) paint.measureText(txt);

            paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_shadow));
            canvas.drawText(txt, x - txtW / 2, y + lift + 1, paint);

            if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_text));
            else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
            canvas.drawText(txt, x - txtW / 2, y + lift, paint);
        }
        else if(direction == DIRECTION_CENTER_V) {
            int txtW = (int) paint.measureText(txt);
            canvas.save();
            canvas.rotate(-90, canvas.getWidth() / 2, canvas.getHeight() / 2);

            paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_shadow));
            canvas.drawText(txt, y - txtW / 2, x - lift + 1, paint);

            if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_text));
            else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
            canvas.drawText(txt, y - txtW / 2, x - lift,  paint);

            canvas.restore();
        }
        else if(direction == DIRECTION_UP) {
            canvas.save();
            canvas.rotate(-90, canvas.getWidth() / 2, canvas.getHeight() / 2);

            paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_shadow));
            canvas.drawText(txt, y, x - lift + 1, paint);

            if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_text));
            else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
            canvas.drawText(txt, y, x - lift,  paint);

            canvas.restore();
        }
    }

    private static void drawArrow(float x, float y, int directon, boolean isOk, Canvas canvas, Context c) {

        float dP = c.getResources().getDisplayMetrics().density;
        int marg = (int)(c.getResources().getDimension(R.dimen.blueprint_arrow_size));

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(dP);
        if(isOk) paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions));
        else paint.setColor(c.getResources().getColor(R.color.blueprint_dimensions_warning));
        paint.setStyle(Paint.Style.FILL);

        Path p = new Path();
        p.moveTo(x, y);

        if(directon == DIRECTION_UP) {
            p.lineTo(x - marg, y + 3 * marg);
            p.lineTo(x + marg, y + 3 * marg);
        }
        else if(directon == DIRECTION_DOWN) {
            p.lineTo(x - marg, y - 3 * marg);
            p.lineTo(x + marg, y - 3 * marg);
        }
        else if(directon == DIRECTION_LEFT) {
            p.lineTo(x + 3 * marg, y - marg);
            p.lineTo(x + 3 * marg, y + marg);
        }
        else if(directon == DIRECTION_RIGHT) {
            p.lineTo(x - 3 * marg, y - marg);
            p.lineTo(x - 3 * marg, y + marg);
        }

        p.lineTo(x, y);
        canvas.drawPath(p, paint);
    }

    private static String getSize(float value, Context c) {
        if(dimensionsMetric)
            return String.format("%.0f", (float)Math.round(value));
        else
            return String.format("%.1f", (float)Math.round(10 * value / 25.4) / 10) + "\"";
    }

    public static String getSizeInInch(float value, Context c) {
        return String.format("%.1f", Math.round(10 * value) / 10f) + "\"";
    }

    public static boolean isWidthFit(float rimWidth, float tireWidth, Context c)
    {
        String[] wW = c.getResources().getStringArray(R.array.array_width);
        String[] rW = c.getResources().getStringArray(R.array.array_rim);

        for(int i = 0; i < rW.length; i ++)
            if(Float.valueOf(rW[i]) == rimWidth)
                if((Integer.valueOf(wW[i]) == tireWidth) ||
                   (Integer.valueOf(wW[i + 1]) == tireWidth) ||
                   (Integer.valueOf(wW[i + 2]) == tireWidth) ||
                   (Integer.valueOf(wW[i + 3]) == tireWidth)) {
                    return true;
                }

        return false;
    }

    public static void addPreset(String size) {
        tiresSaved.add(size);
    }

    public static void deletePreset(int postion) {
        if(postion < tiresSaved.size())
            tiresSaved.remove(postion);
    }

    public static ArrayList<aTirePreset> getPresets() {
        ArrayList<aTirePreset> res = new ArrayList<>();

        for(int i = 0; i < tiresSaved.size(); i ++) {
            res.add(new aTirePreset(tiresSaved.get(i), false));
        }

        return res;
    }
}
