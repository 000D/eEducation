package io.agora.education.classroom.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.agora.education.R;
import io.agora.education.classroom.bean.user.User;
import io.agora.education.classroom.mediator.VideoMediator;
import io.agora.education.classroom.widget.RtcVideoView;

public class ClassVideoAdapter extends RecyclerView.Adapter<ClassVideoAdapter.ViewHolder> {

    private int localUid;
    private List<User> users;

    public ClassVideoAdapter(int localUid) {
        this.localUid = localUid;
        this.users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        this.users.clear();
        for (User user : users) {
            if (user.uid == localUid || user.isRtcOnline)
                this.users.add(user);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RtcVideoView item = new RtcVideoView(context);
        item.init(R.layout.layout_video_small_class, false);
        int width = context.getResources().getDimensionPixelSize(R.dimen.dp_95);
        int height = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        item.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RtcVideoView view = (RtcVideoView) holder.itemView;
        User user = users.get(position);
        view.muteVideo(user.video == 0);
        view.muteAudio(user.audio == 0);
        view.setName(user.account);
        view.setTag(user.uid);
        if (user.uid == localUid) {
            VideoMediator.setupLocalVideo(view);
        } else {
            VideoMediator.setupRemoteVideo(view, user.uid);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
