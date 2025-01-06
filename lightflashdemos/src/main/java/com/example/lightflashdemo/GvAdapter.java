package com.example.lightflashdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class GvAdapter extends BaseAdapter {

    private Context context;
    int[] mColorList,selectors;
    public GvAdapter(Context context, int[] colorList,int[] selectors){
        super();
        this.context = context;
        mColorList = colorList;
        this.selectors = selectors;
    }

    @Override
    public int getCount() {
        return mColorList.length;
    }

    @Override
    public Integer getItem(int position) {
        return mColorList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.gv_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mLl.setBackground(context.getDrawable(getItem(position)));
        holder.mCb.setChecked(selectors[position] == 1);
        holder.mCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheckListener != null){
                    mCheckListener.setCheckState(holder.mCb.isChecked(),position);
                }
            }
        });
        return convertView;
    }

    CheckListener mCheckListener;
    public void setListener(CheckListener listener){
        this.mCheckListener = listener;
    }

    static class ViewHolder{
        CheckBox mCb;
        RelativeLayout mLl;
        public ViewHolder(View view){
            mLl = view.findViewById(R.id.ll);
            mCb = view.findViewById(R.id.cb);
        }

    }
}
