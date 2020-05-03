package com.project1.heydoc.SendSOS;

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

public class SendSOSto_Data_Adapter extends RecyclerView.Adapter<SendSOSto_Data_Adapter.ViewHolder> implements OnSendSOSItemClickListener{

    ArrayList<SendSOSto_Data> items = new ArrayList<>();            //아이템들을 담을 어레이리스트 이다.
    Context context;                                        //glide 클래스를 위한 컨텍스트 가져오기
    OnSendSOSItemClickListener listener;


    public SendSOSto_Data_Adapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {         //뷰 홀더를 만드는 곳
        Log.i("태그", "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());         //인플레이터를 하기 위한 변수 선언(부모의 컨텍스트를 가져옴)
        View itemView = inflater.inflate(R.layout.sendsosto_item, parent, false); //아이템의 xml을 가져와서 인플레이트 함
        return new ViewHolder(itemView);                    //여기서 만든 뷰홀더를 리턴함(반환)

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {            //뷰 홀더를 바인딩 하는 곳
        Log.i("태그", "onBindViewHolder");
        SendSOSto_Data item = items.get(position);              //위치의 아이템을 가져와서 선언
        holder.setItem(item);                               //아이템의 값을 셋팅해줌
        Glide.with(context).load(item.getUri()).apply(new RequestOptions().circleCrop()).into(holder.receiverimage);            //이미지는 위의 셋팅과 다르게 여기서 해줌.
    }

    @Override
    public int getItemCount() {                 //아이템 갯수 카운트 하는 메소드
        return items.size();
    }

    public void setOnItemClickListener(OnSendSOSItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {           //아이템 클릭 리스너 메소드를 만든다.
        if(listener!=null){
            listener.onItemClick(holder, view, position);           //홀더와 뷰와 포지션을 파라미터로 넘김.
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{            //뷰홀더를 만드는 이너클래스
        ImageView receiverimage;                    //상대의 이미지를 보여줄 이미지뷰
        TextView receivername;                  //상대의 이름을 보여줄 텍스트 뷰

        public ViewHolder(@NonNull View itemView) {         //뷰홀더 생성자
            super(itemView);
            receiverimage = itemView.findViewById(R.id.receiverimg);            //xml 파일의 아이디로 연동을 한다
            receivername = itemView.findViewById(R.id.receivername);

            itemView.setOnClickListener(new View.OnClickListener() {                //아이템 뷰에 온클릭 리스너를 달아준다.
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onItemClick(ViewHolder.this, view, position);          //아이템 클릭 파라미터로 홀더와 뷰와 포지션을 보내준다.
                    }
                }
            });
        }
        public void setItem(SendSOSto_Data item){               //이 아이템의 초기값을 넣을 수 있는 세터
            receivername.setText(item.getReceivername());
        }
    }

    //여기서부터는 세터와 게터

    public ArrayList<SendSOSto_Data> getItems() {
        return items;
    }

    public void setItems(ArrayList<SendSOSto_Data> items) {
        this.items = items;
    }

    public void addItem(SendSOSto_Data item){           //아이템을 추가할 때 쓰이는 메소드.. 리사이클러뷰에서 중요.
        items.add(item);
    }

    public SendSOSto_Data getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, SendSOSto_Data item){
        this.items.set(position, item);
    }
}
