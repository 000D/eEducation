package io.agora.education.classroom.widget.whiteboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.herewhite.sdk.domain.Appliance;

import io.agora.education.R;

public class ApplianceView extends RadioGroup {

    private final int[] appliances = new int[]{
            R.drawable.tool_selector,
            R.drawable.tool_pencil,
            R.drawable.tool_text,
            R.drawable.tool_eraser,
            R.drawable.tool_color
    };
    private final int[] ids = new int[]{
            R.id.tool_selector,
            R.id.tool_pencil,
            R.id.tool_text,
            R.id.tool_eraser,
            R.id.tool_color
    };

    public ApplianceView(Context context) {
        this(context, null);
    }

    public ApplianceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        int cellSize = getResources().getDimensionPixelSize(R.dimen.dp_38);
        int margin = getResources().getDimensionPixelSize(R.dimen.dp_4);
        for (int i = 0; i < appliances.length; i++) {
            RadioButton button = initRadioButton(i, cellSize);

            addView(button, cellSize, cellSize);
            if (getOrientation() == VERTICAL) {
                ((LayoutParams) button.getLayoutParams()).topMargin = i == 0 ? 0 : margin;
            } else {
                ((LayoutParams) button.getLayoutParams()).leftMargin = i == 0 ? 0 : margin;
            }
        }
    }

    private RadioButton initRadioButton(int index, int size) {
        RadioButton button = new RadioButton(getContext());
        button.setId(ids[index]);
        button.setBackgroundResource(R.drawable.slt_bg_radius_4_565656_141414);
        button.setButtonDrawable(null);

        Drawable drawable = getResources().getDrawable(appliances[index]);
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        int left = (size - width) / 2;
        drawable.setBounds(left, 0, left + width, height);
        button.setCompoundDrawables(drawable, null, null, null);

        return button;
    }

    public int getItemId(@io.agora.whiteboard.netless.annotation.Appliance String appliance) {
        if (appliance != null) {
            switch (appliance) {
                case Appliance.SELECTOR:
                    return R.id.tool_selector;
                case Appliance.PENCIL:
                    return R.id.tool_pencil;
                case Appliance.TEXT:
                    return R.id.tool_text;
                case Appliance.ERASER:
                    return R.id.tool_eraser;
            }
        }
        return 0;
    }

}
