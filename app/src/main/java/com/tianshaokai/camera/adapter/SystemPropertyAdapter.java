package com.tianshaokai.camera.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tianshaokai.camera.R;
import com.tianshaokai.camera.entity.SystemProperty;

import java.util.List;

public class SystemPropertyAdapter extends BaseQuickAdapter<SystemProperty, BaseViewHolder> {

    public SystemPropertyAdapter(@Nullable List<SystemProperty> data) {
        super(R.layout.layout_system_property_list_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, SystemProperty systemProperty) {
        baseViewHolder.setText(R.id.key, systemProperty.key);
        baseViewHolder.setText(R.id.value, systemProperty.value);
    }
}
