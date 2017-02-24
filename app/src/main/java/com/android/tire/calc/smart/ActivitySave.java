package com.android.tire.calc.smart;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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

    int selectedPreset = -1;
    Menu mMenu;

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
        for(int i = 0; i < TiresCore.getPresets().size(); i ++) {
            String preset = TiresCore.getPresets().get(i).toDisplay();
            if(preset.equalsIgnoreCase(tireLabel)) {
                Log.d(TiresCore.DS, "list: size \"" + tireLabel + "\" already exists, index = " + i);
                selectedPreset = i;
                break;
            }
        }



        ListView lstPresets = (ListView) findViewById(R.id.lstPresets);
        lstPresets.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listTireSave adapter = new listTireSave(getApplicationContext(), TiresCore.getPresets());
        lstPresets.setAdapter(adapter);
        //lstPresets.setItemChecked(selectedPreset, true);

        final ArrayList<aTirePreset> tmpResTires = TiresCore.getPresets();
        lstPresets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == selectedPreset) {
                    Log.d(TiresCore.DS, "list: preset selected = " + tmpResTires.get(position).toDisplay());
                }

                selectedPreset = position;
                mMenu.findItem(R.id.action_delete).setVisible(true);

                Log.d(TiresCore.DS, "list: item-click = " + selectedPreset + ", size = " + tmpResTires.get(position).toDisplay());
            }
        });

        //lstPresets.performItemClick(lstPresets.getAdapter().getView(selectedPreset, null, null), selectedPreset, selectedPreset);


        LinearLayout pnlAdd = (LinearLayout)findViewById(R.id.pnlAdd);
        if(selectedPreset == - 1) {

            pnlAdd.setVisibility(View.VISIBLE);

            TextView txtTireSize = (TextView) findViewById(R.id.txtSizeToSave);
            txtTireSize.setText(tireLabel);

            Button btnSave = (Button) findViewById(R.id.btnAddPreset);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TiresCore.DS, "Saving... " + tireToSave);
                    TiresCore.addPreset(tireToSave);

                    render();
                }
            });

        }
        else {
            pnlAdd.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_save, menu);

        mMenu = menu;
        if(selectedPreset == -1)
            mMenu.findItem(R.id.action_delete).setVisible(false);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d(TiresCore.DS, "Save: menu-back");
            onBackPressed();

            return true;
        }
        else if(id == R.id.action_delete) {
            Log.d(TiresCore.DS, "Deleting... " + TiresCore.getPresets().get(selectedPreset).toDisplay());
            TiresCore.deletePreset(selectedPreset);
            selectedPreset = -1;


            render();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
