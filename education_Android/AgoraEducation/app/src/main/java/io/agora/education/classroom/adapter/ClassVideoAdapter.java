package io.agora.education.classroom.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.agora.education.R;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.bean.user.User;
import io.agora.education.classroom.mediator.VideoMediator;
import io.agora.education.classroom.widget.RtcVideoView;

public class ClassVideoAdapter extends RecyclerView.Adapter<ClassVideoAdapter.ViewHolder> {

    private Teacher teacher;
    private Student local;
    private List<Student> students = new ArrayList<>();

    public ClassVideoAdapter(Student local) {
        this.local = local;
        this.teacher = new Teacher();
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        notifyItemChanged(0);
    }

    public void setLocal(Student local) {
        this.local = local;
        notifyItemChanged(1);
    }

    public void setStudents(List<Student> students) {
        this.students.clear();
        for (Student student : students) {
            if (student.isRtcOnline)
                this.students.add(student);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RtcVideoView item = new RtcVideoView(context);
        item.init(R.layout.item_user_video_mini_class, false);
        int height = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        item.setLayoutParams(new ViewGroup.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.dp_92), height));
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RtcVideoView view = (RtcVideoView) holder.itemView;
        User user = getItem(position);
        view.muteVideo(user.video == 0);
        view.muteAudio(user.audio == 0);
        view.setName(user.account);
        view.setTag(user.uid);
        if (user.uid == local.uid) {
            VideoMediator.setupLocalVideo(view);
        } else {
            VideoMediator.setupRemoteVideo(view, user.uid);
        }
    }

    public User getItem(int position) {
        User user;
        if (position == 0) {
            user = teacher;
        } else if (position == 1) {
            user = local;
        } else {
            user = students.get(position - 2);
        }
        return user;
    }

    @Override
    public int getItemCount() {
        return 2 + students.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
