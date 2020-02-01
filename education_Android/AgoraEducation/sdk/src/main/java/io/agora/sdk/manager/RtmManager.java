package io.agora.sdk.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.agora.base.LogManager;
import io.agora.rtm.ChannelAttributeOptions;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;
import io.agora.sdk.BuildConfig;
import io.agora.sdk.listener.RtmEventListener;

public final class RtmManager extends SdkManager<RtmClient> implements RtmClientListener, RtmChannelListener {

    private final LogManager log = new LogManager(this.getClass().getName());

    private List<RtmEventListener> listeners;
    private RtmChannel rtmChannel;

    private static RtmManager instance;

    private RtmManager() {
        listeners = new ArrayList<>();
    }

    public static RtmManager instance() {
        if (instance == null) {
            synchronized (RtmManager.class) {
                if (instance == null)
                    instance = new RtmManager();
            }
        }
        return instance;
    }

    @Override
    protected RtmClient creakSdk(Context context, String appId) throws Exception {
        return RtmClient.createInstance(context, appId, this);
    }

    @Override
    protected void configSdk() {
        if (BuildConfig.DEBUG) {
            sdk.setParameters("{\"rtm.log_filter\": 65535}");
        }
    }

    @Override
    public void joinChannel(Map<String, String> data) {
        String channelId = data.get(CHANNEL_ID);
        rtmChannel = sdk.createChannel(channelId, this);
        rtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                log.d("join success %s", channelId);
                for (RtmEventListener listener : listeners) {
                    listener.onJoinChannelSuccess(channelId);
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void login(String rtmToken, int userId) {
        sdk.login(rtmToken, String.valueOf(userId), null);
    }

    @Override
    public void leaveChannel() {
        if (rtmChannel != null) {
            rtmChannel.leave(null);
            rtmChannel.release();
            rtmChannel = null;
        }
    }

    @Override
    protected void destroySdk() {
        sdk.release();
    }

    public void registerListener(RtmEventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(RtmEventListener listener) {
        listeners.remove(listener);
    }

    public void queryPeersOnlineStatus(Set<String> set, @NonNull ResultCallback<Map<String, Boolean>> callback) {
        sdk.queryPeersOnlineStatus(set, callback);
    }

    public void getChannelAttributes(String channelId, @NonNull ResultCallback<List<RtmChannelAttribute>> callback) {
        sdk.getChannelAttributes(channelId, new ResultCallback<List<RtmChannelAttribute>>() {
            @Override
            public void onSuccess(List<RtmChannelAttribute> rtmChannelAttributes) {
                callback.onSuccess(rtmChannelAttributes);
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                callback.onFailure(errorInfo);
            }
        });
    }

    public void addOrUpdateChannelAttributes(String channelId, List<RtmChannelAttribute> attributes, @Nullable ResultCallback<Void> callback) {
        sdk.addOrUpdateChannelAttributes(
                channelId,
                attributes,
                new ChannelAttributeOptions(true),
                new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        log.d("addOrUpdateChannelAttributes success");
                        if (callback != null)
                            callback.onSuccess(aVoid);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        if (callback != null)
                            callback.onFailure(errorInfo);
                    }
                }
        );
    }

    public void deleteChannelAttributesByKeys(String channelId, List<String> keys, @Nullable ResultCallback<Void> callback) {
        sdk.deleteChannelAttributesByKeys(
                channelId,
                keys,
                new ChannelAttributeOptions(true),
                new ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        log.d("deleteChannelAttributesByKeys success");
                        if (callback != null)
                            callback.onSuccess(aVoid);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        if (callback != null)
                            callback.onFailure(errorInfo);
                    }
                });
    }

    public void sendMessageToPeer(String userId, String message) {
        sdk.sendMessageToPeer(userId, sdk.createMessage(message), null, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                log.d("sendMessageToPeer success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {

            }
        });
    }

    public void sendMessage(String message) {
        if (rtmChannel != null) {
            rtmChannel.sendMessage(sdk.createMessage(message), new ResultCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    log.d("sendMessage success");
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
    }

    @Override
    public void onMemberCountUpdated(int i) {
        for (RtmEventListener listener : listeners) {
            listener.onMemberCountUpdated(i);
        }
    }

    @Override
    public void onAttributesUpdated(List<RtmChannelAttribute> list) {
        for (RtmEventListener listener : listeners) {
            listener.onAttributesUpdated(list);
        }
    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, RtmChannelMember rtmChannelMember) {
        log.i("onChannelMessageReceived %s from %s", rtmMessage.getText(), rtmChannelMember.getUserId());
        for (RtmEventListener listener : listeners) {
            listener.onMessageReceived(rtmMessage, rtmChannelMember);
        }
    }

    @Override
    public void onMemberJoined(RtmChannelMember rtmChannelMember) {
        log.i("onMemberJoined %s", rtmChannelMember.getUserId());
        for (RtmEventListener listener : listeners) {
            listener.onMemberJoined(rtmChannelMember);
        }
    }

    @Override
    public void onMemberLeft(RtmChannelMember rtmChannelMember) {
        log.i("onMemberLeft %s", rtmChannelMember.getUserId());
        for (RtmEventListener listener : listeners) {
            listener.onMemberLeft(rtmChannelMember);
        }
    }

    @Override
    public void onConnectionStateChanged(int i, int i1) {
        log.i("onConnectionStateChanged %d %d", i, i1);
        for (RtmEventListener listener : listeners) {
            listener.onConnectionStateChanged(i, i1);
        }
    }

    @Override
    public void onMessageReceived(RtmMessage rtmMessage, String s) {
        log.i("onPeerMessageReceived %s from %s", rtmMessage.getText(), s);
        for (RtmEventListener listener : listeners) {
            listener.onMessageReceived(rtmMessage, s);
        }
    }

    @Override
    public void onTokenExpired() {
        for (RtmEventListener listener : listeners) {
            listener.onTokenExpired();
        }
    }

    @Override
    public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
        for (RtmEventListener listener : listeners) {
            listener.onPeersOnlineStatusChanged(map);
        }
    }

}
