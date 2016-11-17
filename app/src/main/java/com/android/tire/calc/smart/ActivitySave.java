package com.android.tire.calc.smart;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tire.calc.smart.core.TiresCore;
import com.android.tire.calc.smart.core.aTirePreset;
import com.android.tire.calc.smart.core.aTireSearch;
import com.android.tire.calc.smart.lists.listTireSave;
import com.android.tire.calc.smart.lists.listTireSearch;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

public class ActivitySave extends AppCompatActivity {

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    ActionBar aBar;

    String tireToSave = "";
    String tireLabel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-73274611-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        aBar = getSupportActionBar();
        //aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tireToSave = extras.getString("SIZE_TO_SAVE");
            tireLabel = extras.getString("SIZE_TO_DISPLAY");
        }

        Log.d(TiresCore.DS, "Save: " + tireToSave);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.setTitle(getString(R.string.save_caption));
        aBar.setTitle(getString(R.string.save_caption));

        render();
    }

    public void render()
    {
        ListView lstPresets = (ListView) findViewById(R.id.lstPresets);

        listTireSave adapter = new listTireSave(getApplicationContext(), TiresCore.getPresets());
        lstPresets.setAdapter(adapter);

        final ArrayList<aTirePreset> tmpResTires = TiresCore.getPresets();
        lstPresets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TiresCore.DS, "list: item-click = " + position);
                //setTireSize(tmpResTires.get(position).getSize());
            }
        });



        TextView txtTireSize = (TextView)findViewById(R.id.txtSizeToSave);
        txtTireSize.setText(tireLabel);

        Button btnSave = (Button)findViewById(R.id.btnAddPreset);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TiresCore.DS, "Saving... " + tireToSave);
                TiresCore.addPresset(tireToSave);

                render();
            }});


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //LinearLayout pnlOptions = (LinearLayout)findViewById(R.id.pnlSearchOptions);
        //LinearLayout pnlResults = (LinearLayout)findViewById(R.id.pnlSearchResults);


        if (id == android.R.id.home) {
            Log.d(TiresCore.DS, "Save: menu-back");
            onBackPressed();

            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
