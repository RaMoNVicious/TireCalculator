package com.android.tire.calc.smart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.android.tire.calc.smart.core.TiresCore;
import com.android.tire.calc.smart.core.aTire;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class ActivityMain extends ActionBarActivity {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private TextView txtT1;
    private TextView txtT2;
    private TextView txtR1;
    private TextView txtR2;
    private LinearLayout pnlT1;
    private LinearLayout pnlT2;

    private int inputTire = 0;

    Menu topMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-73274611-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        TiresCore.init(this);
    }

    protected void onStart() {
        super.onStart();

        txtT1 = (TextView) findViewById(R.id.txtTire1);
        txtT2 = (TextView) findViewById(R.id.txtTire2);
        txtR1 = (TextView) findViewById(R.id.txtRim1);
        txtR2 = (TextView) findViewById(R.id.txtRim2);
        pnlT1 = (LinearLayout)findViewById(R.id.pnlTire1);
        pnlT2 = (LinearLayout)findViewById(R.id.pnlTire2);

        LinearLayout pnlInput = (LinearLayout)findViewById(R.id.pnlTireInput);
        pnlInput.setVisibility(View.GONE);

        if(inputTire == 0)
            renderMain();
        else if(inputTire == 1)
            renderInput(txtT1, TiresCore.getTire(1));
        else if(inputTire == 2)
            renderInput(txtT2, TiresCore.getTire(2));

        adInit();
    }

    private void renderMain() {

        txtT1.setText(TiresCore.getTire(1).getTireLabel());
        txtT2.setText(TiresCore.getTire(2).getTireLabel());
        txtR1.setText(TiresCore.getTire(1).getRimLabel());
        txtR2.setText(TiresCore.getTire(2).getRimLabel());

        pnlT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTire = 1;
                topMenu.findItem(R.id.action_presets).setVisible(true);
                renderInput(txtT1, TiresCore.getTire(1));
            }
        });

        pnlT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputTire = 2;
                topMenu.findItem(R.id.action_presets).setVisible(true);
                renderInput(txtT2, TiresCore.getTire(2));
            }
        });

        final LinearLayout pnlInfo = (LinearLayout)findViewById(R.id.pnlInfo);
        final ScrollView scrlInfo = (ScrollView)findViewById(R.id.pnlInfoScroll);
        ImageView imgTires = (ImageView)findViewById(R.id.imgTires);
        TiresCore.renderTiresFace(this, imgTires);
        //TiresCore.renderTiresSide(this, imgTires);

        imgTires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    scrlInfo.setVisibility(View.VISIBLE);
            }
        });

        pnlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    scrlInfo.setVisibility(View.GONE);
            }
        });

        renderInfo();
    }

    private void renderInput(final TextView srcLabel, final aTire srcTire) {
        final LinearLayout pnlInput = (LinearLayout)findViewById(R.id.pnlTireInput);

        final TextView cmbW = (TextView)findViewById(R.id.cmb_width);
        final TextView txtSplitTire = (TextView)findViewById(R.id.txt_split_01);
        final TextView cmbH = (TextView)findViewById(R.id.cmb_height);
        final TextView cmbD = (TextView)findViewById(R.id.cmb_diameter);
        final TextView cmbR = (TextView)findViewById(R.id.cmb_rim);
        final TextView cmbE = (TextView)findViewById(R.id.cmb_et);
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);

        //String.valueOf(srcTire.getWidth()));

        cmbD.setText(String.valueOf(srcTire.getD()));// + "\"");
        cmbR.setText(String.valueOf(srcTire.getRim()));// + "\"");
        cmbE.setText(String.valueOf(srcTire.getET()));


        if(!srcTire.getInInch()) {
            cmbW.setText(srcTire.getWidthString());
            txtSplitTire.setText("/");
            cmbH.setText(srcTire.getHeightString());

            cmbW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                    intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_WIDTH);
                    intent.putExtra("SIZE", srcTire.getWidth());
                    intent.putExtra("RIM_WIDTH", (float) srcTire.getRim());
                    intent.putExtra("INCHES", srcTire.getInInch());
                    startActivityForResult(intent, 1);
                }
            });

            cmbH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                    intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_HEIGHT);
                    intent.putExtra("SIZE", srcTire.getHeight());
                    intent.putExtra("WIDTH", srcTire.getWidth());
                    intent.putExtra("RIM", srcTire.getD());
                    intent.putExtra("INCHES", srcTire.getInInch());
                    startActivityForResult(intent, 1);
                }
            });
        }
        else {
            cmbW.setText(srcTire.getHeightString());
            txtSplitTire.setText("x");
            cmbH.setText(srcTire.getWidthString());

            cmbH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                    intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_WIDTH);
                    intent.putExtra("SIZE", srcTire.getWidth());
                    intent.putExtra("RIM_WIDTH", (float) srcTire.getRim());
                    intent.putExtra("INCHES", srcTire.getInInch());
                    startActivityForResult(intent, 1);
                }
            });

            cmbW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                    intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_HEIGHT);
                    intent.putExtra("SIZE", srcTire.getHeight());
                    intent.putExtra("WIDTH", srcTire.getWidth());
                    intent.putExtra("RIM", srcTire.getD());
                    intent.putExtra("RIM_WIDTH", (float) srcTire.getRim());
                    intent.putExtra("INCHES", srcTire.getInInch());
                    startActivityForResult(intent, 1);
                }
            });
        }




        cmbD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_RIM_D);
                intent.putExtra("SIZE", (float) srcTire.getD());
                startActivityForResult(intent, 1);
            }});

        cmbR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_RIM_W);
                intent.putExtra("WIDTH", srcTire.getWidth());
                intent.putExtra("SIZE", (float) srcTire.getRim());
                startActivityForResult(intent, 1);
            }
        });

        cmbE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivitySizeSelect.class);
                intent.putExtra("SIZE_TYPE", TiresCore.SIZE_TO_SELECT_RIM_ET);
                intent.putExtra("SIZE", (float)srcTire.getET());
                startActivityForResult(intent, 1);
            }});


        pnlInput.setVisibility(View.VISIBLE);

        ImageView cmdDone = (ImageView)findViewById(R.id.cmdOk);
        cmdDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TiresCore.save(getApplicationContext());
                pnlInput.setVisibility(View.GONE);

                tracker.send(new HitBuilders.EventBuilder().setCategory(getString(R.string.analytics_category_selected_tire)).setAction(TiresCore.getTire(inputTire).toSizeString()).setLabel("InputTire=" + inputTire).build());

                inputTire = 0;
                topMenu.findItem(R.id.action_presets).setVisible(false);
            }
        });
    }

    private void renderInfo() {
        TextView txtT = (TextView)findViewById(R.id.txtInfo);
        TextView txtT1 = (TextView)findViewById(R.id.txtInfo1);
        TextView txtT2 = (TextView)findViewById(R.id.txtInfo2);

        TextView txtTip = (TextView)findViewById(R.id.txtTip);

        TiresCore.renderTireInfo(this, txtT1, txtT2, txtT);
        TiresCore.renderTips(this, txtTip);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent response) {
        if (response == null) return;
        float resSize = response.getFloatExtra("SIZE", 0);
        boolean isInSize = response.getBooleanExtra("INCHES", false);
        int resSizeType = response.getIntExtra("SIZE_TYPE", -1);

        TextView txtT = new TextView(this);

        if(inputTire == 1) txtT = (TextView) findViewById(R.id.txtTire1);
            else if(inputTire == 2) txtT = (TextView) findViewById(R.id.txtTire2);


        if(resSizeType == TiresCore.SIZE_TO_SELECT_WIDTH)
            TiresCore.getTire(inputTire).setWidth(resSize, isInSize);
        else if(resSizeType == TiresCore.SIZE_TO_SELECT_HEIGHT)
            TiresCore.getTire(inputTire).setHeight(resSize, isInSize);
        else if(resSizeType == TiresCore.SIZE_TO_SELECT_RIM_D)
            TiresCore.getTire(inputTire).setD((int) resSize);
        else if(resSizeType == TiresCore.SIZE_TO_SELECT_RIM_W)
            TiresCore.getTire(inputTire).setRim(resSize);
        else if(resSizeType == TiresCore.SIZE_TO_SELECT_RIM_ET)
            TiresCore.getTire(inputTire).setET((int) resSize);

        renderInput(txtT, TiresCore.getTire(inputTire));


        renderMain();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        topMenu = menu;

        topMenu.findItem(R.id.action_show_in_inch).setChecked(!TiresCore.getMetric(this));
        topMenu.findItem(R.id.action_presets).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.action_find_size) {
            Intent intent = new Intent(this, ActivitySearch.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_presets) {
            Intent intent = new Intent(this, ActivitySave.class);
            intent.putExtra("SIZE_TO_SAVE", TiresCore.getTire(inputTire).toSizeString());
            intent.putExtra("SIZE_TO_DISPLAY", TiresCore.getTire(inputTire).getTireLabel().replace(" ", "") + " " + TiresCore.getTire(inputTire).getRimLabel());
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_show_in_inch) {
            item.setChecked(!item.isChecked());
            TiresCore.setMetric(!item.isChecked(), this);
            renderMain();
            return true;
        }
        else if (id == R.id.action_info) {
            Intent intent = new Intent(this, ActivityInfo.class);
            startActivity(intent);

        }
        else if(id == R.id.action_about) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(getString(R.string.dialog_caption_about));
            dlg.setMessage(getString(R.string.dialog_text_about));
            dlg.setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                   //dialogInterface.dismiss();
                }
            });
            dlg.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(inputTire != 0) {

            inputTire = 0;

            final LinearLayout pnlInput = (LinearLayout)findViewById(R.id.pnlTireInput);

            TiresCore.save(getApplicationContext());
            pnlInput.setVisibility(View.GONE);

            tracker.send(new HitBuilders.EventBuilder().setCategory(getString(R.string.analytics_category_selected_tire)).setAction(TiresCore.getTire(inputTire).toSizeString()).setLabel("InputTire=" + inputTire).build());
        }
        else
            finish();
    }

    private void adInit() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("A8A8DEC6901CDAD972CCB09EDC7A3A48")
                .build();
        mAdView.loadAd(adRequest);
    }
}
