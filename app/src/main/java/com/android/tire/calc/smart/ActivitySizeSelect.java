package com.android.tire.calc.smart;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.tire.calc.smart.core.TiresCore;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class ActivitySizeSelect extends AppCompatActivity {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    ActionBar aBar;
    int sizeToSelect = -1;
    boolean isInSize = false;
    float sizeSrc = 0;
    int rimSrc = 0;
    float rimWSrc = 0;
    float widthSrc = 0;

    float sizeTmp = 0;
    boolean isInTmp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_select);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-73274611-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);



        aBar = getSupportActionBar();
        aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sizeToSelect = extras.getInt("SIZE_TYPE");
            sizeSrc = extras.getFloat("SIZE");
            rimSrc = extras.getInt("RIM");
            rimWSrc = extras.getFloat("RIM_WIDTH");
            widthSrc = extras.getFloat("WIDTH");
            isInSize = extras.getBoolean("INCHES");

            sizeTmp = sizeSrc;
            isInTmp = isInSize;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(sizeToSelect == TiresCore.SIZE_TO_SELECT_WIDTH)
            this.setTitle(getString(R.string.dialog_caption_select_width));
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_HEIGHT)
            this.setTitle(getString(R.string.dialog_caption_select_height));
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_D)
            this.setTitle(getString(R.string.dialog_caption_select_diameter));
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_W)
            this.setTitle(getString(R.string.dialog_caption_select_rim));
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_ET)
            this.setTitle(getString(R.string.dialog_caption_select_offset));

        //Log.d(TiresCore.DS, "SizeSelect: SIZE_TYPE = " + sizeToSelect + ", SIZE = " + sizeTmp + ", RIM = " + rimSrc + ", RIM_WIDTH = " + rimWSrc + ", WIDTH = " + widthSrc + ", INCHES = " + isInTmp);

        if(sizeToSelect != -1)
            render();
    }

    private void addItem(LinearLayout out, String txt, boolean isLeft, int h, boolean singleLine) {
        //Log.d(TiresCore.DS, "addItem: txt = " + txt + ", singleLine = " + singleLine);
        float dP = getResources().getDisplayMetrics().density;
        float xINCH = 25.4f;

        boolean isHighlight = true;
        if(sizeToSelect == TiresCore.SIZE_TO_SELECT_WIDTH) {
            float tmpW = Float.valueOf(txt.replace("\"", "").replace(",", "."));

            if(tmpW < 155) {
                tmpW = (float) (Math.ceil(tmpW * xINCH / 5) * 5);
            }
            else
                tmpW = Float.valueOf(txt);

            //isHighlight = TiresCore.isWidthFit(rimWSrc, tmpW, this);
        }
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_W) {
            float tmpW = widthSrc;
            if(tmpW < 155) {
                tmpW = widthSrc;
                tmpW = (float) (Math.ceil(tmpW * xINCH / 5) * 5);
            }

            //isHighlight = TiresCore.isWidthFit(Float.valueOf(txt), tmpW, this);
        }


        LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        int pnlPad = (int)(getResources().getDimension(R.dimen.app_marging_main));

        TextView txtValue = new TextView(this);
        TextView txtLine = new TextView(this);

        txtValue.setLayoutParams(txtParam);
        txtValue.setPadding(pnlPad, 0, pnlPad, 0);
        if(isHighlight)
            txtValue.setTextColor(getResources().getColor(R.color.blueprint_dimensions_text));
        else
            txtValue.setTextColor(getResources().getColor(R.color.blueprint_tire_fill));
        txtValue.setTextSize(getResources().getDimension(R.dimen.size_select_item_text_size) / dP);
        txtValue.setText(txt);
        txtValue.setHeight(h);

        txtLine.setLayoutParams(lineParam);
        txtLine.setPadding(pnlPad, pnlPad, pnlPad, pnlPad);
        txtValue.setHeight(h);

        if(isLeft) {
            txtValue.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            txtLine.setGravity(Gravity.CENTER_VERTICAL);

            txtLine.setBackgroundResource(R.drawable.line);
            //txtLine.setBackgroundColor(Color.RED);

            out.addView(txtValue);
            out.addView(txtLine);
        }
        else  {
            txtValue.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            txtLine.setGravity(Gravity.CENTER_VERTICAL);

            txtLine.setBackgroundResource(R.drawable.line);
            //txtLine.setBackgroundColor(Color.RED);

            if(singleLine) {
                int space = 0;

                if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    space = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                else
                    space = getApplicationContext().getResources().getDisplayMetrics().heightPixels - (int)(dP * getApplicationContext().getResources().getDimension(R.dimen.app_input_min_height));


                LinearLayout pnlSpace = new LinearLayout(this);
                pnlSpace.setLayoutParams(lineParam);
                pnlSpace.setMinimumWidth(space / 3);

                LinearLayout pnlLineV = new LinearLayout(this);
                pnlLineV.setLayoutParams(lineParam);
                pnlLineV.setBackgroundColor(getResources().getColor(R.color.blueprint_axes));
                pnlLineV.setMinimumWidth((int)getResources().getDimension(R.dimen.size_line_vertical_width) / 2);

                out.addView(pnlSpace);
                out.addView(pnlLineV);
            }

            out.addView(txtLine);
            out.addView(txtValue);
        }
    }

    private void render() {
        float xINCH = 25.4f;
        float dP = getResources().getDisplayMetrics().density;

        final ScrollView scrSize = (ScrollView)findViewById(R.id.scrlSizes);
        final LinearLayout pnlSize = (LinearLayout)findViewById(R.id.pnlSizes);
        pnlSize.removeAllViews();

        String[] strS = null;

        if((sizeToSelect == TiresCore.SIZE_TO_SELECT_WIDTH) ||
           (sizeToSelect == TiresCore.SIZE_TO_SELECT_HEIGHT)) {

            float mmH = 0;
            float inH = 0;
            float mmFirst = 0;
            float inFirst = 0;
            float dFirst = 0;

            int selected = 0;

            if(sizeToSelect == TiresCore.SIZE_TO_SELECT_WIDTH) {
                strS = this.getApplicationContext().getResources().getStringArray(R.array.array_width);

                mmH = getResources().getDimension(R.dimen.size_select_text_size) * 2;
                inH = mmH * xINCH / 10;

                mmFirst = Float.valueOf(strS[0]);
                inFirst = Math.round(mmFirst / xINCH * 2) / 2;

                dFirst = (mmH - inH / 2.0f) / 2.0f - (mmFirst - inFirst * xINCH) * mmH / 10;
            }
            else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_HEIGHT) {
                strS = this.getApplicationContext().getResources().getStringArray(R.array.array_height);

                inH = 2 * getResources().getDimension(R.dimen.size_select_text_size) * 2;
                if(isInTmp) {
                    mmH = (2 * 5 * widthSrc * xINCH / 100) / xINCH * inH;
                    mmFirst = 2 * Float.valueOf(strS[0]) * widthSrc * xINCH / 100 + rimSrc * xINCH;
                    inFirst = Math.round(mmFirst / xINCH * 2) / 2;
                    dFirst = ((inFirst * xINCH) - (2 * Float.valueOf(strS[0]) * widthSrc * xINCH / 100 + rimSrc * xINCH)) / xINCH * inH / 2;
                }
                else {
                    mmH = (2 * 5 * widthSrc / 100) / xINCH * inH;
                    mmFirst = 2 * Float.valueOf(strS[0]) * widthSrc / 100 + rimSrc * xINCH;
                    inFirst = Math.round(mmFirst / xINCH * 2) / 2;
                    dFirst = ((inFirst * xINCH) - (2 * Float.valueOf(strS[0]) * widthSrc / 100 + rimSrc * xINCH)) / xINCH * inH / 2;
                }
            }


            //Log.d(TiresCore.DS, "mmH = " + mmH + ", inH = " + inH + ", mmFirst = " + mmFirst + ", inFirst = " + inFirst + ", dFirst = " + dFirst);


            TextView dText = new TextView(this);
            dText.setHeight((int) Math.abs(dFirst));
            dText.setBackgroundColor(getResources().getColor(R.color.app_background));

            pnlSize.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            LinearLayout.LayoutParams pnlParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            LinearLayout pnlMM = new LinearLayout(this);
            LinearLayout pnlIn = new LinearLayout(this);
            LinearLayout pnlLine = new LinearLayout(this);

            pnlMM.setLayoutParams(pnlParam);
            pnlIn.setLayoutParams(pnlParam);
            pnlMM.setOrientation(LinearLayout.VERTICAL);
            pnlIn.setOrientation(LinearLayout.VERTICAL);

            pnlLine.setLayoutParams(lineParam);
            pnlLine.setOrientation(LinearLayout.VERTICAL);
            pnlLine.setBackgroundColor(getResources().getColor(R.color.blueprint_axes));
            pnlLine.setMinimumWidth((int)dP);//(int)getResources().getDimension(R.dimen.size_line_vertical_width) / 1);
            //pnlLine.setPadding(0, 0, 0, 0);


            if(dFirst < 0)
                pnlMM.addView(dText);
            else
                pnlIn.addView(dText);

            for(int i = 0; i < strS.length; i++) {
                final String sizeMM = strS[i];
                int realH = (int)mmH;
                if(i > 0)
                    realH = Math.round(mmH * (float)i) - Math.round(mmH * (float)(i - 1));


                final LinearLayout pnlItem = new LinearLayout(this);
                pnlItem.setOrientation(LinearLayout.HORIZONTAL);
                addItem(pnlItem, sizeMM, true, realH, false);

                if(sizeTmp == Float.valueOf(sizeMM)) {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                    pnlItem.setTag("+");

                    selected = (int)(i * mmH);
                }
                else {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.app_background));
                    pnlItem.setTag("");
                }

                pnlMM.addView(pnlItem);

                pnlItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!pnlItem.getTag().toString().equalsIgnoreCase("+")) {
                            LinearLayout oldSelection = (LinearLayout)pnlSize.findViewWithTag("+");
                            oldSelection.setBackgroundColor(getResources().getColor(R.color.app_background));
                            oldSelection.setTag("");

                            pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                            pnlItem.setTag("+");
                        }

                        selectSize(sizeMM);
                    }});
            }

            float inValue = inFirst;
            double inEnd = 0;

            if(sizeToSelect == TiresCore.SIZE_TO_SELECT_WIDTH) {
                inEnd = Float.valueOf(strS[strS.length - 1]) / xINCH;
                //Log.d(TiresCore.DS, "inEnd = " + inEnd + " (" + strS[strS.length - 1] + "mm) from " + Float.valueOf(strS[strS.length - 1]) / xINCH);
            }
            else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_HEIGHT) {
                if(isInTmp)
                    inEnd = (2 * Float.valueOf(strS[strS.length - 1]) * widthSrc * xINCH / 100 + rimSrc * xINCH) / xINCH;
                else
                    inEnd = (2 * Float.valueOf(strS[strS.length - 1]) * widthSrc / 100 + rimSrc * xINCH) / xINCH;
                //Log.d(TiresCore.DS, "inEnd = " + inEnd + " from " + (2 * Float.valueOf(strS[strS.length - 1]) * widthSrc / 100 + rimSrc * xINCH) / xINCH);
            }

            int i = 0;

            while(inValue <= inEnd + 0.5f) {
                int realH = (int)inH;
                if(i > 0)
                    realH = Math.round(i * inH) - Math.round((i - 1) * inH);

                //Log.d(TiresCore.DS, "realH\" = " + realH + " / " + (inH * (float)i) + " - " + (inH * (float)(i - 1)) + " = " + (inH * (float)i - inH * (float)(i - 1)));

                final LinearLayout pnlItem = new LinearLayout(this);
                pnlItem.setOrientation(LinearLayout.HORIZONTAL);
                addItem(pnlItem, TiresCore.getSizeInInch(inValue, this), false, realH / 2, false);


                if(sizeTmp == inValue) {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                    pnlItem.setTag("+");

                    selected = (int)(i * inH / 2);
                }
                else {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.app_background));
                    pnlItem.setTag("");
                }

                pnlIn.addView(pnlItem);

                final String inText = TiresCore.getSizeInInch(inValue, this);
                pnlItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!pnlItem.getTag().toString().equalsIgnoreCase("+")) {
                            LinearLayout oldSelection = (LinearLayout) pnlSize.findViewWithTag("+");
                            oldSelection.setBackgroundColor(getResources().getColor(R.color.app_background));
                            oldSelection.setTag("");

                            pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                            pnlItem.setTag("+");
                        }

                        selectSize(inText);
                    }
                });

                inValue += 0.5f;
                i ++;
            }

            pnlSize.addView(pnlMM);
            pnlSize.addView(pnlLine);
            pnlSize.addView(pnlIn);

            final float curPos = selected;
            pnlSize.post(new Runnable() {
                @Override
                public void run() {
                    scrSize.scrollTo(0, (int) curPos - scrSize.getMaxScrollAmount());
                }
            });
        }
        else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_D) {
            pnlSize.setOrientation(LinearLayout.VERTICAL);

            strS = this.getApplicationContext().getResources().getStringArray(R.array.array_diameter);
            int mmH = (int)getResources().getDimension(R.dimen.size_select_text_size) * 2;

            int selected = 0;

            for(int i = 0; i < strS.length; i ++) {
                final String size = strS[i];

                final LinearLayout pnlItem = new LinearLayout(this);
                pnlItem.setOrientation(LinearLayout.HORIZONTAL);
                addItem(pnlItem, size, false, mmH, true);

                if(sizeTmp == Float.valueOf(size.substring(1))) {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                    pnlItem.setTag("+");

                    selected = i;
                }
                else {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.app_background));
                    pnlItem.setTag("");
                }

                pnlItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!pnlItem.getTag().toString().equalsIgnoreCase("+")) {
                            LinearLayout oldSelection = (LinearLayout) pnlSize.findViewWithTag("+");
                            oldSelection.setBackgroundColor(getResources().getColor(R.color.app_background));
                            oldSelection.setTag("");

                            pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                            pnlItem.setTag("+");
                        }

                        selectSize(size.substring(1));
                    }
                });

                pnlSize.addView(pnlItem);
            }

            final float curPos = mmH * (selected);
            pnlSize.post(new Runnable() {
                @Override
                public void run() {
                    scrSize.scrollTo(0, (int) curPos - scrSize.getMaxScrollAmount());
                }});
        }
        else if((sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_W) ||
                (sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_ET)) {

            pnlSize.setOrientation(LinearLayout.VERTICAL);

            if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_W)
                strS = this.getApplicationContext().getResources().getStringArray(R.array.array_rim);
            else if(sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_ET)
                strS = this.getApplicationContext().getResources().getStringArray(R.array.array_et);

            int selected = 0;
            int mmH = (int)getResources().getDimension(R.dimen.size_select_text_size) * 2;

            for(int i = 0; i < strS.length; i ++) {
                final String size;
                if ((sizeToSelect == TiresCore.SIZE_TO_SELECT_RIM_ET) && (Integer.valueOf(strS[i]) > 0))
                    size = "+" + strS[i];
                else
                    size = strS[i];

                final LinearLayout pnlItem = new LinearLayout(this);
                pnlItem.setOrientation(LinearLayout.HORIZONTAL);
                addItem(pnlItem, size, false, mmH, true);

                if(sizeTmp == Float.valueOf(size)) {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                    pnlItem.setTag("+");

                    selected = i;
                }
                else {
                    pnlItem.setBackgroundColor(getResources().getColor(R.color.app_background));
                    pnlItem.setTag("");
                }

                pnlItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!pnlItem.getTag().toString().equalsIgnoreCase("+")) {
                            LinearLayout oldSelection = (LinearLayout) pnlSize.findViewWithTag("+");
                            oldSelection.setBackgroundColor(getResources().getColor(R.color.app_background));
                            oldSelection.setTag("");

                            pnlItem.setBackgroundColor(getResources().getColor(R.color.size_select_selected));
                            pnlItem.setTag("+");
                        }

                        selectSize(size);
                    }
                });

                pnlSize.addView(pnlItem);
            }

            final float curPos = mmH * (selected);
            pnlSize.post(new Runnable() {
                @Override
                public void run() {
                    scrSize.scrollTo(0, (int) curPos - scrSize.getMaxScrollAmount());
                }});
        }
    }

    private void selectSize(String res) {
        //Log.d(TiresCore.DS, "SizeSelect: selected size = " + res);

        float sizeSrc = 0;

        if(res.contains("\"")) {
            isInTmp = true;
            sizeSrc = Float.valueOf(res.substring(0, res.length() - 1));
        }
        else {
            isInTmp = false;
            sizeSrc = Float.valueOf(res);
        }

        if(sizeTmp == sizeSrc)
            returnSize();

        sizeTmp = sizeSrc;

        //Log.d(TiresCore.DS, "SizeSelect: SIZE = " + sizeSrc + ", INCHES = " + isInTmp);
    }

    private void returnSize() {
        Intent intent = new Intent();
        intent.putExtra("SIZE", sizeTmp);
        intent.putExtra("INCHES", isInTmp);
        intent.putExtra("SIZE_TYPE", sizeToSelect);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_size_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            //Log.d(TiresCore.DS, "SizeSelect: menu-back");
            onBackPressed();
            return true;
        }
        if(id == R.id.action_size_done) {
            returnSize();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
