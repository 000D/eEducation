package io.agora.education.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T, VH extends BaseListAdapter.BaseViewHolder> extends BaseAdapter {

    private List<T> list = new ArrayList<>();

    public List<T> getList() {
        return list;
    }

    public void setList(@NonNull List<T> mList) {
        this.list = mList;
    }

    public void addItem(T item) {
        this.list.add(item);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH viewHolder;
        if (convertView == null) {
            viewHolder = onCreateViewHolder(getItemViewType(position), parent);
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VH) convertView.getTag();
        }
        onBindViewHolder(viewHolder, list.get(position), position);
        return convertView;
    }

    protected abstract void onBindViewHolder(VH viewHolder, T t, int position);

    protected abstract VH onCreateViewHolder(int itemViewType, ViewGroup parent);

    public static class BaseViewHolder {
        public View itemView;

        public BaseViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }

}
