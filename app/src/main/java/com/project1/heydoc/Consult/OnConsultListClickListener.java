package com.project1.heydoc.Consult;

import android.view.View;

public interface OnConsultListClickListener {
    public void onItemClick(ConsultList_Data_Adapter.ViewHolder viewHolder, View view, int position);

    public void onItemLongClick(ConsultList_Data_Adapter.ViewHolder viewHolder, View view, int position);
}
