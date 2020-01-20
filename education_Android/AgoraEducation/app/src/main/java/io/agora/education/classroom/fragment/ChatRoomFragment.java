package io.agora.education.classroom.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.BindView;
import io.agora.education.R;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.BaseClassActivity;
import io.agora.education.classroom.ReplayActivity;
import io.agora.education.classroom.adapter.MessageListAdapter;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.mediator.MsgMediator;

public class ChatRoomFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnKeyListener {

    @BindView(R.id.lv_msg)
    protected ListView lv_msg;
    @BindView(R.id.edit_send_msg)
    protected EditText edit_send_msg;

    private MessageListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_chatroom;
    }

    @Override
    protected void initData() {
        adapter = new MessageListAdapter();
    }

    @Override
    protected void initView() {
        lv_msg.setAdapter(adapter);
        lv_msg.setOnItemClickListener(this);
        edit_send_msg.setOnKeyListener(this);
    }

    public void setEditTextEnable(boolean isEnable) {
        runOnUiThread(() -> {
            if (edit_send_msg != null) {
                edit_send_msg.setEnabled(isEnable);
                if (isEnable) {
                    edit_send_msg.setHint(R.string.hint_im_message);
                } else {
                    edit_send_msg.setHint(R.string.chat_muting);
                }
            }
        });
    }

    public void addMessage(ChannelMsg channelMsg) {
        runOnUiThread(() -> {
            if (lv_msg != null) {
                adapter.addItem(channelMsg);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ChannelMsg msg = adapter.getItem(position);
        msg.link = "/replay/c97c09499a6e4a7086b1d3c3802c634d/1579145682784/1579145699157";
        msg.url = "https://beings.oss-cn-hangzhou.aliyuncs.com/8a44107d5546925853e1c792a1309343_281dc9bdb52d04dc20036dbd8313ed055.m3u8";
        if (!TextUtils.isEmpty(msg.link)) {
            String[] strings = msg.link.split("/");
            String uuid = strings[2];
            long startTime = Long.parseLong(strings[3]);
            long endTime = Long.parseLong(strings[4]);
            Intent intent = new Intent(context, ReplayActivity.class);
            intent.putExtra(ReplayActivity.WHITE_BOARD_UID, uuid);
            intent.putExtra(ReplayActivity.WHITE_BOARD_START_TIME, startTime);
            intent.putExtra(ReplayActivity.WHITE_BOARD_END_TIME, endTime);
            intent.putExtra(ReplayActivity.WHITE_BOARD_URL, msg.url);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (!edit_send_msg.isEnabled()) {
            return false;
        }
        String text = edit_send_msg.getText().toString();
        if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction() && text.trim().length() > 0) {
            if (context instanceof BaseClassActivity) {
                ChannelMsg msg = new ChannelMsg(((BaseClassActivity) context).getMyName(), text);
                MsgMediator.sendMessage(msg);
                addMessage(msg);
                edit_send_msg.setText("");
            }
            return true;
        }
        return false;
    }

}
