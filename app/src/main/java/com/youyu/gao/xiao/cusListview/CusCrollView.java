package com.youyu.gao.xiao.cusListview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @Author zsj
 * @Date 2018-12-08 0:46
 * @Commit
 */
public class CusCrollView extends GridView {
    public CusCrollView(Context context) {
        super(context);
    }

    public CusCrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusCrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
