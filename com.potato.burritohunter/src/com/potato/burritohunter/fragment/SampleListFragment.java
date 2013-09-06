package com.potato.burritohunter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.potato.burritohunter.R;
import com.potato.burritohunter.stuff.SearchResult;

public class SampleListFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    public static class SlidingMenuAdapter extends ArrayAdapter<SearchResult> {

        public SlidingMenuAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.saved_list_item, null);
            }
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(getItem(position)._name);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            desc.setText(getItem(position).address);
            return convertView;
        }

    }
}
