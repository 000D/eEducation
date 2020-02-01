package io.agora.education.classroom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.education.R;
import io.agora.education.base.BaseListAdapter;
import io.agora.education.classroom.bean.user.User;

public class UserListAdapter extends BaseListAdapter<User, UserListAdapter.ViewHolder> {

    private int localUid;
    private View.OnClickListener listener;

    public UserListAdapter(int localUid) {
        this.localUid = localUid;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(UserListAdapter.ViewHolder viewHolder, User user, int position) {
        viewHolder.tv_name.setText(user.account);
        if (user.uid == localUid) {
            viewHolder.iv_btn_mute_audio.setVisibility(View.VISIBLE);
            viewHolder.iv_btn_mute_video.setVisibility(View.VISIBLE);
            viewHolder.iv_btn_mute_audio.setSelected(user.audio == 1);
            viewHolder.iv_btn_mute_video.setSelected(user.video == 1);
            viewHolder.iv_btn_mute_audio.setOnClickListener(listener);
            viewHolder.iv_btn_mute_video.setOnClickListener(listener);
        } else {
            viewHolder.iv_btn_mute_video.setVisibility(View.GONE);
            viewHolder.iv_btn_mute_audio.setVisibility(View.GONE);
        }
    }

    @Override
    protected UserListAdapter.ViewHolder onCreateViewHolder(int itemViewType, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_list, parent, false);
        return new UserListAdapter.ViewHolder(view);
    }

    class ViewHolder extends BaseListAdapter.BaseViewHolder {
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.iv_btn_mute_audio)
        ImageView iv_btn_mute_audio;
        @BindView(R.id.iv_btn_mute_video)
        ImageView iv_btn_mute_video;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
