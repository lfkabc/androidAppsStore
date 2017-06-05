package com.example.luo.androidappsstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by luo on 2017/6/5.
 */

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Item> mItemList;

    public GridAdapter(Context context, List<Item> items) {
        mContext = context;
        mItemList = items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new GridViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.item_image);
            viewHolder.textView = (TextView)convertView.findViewById(R.id.item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (GridViewHolder) convertView.getTag();
        }
        Item item = mItemList.get(position);
        viewHolder.textView.setText(item.getName());
        viewHolder.downloadUrl = item.getUrl();
        Glide.with(mContext)
                .load(item.getIcon())
                .into(viewHolder.imageView);
        convertView.setTag(viewHolder);

        return convertView; // 返回ImageView
    }


    /*
     * 功能：获得当前选项的ID
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        //System.out.println("getItemId = " + position);
        return position;
    }

    /*
     * 功能：获得当前选项
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /*
     * 获得数量
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return mItemList.size();
    }
}
