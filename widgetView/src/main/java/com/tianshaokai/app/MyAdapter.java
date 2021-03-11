package com.tianshaokai.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<MyFunction> myFunctionList;

    public MyAdapter(List<MyFunction> myFunctionList) {
        this.myFunctionList = myFunctionList;
    }

    private OnItemClickListener onItemClickListener;//声明一下这个接口
    //提供setter方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据RecyclerView获得当前View的位置
                int position = viewHolder.getAdapterPosition();
                //程序执行到此，会去执行具体实现的onItemClick()方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position, myFunctionList.get(position));
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        MyFunction myFunction = myFunctionList.get(position);
        viewHolder.onBindViewData(myFunction);

    }

    @Override
    public int getItemCount() {
        return myFunctionList == null ? 0 : myFunctionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

        public void onBindViewData(MyFunction myFunction) {
            textView.setText(myFunction.getText());
        }
    }
}
