package com.mitlab.zusliu.Update.List.View;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mitlab.zusliu.R;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter
{
    private Context context = null;
    private ArrayList<ListItem> items = new ArrayList<ListItem>();

    // using default parameter constructor
    public ListAdapter() { /***/ }

    // using custom parameter constructor
    public ListAdapter(Context _context) { this.context = _context; }

    public void addItem(ListItem item) { this.items.add(item); }
    public void clearItem() { this.items.clear(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = (View) convertView;

        if(view == null) { view = View.inflate(this.context, R.layout.beacon_data, null); }

        if((!this.items.isEmpty()) && this.items.size() > position)
        {
            TextView text1 = (TextView) view.findViewById(R.id.it3_text1);
            TextView text2 = (TextView) view.findViewById(R.id.it3_text2);
            TextView text3 = (TextView) view.findViewById(R.id.it3_text3);
            TextView text4 = (TextView) view.findViewById(R.id.it3_text4);
            TextView text5 = (TextView) view.findViewById(R.id.it3_text5);
            TextView text6 = (TextView) view.findViewById(R.id.it3_text6);/////////////////////
            ListItem item = (ListItem) this.items.toArray()[position];

            text1.setText(item.getText1());
            text2.setText(item.getText2());
            text3.setText(item.getText3());
            text4.setText(item.getText4() + " DBM");
            text5.setText(item.getText5() + " V");
            text6.setText(item.getText6() ); ////////////////
        }

        else { view.setVisibility(View.GONE); }

        return view;
    }

    @Override
    public int getCount() { return this.items.size(); }

    @Override
    public Object getItem(int position) { return this.items.toArray()[position]; }

    @Override
    public long getItemId(int position) { return 0; }

}