package com.hazelwood.partypal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by Hazelwood on 11/13/14.
 */
public class Custom_GRIDVIEW extends GridView {
    public Custom_GRIDVIEW(Context context) {
        super(context);
    }
    public Custom_GRIDVIEW(Context c, AttributeSet set){
        super(c, set);
    }
    public Custom_GRIDVIEW(Context c, AttributeSet set, int style){
        super(c, set, style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int customHeight = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();

        super.onMeasure(widthMeasureSpec, customHeight);
    }
}
