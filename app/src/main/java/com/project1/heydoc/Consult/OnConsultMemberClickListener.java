package com.project1.heydoc.Consult;

import android.view.View;

public interface OnConsultMemberClickListener {
    public void onItemClick(ConsultMember_Data_Adapter.ViewHolder viewHolder, View view, int position);

    public void onItemLongClick(ConsultMember_Data_Adapter.ViewHolder viewHolder, View view, int position);
}
