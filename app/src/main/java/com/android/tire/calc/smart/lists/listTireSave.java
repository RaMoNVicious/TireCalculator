package com.android.tire.calc.smart.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tire.calc.smart.R;
import com.android.tire.calc.smart.core.aTirePreset;
import com.android.tire.calc.smart.core.aTireSearch;

import java.util.ArrayList;

/**
 * Created by ramon on 15.04.16.
 */
public class listTireSave extends BaseAdapter {

    private Context listContext;
    private ArrayList<aTirePreset> listItems;
    LayoutInflater listInflater;

    public listTireSave(Context context, ArrayList<aTirePreset> srcItems) {
        listContext = context;
        listItems = srcItems;
        listInflater = (LayoutInflater) listContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listRow = convertView;
        if(listRow == null) {
            listRow = listInflater.inflate(R.layout.list_preset, parent, false);
        }

        TextView lblSize = (TextView)listRow.findViewById(R.id.txtSize);
        //ImageView

        lblSize.setText(listItems.get(position).toDisplay());

        return listRow;
    }
}
