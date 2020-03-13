package com.gaoxianglong.maque.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaoxianglong.maque.R;

import java.util.List;

public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordAdapter.ViewHolder> {

    private static final String TAG = "CallRecordAdapter";
    private Context mContext;
    private List<String> mCallList;

    public CallRecordAdapter(List<String> callList) {
        this.mCallList = callList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.call_record_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String callRecordText = mCallList.get(position);
        String date;
        String s = callRecordText.substring(11);
        String year = callRecordText.substring(0, 4);
        if (year.equals("2020")) {
            date = callRecordText.substring(5,10);
        } else {
            date = callRecordText.substring(0,10);
        }
        holder.callRecord.setText(date);
        holder.callRecordDate.setText(s);
    }

    @Override
    public int getItemCount() {
        return mCallList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView callRecord;
        TextView callRecordDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            callRecord = itemView.findViewById(R.id.call_record_tv);
            callRecordDate = itemView.findViewById(R.id.call_record_date);
        }
    }
}
