package com.project1.heydoc.Record;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project1.heydoc.R;

import java.util.ArrayList;

public class Record_Data_Adapter extends RecyclerView.Adapter<Record_Data_Adapter.ViewHolder> implements OnRecordItemClickListener, Filterable {

    ArrayList<Record_Data> items = new ArrayList<Record_Data>();                //데이터 형식의 arraylist 선언.
    ArrayList<Record_Data> unitems = items;

    OnRecordItemClickListener listener;     //온레코드아이템클릭리스터 선언

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {             //뷰홀더 생성
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.record_item, parent, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {                //뷰홀더를 리사이클러뷰에 추가
        Record_Data item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {                 //리사이클러뷰 안에 있는 아이템들을 카운트함
        return items.size();
    }               //아이템즈에 얼마나 있는지 사이즈 리턴해주는 메소드

    public void setOnItemClickListener(OnRecordItemClickListener listener){         //아이템클릭리스너 셋팅
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {           //아이템클릭 이벤트 시 파라미터로 값을 넘겨주도록 함
        if(listener!=null){
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onItemLongClick(ViewHolder holder, View view, int position) {
        if(listener!=null){
            listener.onItemLongClick(holder, view, position);
        }
    }

    @Override
    public Filter getFilter() {                                                         //필터링 기능을 넣기위한 겟필터 함수(adapter가 filterable을 implements 해서 사용 가능)


        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {               //필터링하는 메소드
                String charString = charSequence.toString();                            //입력된 값을 스트링으로 받아옴
                Log.i("태그", "필터링 실행!");
                if(charString.isEmpty()){                                           //값이 비어 있으면 기존 아이템리스트를 출력하도록 아이템에 언필터아이템을 넣음.
                    Log.i("태그", "아이템이 없다면");
                    items = unitems;
                }else{
                    ArrayList<Record_Data> iteming = new ArrayList<>();             //필터링 되는 것을 저장하기 위한 리스트를 선언
                    Log.i("태그", "아이템이 있다면");
                    for(Record_Data data : unitems){                                //필터링 안 된 아이템리스트만큼 반복문을 돌림.
                        Log.i("태그", "필터링 포문 안");
                        if(data.getSubject().contains(charString.toLowerCase())||data.getDetail().contains(charString.toLowerCase())||          //필터링 값을 가졌는지 확인
                                data.getSection().contains(charString.toLowerCase())||data.getDate().contains(charString.toLowerCase())){
                            Log.i("태그", "필터링 조건문 안");
                            iteming.add(data);                                      //찾는 값을 가졌으면 필터링 중인 아이템 리스트에 넣음
                            Log.i("태그", "조건에 맞는 아이템을 필터링어레이에 넣음");
                        }
                    }

                    items = iteming;                                //끝나면 필터링 중인 아이템 리스트를 필터된 아이템 리스트에 넣음
                    Log.i("태그", "필터드어레이를 필터링어레이로 선언");

                }
                FilterResults filterResults = new FilterResults();          //필터 결과를 선언
                filterResults.values = items;                       //필터결과 값에 아이템 리스트를 넣음
                Log.i("태그", "필터링 결과에 필터드어레이를 넣음");
                return filterResults;               //필터 결과 반환
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                items = (ArrayList<Record_Data>)filterResults.values;               //필터결과 값을 아이템리스트에 넣음
                Log.i("태그", "결과값을 출력");
                notifyDataSetChanged();                                                 //변화 결과 알려주기.
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder{            //뷰홀더 클래스 정의

        TextView showsubject;                   //보여줄 텍스트 뷰 선언
        TextView showdate;
        TextView showsection;
        TextView showdetail;
        TextView showattach;
        String thistime;

        public ViewHolder(View itemView, final OnRecordItemClickListener listener){           //뷰홀더 생성자
            super(itemView);
            showsubject = itemView.findViewById(R.id.thesubject);       //xml과 연동
            showdate = itemView.findViewById(R.id.thedate);
            showsection = itemView.findViewById(R.id.thesection);
            showdetail = itemView.findViewById(R.id.thedetail);
            showattach = itemView.findViewById(R.id.attachnum);

            itemView.setOnClickListener(new View.OnClickListener() {            //뷰홀더에 온클릭리스너 설정
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();            //눌린 위치의 어레이리스트 위치 확인
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, view, position);      //리스너가 널이 아니면, 아이템클릭 메소드에 파라미터 값으로 해당값들 보내기
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {            //오래 누르기 기능 리스너를 만듦
                @Override
                public boolean onLongClick(View view) {             //오래 눌렀을 때
                    int position = getAdapterPosition();                //어뎁터 위치를 가져오고
                    if(listener!=null){
                        listener.onItemLongClick(ViewHolder.this, view, position);      //리스너가 null이 아니면 롱클릭 메소드를 실행
                    }
                    return false;
                }
            });

        }

        public void setItem(Record_Data item){                      //각 텍스트들 값 넣어주는 셋팅
            showsubject.setText(item.getSubject());
            showdate.setText(item.getDate());
            showsection.setText(item.getSection());
            showdetail.setText(item.getDetail());
            showattach.setText(item.getAttach());
            thistime = item.getThistime();
        }
    }

    public void addItem(Record_Data item){              //리사이클러뷰에 아이템 추가하는 메소드
        items.add(item);
    }

    public void setItems(ArrayList<Record_Data> items){             //리사이클러뷰의 아이템을 어레이리스트 형식으로 통째로 조작하는 셋터
        this.items = items;
    }

    public Record_Data getItem(int position){                   //리사이클러뷰의 아이템을 볼 수 있는 겟터
        return items.get(position);
    }

    public void setItem(int position, Record_Data item){         //리사이클러뷰의 아이템을 리스트 안의 하나의 값을 바꿀 수 있는 셋터
        this.items.set(position, item);
    }
}
