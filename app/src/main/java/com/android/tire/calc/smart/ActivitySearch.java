package com.android.tire.calc.smart;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.tire.calc.smart.core.TiresCore;
import com.android.tire.calc.smart.core.aTireSearch;
import com.android.tire.calc.smart.lists.listTireSearch;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivitySearch extends AppCompatActivity {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    ActionBar aBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-73274611-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        aBar = getSupportActionBar();
        //aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    protected void onStart() {
        super.onStart();

        this.setTitle(getString(R.string.search_caption));

        aBar.setTitle(getString(R.string.search_caption));
        aBar.setSubtitle(getString(R.string.search_caption_sub) + " " + TiresCore.getTire(1).getTireLabel());

        Switch swtchLockW = (Switch)findViewById(R.id.chkLockWidth);
        Switch swtchLockH = (Switch)findViewById(R.id.chkLockHieght);
        Switch swtchLockD = (Switch)findViewById(R.id.chkLockRim);
        Switch swtchOffroad = (Switch)findViewById(R.id.chkOffroad);
        final LinearLayout pnlRim = (LinearLayout)findViewById(R.id.pnlLockRim);
        Spinner cmbRim = (Spinner)findViewById(R.id.cmbLockRim);

        final String[] rimSizes = getResources().getStringArray(R.array.array_diameter);
        ArrayAdapter<String> adapterRrim = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rimSizes);
        adapterRrim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbRim.setAdapter(adapterRrim);
        if(TiresCore.searchLockRimSize < rimSizes.length)
            cmbRim.setSelection(TiresCore.searchLockRimSize);

        cmbRim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TiresCore.searchLockRimSize = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }});

        swtchLockW.setChecked(TiresCore.searchLockWidth);
        swtchLockH.setChecked(TiresCore.searchLockHeight);
        swtchLockD.setChecked(TiresCore.searchLockRim);
        swtchOffroad.setChecked(TiresCore.searchOffroad);
        if(TiresCore.searchLockRim) pnlRim.setVisibility(View.VISIBLE);
            else pnlRim.setVisibility(View.GONE);

        swtchLockW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiresCore.searchLockWidth = isChecked;
                TiresCore.saveSearchOptions(getApplicationContext());
            }
        });

        swtchLockH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiresCore.searchLockHeight = isChecked;
                TiresCore.saveSearchOptions(getApplicationContext());
            }
        });

        swtchLockD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiresCore.searchLockRim = isChecked;
                TiresCore.saveSearchOptions(getApplicationContext());
                if(TiresCore.searchLockRim) pnlRim.setVisibility(View.VISIBLE);
                    else pnlRim.setVisibility(View.GONE);
            }
        });

        swtchOffroad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TiresCore.searchOffroad = isChecked;
                TiresCore.saveSearchOptions(getApplicationContext());
            }
        });


        final LinearLayout pnlOptions = (LinearLayout)findViewById(R.id.pnlSearchOptions);
        final LinearLayout pnlResults = (LinearLayout)findViewById(R.id.pnlSearchResults);
        final Context tmpContext = this;

        Button btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TiresCore.DS, "Search: btnSearch.onClick");

                pnlOptions.setVisibility(View.GONE);
                pnlResults.setVisibility(View.VISIBLE);

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.analytics_category_search_tire))
                        .setAction("lockW=" + TiresCore.searchLockWidth + "|lockH=" + TiresCore.searchLockHeight + "|lockD=" + TiresCore.searchLockRim + "|offroad=" + TiresCore.searchOffroad)
                        .build());


                ArrayList<aTireSearch> resTires = new ArrayList<aTireSearch>();
                if(!TiresCore.searchOffroad)
                    resTires = TiresCore.findTireSizeMM(tmpContext);
                else {
                    resTires = TiresCore.findTireSizeIN(tmpContext);
                }

                ListView lstMain = (ListView) findViewById(R.id.lstTires);

                listTireSearch adapter = new listTireSearch(getApplicationContext(), resTires);
                lstMain.setAdapter(adapter);

                final ArrayList<aTireSearch> tmpResTires = resTires;
                lstMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setTireSize(tmpResTires.get(position).getSize());
                    }
                });
            }
        });
    }

    public void setTireSize(String srcSize) {
        TiresCore.setTire2(srcSize);
        //Log.d(TiresCore.DS, "Search: Done = " + TiresCore.getTire(2).toSizeString());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        LinearLayout pnlOptions = (LinearLayout)findViewById(R.id.pnlSearchOptions);
        LinearLayout pnlResults = (LinearLayout)findViewById(R.id.pnlSearchResults);


        if (id == android.R.id.home) {
            //Log.d(TiresCore.DS, "Search: menu-back");

            if(pnlOptions.getVisibility() == View.VISIBLE)
                onBackPressed();
            else {
                pnlOptions.setVisibility(View.VISIBLE);
                pnlResults.setVisibility(View.GONE);
            }

            return true;
        }
        if(id == R.id.action_size_done) {
            finish();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
