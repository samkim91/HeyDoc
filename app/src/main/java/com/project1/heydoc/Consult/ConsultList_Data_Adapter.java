package com.project1.heydoc.Consult;

import android.content.Context;
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

public class ConsultList_Data_Adapter extends RecyclerView.Adapter<ConsultList_Data_Adapter.ViewHolder> implements OnConsultListClickListener{        //상담목록 데이터 어텝터 클래스 만들기, 리사이클러뷰 어텝터를 상속(이 뷰홀더를 가진)

    ArrayList<ConsultList_Data> items = new ArrayList<ConsultList_Data>();      //상담목록 데이터를 가질 어레이리스트 선언
    OnConsultListClickListener listener;
    Context context;


    public ConsultList_Data_Adapter(Context context){
        this.context = context;
    }

    public void setOnItemClickListener(OnConsultListClickListener listener){         //아이템클릭리스너 셋팅
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position){        //온아이템클릭 메소드 정의.. 파라미터로 값 넘겨줌
        if(listener!=null){
            listener.onItemClick(holder, view, position);
        }
    }

    public void onItemLongClick(ViewHolder holder, View view, int position){
        if(listener!=null){
            listener.onItemLongClick(holder, view, position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {         //뷰홀더 크리에이트 메소드
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());                 //인플레이터 선언
        View itemView = inflater.inflate(R.layout.consult_list_item, parent, false);        //아이템뷰에 인플레이터를 인플레이트함
        return new ViewHolder(itemView);            //뷰홀더 리턴
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {        //뷰홀더 바인딩 하는 메소드
        ConsultList_Data item = items.get(position);            //아이템 포지션을 얻고
        holder.setItem(item);                                   //아이템을 셋팅함
        Glide.with(context).load(item.uri).apply(new RequestOptions().circleCrop()).into(holder.personimg);
    }

    @Override
    public int getItemCount() {         //아이템즈 크기 카운트하는 메소드
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{        //뷰홀더 클래스 선언
        ImageView personimg;            //사람 이미지
        TextView personname;            //사람 이름
        TextView showmessage;
        TextView lastmsgtime;

        public ViewHolder(View itemView){       //뷰홀더 생성자
            super(itemView);
            personimg = itemView.findViewById(R.id.personimg);
            personname = itemView.findViewById(R.id.personname1);
            showmessage = itemView.findViewById(R.id.showmessage);
            lastmsgtime = itemView.findViewById(R.id.lastmsgtime);


            itemView.setOnClickListener(new View.OnClickListener() {            //뷰에다 온클릭 리스너 기능 넣는 구문
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {                    //롱클릭리스너 생성 및 설정
                @Override
                public boolean onLongClick(View view) {                         //아이템을 오래 눌렀을 때
                    int position = getAdapterPosition();                        //어뎁터로부터 위치를 가져오고
                    if(listener!=null){
                        listener.onItemLongClick(ViewHolder.this, view, position);      //리스너가 null이 아니면 롱클릭을 실행
                    }
                    return false;
                }
            });
        }

        public void setItem(ConsultList_Data item){     //뷰홀더 셋 메소드
            personname.setText(item.getPersonname());
            showmessage.setText(item.getShowmessage());
            lastmsgtime.setText(item.getLastmsgtime());
        }

    }

    public void addItem(ConsultList_Data item){         //아이템 추가하는 메소드
        items.add(item);
    }           //아이템 추가하는 메소드

    public void setItems(ArrayList<ConsultList_Data> items) {       //아이템 한번에 셋터
        this.items = items;
    }       //아이템 전체를 설정하는 세터

    public ConsultList_Data getItem(int position){          //아이템 가져올 수 있는 메소드
        return items.get(position);
    }       //특정 위치 아이템을 반환하는 메소드

    public void setItem(int position, ConsultList_Data item){           //지정 아이템의 값만 바꾸는 메소드
        this.items.set(position, item);
    }       //특정 위치 아이템을 설정하는 세터
}
