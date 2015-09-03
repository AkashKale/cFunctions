package com.conrover.cfunctions;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.sql.Array;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Akash on 27-08-2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter{

    private Context _context;
    private List<String> _groupNames; // header titles
    // child data in format of header title, child title
    private HashMap<String,List<String>> _listItems;

    public ExpandableListAdapter(Context context, List<String> groupNames,
                                 HashMap<String, List<String>> listItems) {
        this._context = context;
        this._groupNames = groupNames;
        this._listItems = listItems;
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
}