package com.afollestad.aesthetic.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;
import static com.afollestad.aesthetic.TintHelper.createTintedDrawable;

/** @author Aidan Follestad (afollestad) */
@SuppressWarnings("RestrictedApi")
@RestrictTo(LIBRARY_GROUP)
public class AestheticActionMenuItemView extends ActionMenuItemView {

  private Drawable icon;
  private Subscription subscription;

  public AestheticActionMenuItemView(Context context) {
    super(context);
  }

  public AestheticActionMenuItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticActionMenuItemView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setIcon(final Drawable icon) {
    // We need to retrieve the color again here.
    // For some reason, without this, a transparent color is used and the icon disappears
    // when the overflow menu opens.
    Aesthetic.get()
        .iconTitleColor()
        .observeOn(AndroidSchedulers.mainThread())
        .take(1)
        .subscribe(color -> setIcon(icon, color.toEnabledSl()), onErrorLogAndRethrow());
  }

  public void setIcon(final Drawable icon, ColorStateList colors) {
    this.icon = icon;
    super.setIcon(createTintedDrawable(icon, colors));
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subscription =
        Aesthetic.get()
            .iconTitleColor()
            .compose(distinctToMainThread())
            .subscribe(
                colors -> {
                  if (icon != null) {
                    setIcon(icon, colors.toEnabledSl());
                  }
                },
                onErrorLogAndRethrow());
  }

  @Override
  protected void onDetachedFromWindow() {
    subscription.unsubscribe();
    super.onDetachedFromWindow();
  }
}
