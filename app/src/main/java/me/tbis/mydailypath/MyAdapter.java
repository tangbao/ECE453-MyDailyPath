package me.tbis.mydailypath;

/**
 * Created by tzzma on 2017/11/8.
 *
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, String>> mList;
    private LayoutInflater mInflater;
    public MyAdapter(Context c, List<Map<String, String>> list){
        this.mContext = c;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.checkin_listview, null);

            holder.mNameAndTime = convertView.findViewById(R.id.name_and_time);
            holder.mGPSCoordinate = convertView.findViewById(R.id.gps_coord);
            holder.mAddress = convertView.findViewById(R.id.address);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mNameAndTime.setText(mList.get(position).get("name") + " " + mList.get(position).get("time"));
        holder.mGPSCoordinate.setText(mList.get(position).get("latitude") + ", " + mList.get(position).get("longitude"));
        holder.mAddress.setText(mList.get(position).get("address"));

        return convertView;
    }

    private final class ViewHolder{
        private TextView mNameAndTime;
        private TextView mGPSCoordinate;
        private TextView mAddress;
    }
}