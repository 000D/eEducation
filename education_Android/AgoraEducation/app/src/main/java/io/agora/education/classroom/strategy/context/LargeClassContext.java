package io.agora.education.classroom.strategy.context;

import android.content.Context;

import androidx.annotation.NonNull;

import io.agora.education.classroom.bean.msg.Cmd;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.strategy.channel.ChannelStrategy;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.manager.RtcManager;

public class LargeClassContext extends ClassContext {

    LargeClassContext(Context context, ChannelStrategy strategy) {
        super(context, strategy);
    }

    @Override
    public void checkChannelEnterable(@NonNull ResultCallback<Boolean> callback) {
        callback.onSuccess(true);
    }

    @Override
    void preConfig() {
        RtcManager.instance().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        RtcManager.instance().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        RtcManager.instance().enableDualStreamMode(false);
    }

    @Override
    public void onPeerMsgReceived(PeerMsg msg) {
        super.onPeerMsgReceived(msg);
        Cmd cmd = msg.getCmd();
        if (cmd == null) return;
        switch (cmd) {
            case ACCEPT:
                accept();
                break;
            case REJECT:
                reject();
                break;
        }
    }

    public void apply(boolean isPrepare) {
        if (isPrepare) {
            channelStrategy.updateLocalAttribute(channelStrategy.getLocal(), null);
        } else {
            channelStrategy.getTeacher().sendMessageTo(Cmd.APPLY);
        }
    }

    public void cancel() {
        channelStrategy.clearLocalAttribute(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                channelStrategy.getTeacher().sendMessageTo(Cmd.CANCEL);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    private void accept() {
        Student local = channelStrategy.getLocal();
        local.audio = 1;
        local.video = 1;
        channelStrategy.updateLocalAttribute(local, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RtcManager.instance().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    private void reject() {
        channelStrategy.clearLocalAttribute(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RtcManager.instance().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

}
