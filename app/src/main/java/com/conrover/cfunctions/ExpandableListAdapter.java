package com.conrover.cfunctions;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 27-08-2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _groupNames; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listItems;

    private List<String> original_groupNames;
    private HashMap<String, List<String>> original_listitems;

    public ExpandableListAdapter(Context context, List<String> groupNames,
                                 HashMap<String, List<String>> listItems) {
        this._context = context;
        this._groupNames = groupNames;
        this._listItems = listItems;
        original_groupNames=new ArrayList<String>(groupNames);
        original_listitems=new HashMap<String,List<String>>(listItems);
        //this.original_groupNames = groupNames;
        //this.original_listitems = listItems;
        //Log.e("Constructor", "Working");
    }

    @Override
    public int getGroupCount() {
        return this._groupNames.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listItems.get(this._groupNames.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._groupNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listItems.get(this._groupNames.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView tvListGroup = (TextView) convertView
                .findViewById(R.id.tvListGroup);
        tvListGroup.setTypeface(null, Typeface.BOLD);
        tvListGroup.setText(headerTitle);
        //Log.e("Header", "Working"+tvListGroup.getText());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        TextView tvListItem = (TextView) convertView
                .findViewById(R.id.tvListItem);

        tvListItem.setText(childText);
        //Log.e("Child","Working");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void FilterData(String query) {
        query = query.toLowerCase();
        //HashMap<String,List<String>> copyhp=new HashMap<String,List<String>>(this._listItems);
        //ArrayList<String> copylist=new ArrayList<String>(this._groupNames);
        this._groupNames.clear();
        this._listItems.clear();
        if (query.isEmpty()) {
            this._groupNames.addAll(original_groupNames);
            this._listItems.putAll(original_listitems);
            //this._groupNames.addAll(copylist);
            //this._listItems.putAll(copyhp);
        } else {
            ArrayList<String> newgrp=new ArrayList<String>();
            HashMap<String,List<String>> newhp=new HashMap<String,List<String>>();
            int grpflag=0;
            for (Map.Entry<String,List<String>> entry : original_listitems.entrySet()) {
                String key = entry.getKey();
                grpflag=0;
                List<String> value = entry.getValue();
                List<String> newlist=new ArrayList<String>();
                for(String entry2:value){
                    if(entry2.contains(query))
                    {
                        newlist.add(entry2);
                        grpflag=1;
                    }
                }
                if(grpflag==1)
                {
                    newgrp.add(key);
                    newhp.put(key, newlist);
                }

            }
            this._groupNames.addAll(newgrp);
            this._listItems.putAll(newhp);
            notifyDataSetChanged();
        }
    }
}