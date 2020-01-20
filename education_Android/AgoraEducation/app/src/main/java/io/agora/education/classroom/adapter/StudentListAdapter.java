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
import io.agora.education.classroom.bean.user.Student;

public class StudentListAdapter extends BaseListAdapter<Student, StudentListAdapter.ViewHolder> {

    private int myUid;
    private View.OnClickListener listener;

    public StudentListAdapter(int uid) {
        this.myUid = uid;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(StudentListAdapter.ViewHolder viewHolder, Student student, int position) {
        viewHolder.tv_name.setText(student.account);
        if (myUid == student.uid) {
            viewHolder.iv_btn_mute_audio.setVisibility(View.VISIBLE);
            viewHolder.iv_btn_mute_video.setVisibility(View.VISIBLE);
            viewHolder.iv_btn_mute_audio.setSelected(student.audio == 1);
            viewHolder.iv_btn_mute_video.setSelected(student.video == 1);
            viewHolder.iv_btn_mute_audio.setOnClickListener(listener);
            viewHolder.iv_btn_mute_video.setOnClickListener(listener);
        } else {
            viewHolder.iv_btn_mute_video.setVisibility(View.GONE);
            viewHolder.iv_btn_mute_audio.setVisibility(View.GONE);
        }
    }

    @Override
    protected StudentListAdapter.ViewHolder onCreateViewHolder(int itemViewType, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_list, parent, false);
        return new StudentListAdapter.ViewHolder(view);
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
