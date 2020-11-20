package com.tianshaokai.study.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianshaokai.common.audio.AudioTrackManager;
import com.tianshaokai.study.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioRecordListAdapter extends RecyclerView.Adapter<AudioRecordListAdapter.ViewHolder> {

    private List<File> fileList;

    public AudioRecordListAdapter(List<File> mObjects) {
        this.fileList = new ArrayList<>(mObjects);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_audio_list_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setText(position);
    }

    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.size();
    }

    public @Nullable File getItem(int position) {
        return fileList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFileName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final File item = getItem(getAdapterPosition());
                    if(item == null) return;
                    AudioTrackManager.getInstance().setDataSource(item.getAbsolutePath()).play();
                }
            });
        }

        public void setText(int position) {
            final File item = getItem(position);
            if(item == null) return;
            tvFileName.setText(item.getName());
        }
    }

    public void addFile(String path) {
        if(TextUtils.isEmpty(path)) return;
        File file = new File(path);
        fileList.add(0, file);
        notifyItemInserted(0);
    }

    public void deleteFile() {
        fileList.clear();
        notifyDataSetChanged();
    }
}
