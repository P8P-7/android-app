package nl.team_goliath.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class NoSwipePager extends ViewPager {
    public NoSwipePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof JoystickView || v instanceof ConstraintLayout) {
            return true;
        }

        return super.canScroll(v, checkV, dx, x, y);
    }
}