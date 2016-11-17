package com.android.tire.calc.smart.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.tire.calc.smart.R;
import com.android.tire.calc.smart.core.TiresCore;
import com.android.tire.calc.smart.core.aTireSearch;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ramon on 05.02.16.
 */
public class listTireSearch extends BaseAdapter {

    private Context listContext;
    private ArrayList<aTireSearch> listItems;
    LayoutInflater listInflater;


    public listTireSearch(Context context, ArrayList<aTireSearch> srcItems) {
        listContext = context;
        listItems = srcItems;
        listInflater = (LayoutInflater) listContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View listRow = convertView;
        if(listRow == null) {
            listRow = listInflater.inflate(R.layout.list_search_result, parent, false);
        }

        TextView txtTireSize = (TextView)listRow.findViewById(R.id.txtSize);
        TextView txtDifW = (TextView)listRow.findViewById(R.id.txtDifferenceW);
        TextView txtDifH = (TextView)listRow.findViewById(R.id.txtDifferenceH);
        //LinearLayout pnlTireRow = (LinearLayout)currRow.findViewById(R.id.pnlTireSearch);

        txtTireSize.setText(listItems.get(i).getLabelTire());
        txtDifW.setText(getSize(listItems.get(i).getTireDifferenceW(), listContext));
        txtDifH.setText(getSize(listItems.get(i).getTireDifferenceH(), listContext));


        return listRow;
    }

    private static String getSize(float value, Context c) {
        String prefix = "";
        if(value > 0)
            prefix = "+";

        if(TiresCore.getMetric(c))
            return prefix + String.format(Locale.ENGLISH, "%.0f", (float)Math.round(value)) + " " + c.getString(R.string.text_size_mm);
        else
            return prefix + String.format(Locale.ENGLISH, "%.1f", (float)Math.round(10 * value / 25.4) / 10) + "\"";
    }
}
