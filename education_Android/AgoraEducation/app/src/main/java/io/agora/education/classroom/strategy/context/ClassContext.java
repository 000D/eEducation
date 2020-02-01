package io.agora.education.classroom.strategy.context;

import android.app.Activity;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.agora.education.R;
import io.agora.education.classroom.bean.channel.ChannelInfo;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.msg.Cmd;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.strategy.ChannelEventListener;
import io.agora.education.classroom.strategy.ChannelStrategy;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.listener.RtcEventListener;
import io.agora.sdk.manager.RtcManager;

public abstract class ClassContext implements ChannelEventListener {

    private Context context;

    ChannelStrategy channelStrategy;
    ClassEventListener classEventListener;

    ClassContext(Context context, ChannelStrategy strategy) {
        this.context = context;
        channelStrategy = strategy;
        channelStrategy.setChannelEventListener(this);
        RtcManager.instance().registerListener(rtcEventListener);
    }

    public void setClassEventListener(ClassEventListener listener) {
        classEventListener = listener;
    }

    public abstract void checkChannelEnterable(@NotNull ResultCallback<Boolean> callback);

    public void joinChannel() {
        preConfig();
        channelStrategy.joinChannel(context.getString(R.string.agora_rtc_token));
    }

    public void leaveChannel() {
        channelStrategy.leaveChannel();
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
        RtcManager.instance().unregisterListener(rtcEventListener);
        leaveChannel();
    }

    void runListener(Runnable runnable) {
        if (classEventListener != null) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(runnable);
            }
        }
    }

    @Override
    public void onChannelInfoInit() {
        runListener(() -> classEventListener.onTeacherInit(channelStrategy.getTeacher()));
    }

    @Override
    public void onLocalChanged(Student local) {
        runListener(() -> classEventListener.onMuteLocalChat(local.chat == 0));
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        runListener(() -> {
            classEventListener.onClassStateChanged(teacher.class_state == 1);
            classEventListener.onWhiteboardIdChanged(teacher.whiteboard_uid);
            classEventListener.onMuteAllChat(teacher.mute_chat == 1);
        });
    }

    @Override
    public void onStudentsChanged(List<Student> students) {
    }

    @Override
    public void onChannelMsgReceived(ChannelMsg msg) {
        runListener(() -> classEventListener.onChannelMsgReceived(msg));
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
    }

    private RtcEventListener rtcEventListener = new RtcEventListener() {
        @Override
        public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
            if (uid == 0) {
                runListener(() -> classEventListener.onNetworkQualityChanged(Math.max(txQuality, rxQuality)));
            }
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            if (uid == ChannelInfo.SHARE_UID) {
                runListener(() -> classEventListener.onScreenShareJoined(uid));
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            if (uid == ChannelInfo.SHARE_UID) {
                runListener(() -> classEventListener.onScreenShareOffline(uid));
            }
        }
    };

}
