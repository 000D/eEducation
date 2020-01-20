package io.agora.education.classroom.fragment;

import android.view.View;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import io.agora.education.base.BaseFragment;
import io.agora.education.classroom.BaseClassActivity;
import io.agora.education.classroom.adapter.StudentListAdapter;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.R;

public class StudentListFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.lv_students)
    protected ListView lv_students;

    private StudentListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_student_list;
    }

    @Override
    protected void initData() {
        if (context instanceof BaseClassActivity) {
            adapter = new StudentListAdapter(((BaseClassActivity) context).getMyUserId());
            adapter.setOnClickListener(this);
        }
    }

    @Override
    protected void initView() {
        lv_students.setAdapter(adapter);
    }

    public void setStudentList(List<Student> students) {
        adapter.setList(students);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (context instanceof BaseClassActivity) {
            boolean isSelected = view.isSelected();
            switch (view.getId()) {
                case R.id.iv_btn_mute_audio:
                    ((BaseClassActivity) context).classContext.muteLocalAudio(isSelected);
                    break;
                case R.id.iv_btn_mute_video:
                    ((BaseClassActivity) context).classContext.muteLocalVideo(isSelected);
                    break;
            }
        }
    }

}
