package com.mitlab.zusliu.User.Interface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mitlab.zusliu.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    private List<String> mDataSet;

    public MyAdapter(List<String> data)
    {
        mDataSet = data;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_view_1, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.txtItem.setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtItem;
        public ViewHolder(View v){
            super(v);
            txtItem = (TextView) v.findViewById(R.id.txtItem);
        }
    }
}
