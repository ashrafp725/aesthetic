package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Subscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

/** @author Aidan Follestad (afollestad) */
@RestrictTo(LIBRARY_GROUP)
public class AestheticDrawerLayout extends DrawerLayout {

  private ActiveInactiveColors lastState;
  private DrawerArrowDrawable arrowDrawable;
  private Subscription subscription;

  public AestheticDrawerLayout(Context context) {
    super(context);
  }

  public AestheticDrawerLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void invalidateColor(ActiveInactiveColors colors) {
    if (colors == null) {
      return;
    }
    this.lastState = colors;
    if (this.arrowDrawable != null) {
      this.arrowDrawable.setColor(lastState.activeColor());
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .iconTitleColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColor, onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }

  @Override
  public void addDrawerListener(@NonNull DrawerListener listener) {
    super.addDrawerListener(listener);
    if (listener instanceof ActionBarDrawerToggle) {
      this.arrowDrawable = ((ActionBarDrawerToggle) listener).getDrawerArrowDrawable();
    }
    invalidateColor(lastState);
  }

  @SuppressWarnings("deprecation")
  @Override
  public void setDrawerListener(DrawerListener listener) {
    super.setDrawerListener(listener);
    if (listener instanceof ActionBarDrawerToggle) {
      this.arrowDrawable = ((ActionBarDrawerToggle) listener).getDrawerArrowDrawable();
    }
    invalidateColor(lastState);
  }
}
