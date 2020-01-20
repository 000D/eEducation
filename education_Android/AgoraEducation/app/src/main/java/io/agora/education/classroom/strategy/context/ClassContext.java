package io.agora.education.classroom.strategy.context;

import android.app.Activity;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import io.agora.education.R;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.msg.Cmd;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.strategy.channel.ChannelEventListener;
import io.agora.education.classroom.strategy.channel.ChannelStrategy;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.manager.RtcManager;
import io.agora.sdk.manager.RtmManager;
import io.agora.sdk.manager.SdkManager;

public abstract class ClassContext implements ChannelEventListener {

    private Context context;
    private ChannelEventListener channelEventListener;

    public ChannelStrategy channelStrategy;

    ClassContext(Context context, ChannelStrategy strategy) {
        this.context = context;
        channelStrategy = strategy;
        channelStrategy.setChannelEventListener(this);
    }

    public void setChannelEventListener(ChannelEventListener listener) {
        channelEventListener = listener;
    }

    public abstract void checkChannelEnterable(@NotNull ResultCallback<Boolean> callback);

    public void joinChannel() {
        RtmManager.instance().joinChannel(new HashMap<String, String>() {{
            put(SdkManager.CHANNEL_ID, channelStrategy.getChannelId());
        }});
        preConfig();
        RtcManager.instance().joinChannel(new HashMap<String, String>() {{
            put(SdkManager.TOKEN, context.getString(R.string.agora_rtc_token));
            put(SdkManager.CHANNEL_ID, channelStrategy.getChannelId());
            put(SdkManager.USER_ID, channelStrategy.getLocal().getUserId());
        }});
    }

    public void leaveChannel() {
        RtmManager.instance().leaveChannel();
        RtcManager.instance().leaveChannel();
    }

    abstract void preConfig();

    public void muteLocalAudio(boolean isMute) {
        Student local = channelStrategy.getLocal();
        local.audio = isMute ? 0 : 1;
        channelStrategy.updateLocalAttribute(local, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RtcManager.instance().muteLocalAudioStream(isMute);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void muteLocalVideo(boolean isMute) {
        Student local = channelStrategy.getLocal();
        local.video = isMute ? 0 : 1;
        channelStrategy.updateLocalAttribute(local, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RtcManager.instance().muteLocalVideoStream(isMute);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void muteLocalChat(boolean isMute) {
        Student local = channelStrategy.getLocal();
        local.chat = isMute ? 0 : 1;
        channelStrategy.updateLocalAttribute(local, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void release() {
        channelStrategy.clearLocalAttribute(null);
        channelStrategy.release();
        leaveChannel();
    }

    private void runListener(Runnable runnable) {
        if (channelEventListener != null) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(runnable);
            }
        }
    }

    @Override
    public void onChannelInfoInit() {
        runListener(() -> channelEventListener.onChannelInfoInit());
    }

    @Override
    public void onLocalChanged(Student local) {
        runListener(() -> channelEventListener.onLocalChanged(local));
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        runListener(() -> channelEventListener.onTeacherChanged(teacher));
    }

    @Override
    public void onStudentsChanged(List<Student> students) {
        runListener(() -> channelEventListener.onStudentsChanged(students));
    }

    @Override
    public void onChannelMsgReceived(ChannelMsg msg) {
        runListener(() -> channelEventListener.onChannelMsgReceived(msg));
    }

    @Override
    public void onPeerMsgReceived(PeerMsg msg) {
        Cmd cmd = msg.getCmd();
        if (cmd == null) return;
        switch (cmd) {
            case MUTE_AUDIO:
                muteLocalAudio(true);
                break;
            case UNMUTE_AUDIO:
                muteLocalAudio(false);
                break;
            case MUTE_VIDEO:
                muteLocalVideo(true);
                break;
            case UNMUTE_VIDEO:
                muteLocalVideo(false);
                break;
            case MUTE_CHAT:
                muteLocalChat(true);
                break;
            case UNMUTE_CAHT:
                muteLocalChat(false);
                break;
        }
        runListener(() -> channelEventListener.onPeerMsgReceived(msg));
    }

    @Override
    public void onScreenShareJoined(int uid) {
        runListener(() -> channelEventListener.onScreenShareJoined(uid));
    }

    @Override
    public void onScreenShareOffline(int uid) {
        runListener(() -> channelEventListener.onScreenShareOffline(uid));
    }

}
