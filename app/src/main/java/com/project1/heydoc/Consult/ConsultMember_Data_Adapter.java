package com.project1.heydoc.Consult;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project1.heydoc.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ConsultMember_Data_Adapter extends RecyclerView.Adapter<ConsultMember_Data_Adapter.ViewHolder> implements OnConsultMemberClickListener {

    ArrayList<ConsultMember_Data> items = new ArrayList<ConsultMember_Data>();
    OnConsultMemberClickListener listener;
    Context context;

    public ConsultMember_Data_Adapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("태그", "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());             //인플레이터 선언
        View itemView = inflater.inflate(R.layout.consult_member_item, parent, false);      //아이템뷰 선언 및 인플레이터 적용
        return new ViewHolder(itemView);        //뷰홀더 반환
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("태그", "onBindViewHolder");
        ConsultMember_Data item = items.get(position);
        holder.setItem(item);
        Glide.with(context).load(item.getUri()).apply(new RequestOptions().circleCrop()).into(holder.memberimg);
    }

    @Override
    public int getItemCount() {         //어레이리스트 카운트
        return items.size();
    }

    public void setOnItemClickListener(OnConsultMemberClickListener listener){      //아이템클릭리스너 셋팅
        this.listener = listener;
    }


    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {           //온 아이템 클릭 정의.. 파라미터로 값들 넘겨줌.
        if(listener!=null){
            listener.onItemClick(holder, view, position);
        }

    }

    @Override
    public void onItemLongClick(ViewHolder viewHolder, View view, int position) {
        if(listener!=null){
            listener.onItemLongClick(viewHolder, view, position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{            //뷰홀더 클래스
        ImageView memberimg;
        TextView membername;

        public ViewHolder(View itemView){               //뷰홀더 생성자
            super(itemView);
            memberimg = itemView.findViewById(R.id.memberimg);
            membername = itemView.findViewById(R.id.membername);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(        new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemLongClick(ViewHolder.this, view, position);
                    }

                    return false;
                }
            });

        }

        public void setItem(ConsultMember_Data item){           //뷰홀더 아이템들 세터
            Log.i("태그", "setItem");
            membername.setText(item.getMembername());
        }
    }

    public void addItem(ConsultMember_Data item){           //아이템 추가하는 메소드
        items.add(item);
    }

    public void setItems(ArrayList<ConsultMember_Data> items){      //데이터 전체를 설정하는 세터
        this.items = items;
    }

    public ConsultMember_Data getItem(int position){            //특정 위치에 있는 아이템을 가져오는 메소드
        return items.get(position);
    }

    public void setItem(int position, ConsultMember_Data item){         //특정 위치 아이템을 설정하는 세터
        this.items.set(position, item);
    }
}
