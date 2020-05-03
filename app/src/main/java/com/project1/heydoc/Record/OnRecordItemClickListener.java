package com.project1.heydoc.Record;

import android.view.View;

public interface OnRecordItemClickListener {
    public void onItemClick(Record_Data_Adapter.ViewHolder holder, View view, int position);

    public void onItemLongClick(Record_Data_Adapter.ViewHolder holder, View view, int position);

}
