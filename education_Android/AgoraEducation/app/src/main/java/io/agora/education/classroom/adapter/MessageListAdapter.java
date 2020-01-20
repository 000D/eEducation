package io.agora.education.classroom.adapter;

import android.content.res.Resources;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.education.R;
import io.agora.education.base.BaseListAdapter;
import io.agora.education.classroom.bean.msg.ChannelMsg;

public class MessageListAdapter extends BaseListAdapter<ChannelMsg, MessageListAdapter.ViewHolder> {

    @Override
    protected void onBindViewHolder(MessageListAdapter.ViewHolder viewHolder, ChannelMsg msg, int position) {
        Resources resources = viewHolder.itemView.getContext().getResources();
        viewHolder.tv_name.setText(msg.account);
        if (TextUtils.isEmpty(msg.link)) {
            viewHolder.tv_content.setText(msg.content);
            viewHolder.tv_content.setTextColor(resources.getColor(R.color.gray_666666));
            viewHolder.tv_content.getPaint().setFlags(0);
        } else {
            viewHolder.tv_content.setText(resources.getString(R.string.replay_recording));
            viewHolder.tv_content.setTextColor(resources.getColor(R.color.blue_1F3DE8));
            viewHolder.tv_content.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    @Override
    protected MessageListAdapter.ViewHolder onCreateViewHolder(int itemViewType, ViewGroup parent) {
        View view;
        if (itemViewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_msg_other, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_msg_me, parent, false);
        }
        return new MessageListAdapter.ViewHolder(view);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getList().get(position).isMe) {
            return 1;
        } else {
            return 0;
        }
    }

    class ViewHolder extends BaseListAdapter.BaseViewHolder {
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_content)
        TextView tv_content;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
