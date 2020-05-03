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
import com.project1.heydoc.Login.LoginedUser;
import com.project1.heydoc.R;

import java.util.ArrayList;

public class Consult_Data_Adapter extends RecyclerView.Adapter{

    ArrayList<Consult_Data> items = new ArrayList<Consult_Data>();              //상담 데이터 arraylist 선언
    Context context;

    public Consult_Data_Adapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {                  //뷰타입을 지정하는 메소드
        Consult_Data consult_data = items.get(position);        //position의 데이터를 선택하고
        if(consult_data.getId().equals(LoginedUser.id)){          //데이터의 이름이 나(me)이면 0을 리턴, 아니면 1을 리턴
            return 0;
        }else{
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {             //뷰홀더 만들어주는 메소드
        View itemView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());                     //인플레이터 선언
        if(viewType==0){                    //데이터가 나일 때
            itemView = inflater.inflate(R.layout.consult_item0, parent, false);    //아이템 뷰에 인플레이터 인플레이트
            return new ViewHolder0(itemView);
        }else {                                 //데이터가 상대일 때
            itemView = inflater.inflate(R.layout.consult_item1, parent, false);    //아이템 뷰에 인플레이터 인플레이트
            return new ViewHolder1(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {       //뷰홀더에 값을 바인딩 하는 메소드
        Consult_Data item = items.get(position);
        if(item.getId().equals(LoginedUser.id)){
            ViewHolder0 holder0 = (ViewHolder0) holder;         //데이터가 나일 때 바인딩하는 것
            holder0.setItem0(item);
        }else{
            ViewHolder1 holder1 = (ViewHolder1) holder;             //데이터가 상대일 때 바인딩하는 것
            if(!item.getUri().equals("")){
                Glide.with(context).load(item.getUri()).apply(new RequestOptions().circleCrop()).into(holder1.personimage);
            }else {
                holder1.personimage.setImageResource(R.drawable.member);
            }
            holder1.setItem1(item);
        }
    }

    @Override
    public int getItemCount() {             //arraylist 아이템이 몇개 인지 알 수 있는 게터
        return items.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder{            //뷰홀더 클래스

        TextView personname1;                //텍스트뷰 선언
        TextView sendedtext1;
        TextView sendedtime1;
        ImageView personimage;

        public ViewHolder1(View itemView){           //뷰홀더 생성자.. xml이랑 연동
            super(itemView);
            personimage = itemView.findViewById(R.id.imginconsult);
            personname1 = itemView.findViewById(R.id.personname);
            sendedtext1 = itemView.findViewById(R.id.sendedtext);
            sendedtime1 = itemView.findViewById(R.id.sendedtime);
        }

        public void setItem1(Consult_Data item){         //상담데이터 아이템과 연동해주는 쎄터
            personname1.setText(item.getName());
            sendedtext1.setText(item.getText());
            sendedtime1.setText(item.getTime());
        }
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder{
        TextView sendingtext0;                //텍스트뷰 선언
        TextView sendingtime0;

        public ViewHolder0(View itemView){
            super(itemView);
            sendingtext0 = itemView.findViewById(R.id.sendingtext);
            sendingtime0 = itemView.findViewById(R.id.sendingtime);
        }

        public void setItem0(Consult_Data item){
            sendingtext0.setText(item.getText());
            sendingtime0.setText(item.getTime());
        }
    }


    public void addItem(Consult_Data item){                         //아이템 추가하는 add 메소드
        items.add(item);
    }

    public void setItems(ArrayList<Consult_Data> items) {           //arraylist 급으로 아이템 정하는 쎄터
        this.items = items;
    }

    public Consult_Data getItem(int position){          //해당 위치의 아이템을 리턴해주는 게터
        return items.get(position);
    }

    public void setItem(int position, Consult_Data item){       //해당 포지션의 아이템을 백업해주는 셋터?
        this.items.set(position, item);
    }
}
