package com.example.virtualmeetingapp.SpaceTabLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.virtualmeetingapp.SpaceTabLayout.SpaceTabLayoutUserType.SpaceTabLayout2;
import com.google.android.material.snackbar.Snackbar;

public class SpaceTabLayoutBehavior2 extends CoordinatorLayout.Behavior<SpaceTabLayout2> {

    public SpaceTabLayoutBehavior2(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, SpaceTabLayout2 child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, SpaceTabLayout2 child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}
