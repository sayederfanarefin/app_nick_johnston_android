// Generated code from Butter Knife. Do not modify!
package com.bulbinc.nick;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SignupActivity$$ViewBinder<T extends com.bulbinc.nick.SignupActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492998, "field '_nameText'");
    target._nameText = finder.castView(view, 2131492998, "field '_nameText'");
    view = finder.findRequiredView(source, 2131492986, "field '_emailText'");
    target._emailText = finder.castView(view, 2131492986, "field '_emailText'");
    view = finder.findRequiredView(source, 2131492987, "field '_passwordText'");
    target._passwordText = finder.castView(view, 2131492987, "field '_passwordText'");
    view = finder.findRequiredView(source, 2131492999, "field '_reEnterPasswordText'");
    target._reEnterPasswordText = finder.castView(view, 2131492999, "field '_reEnterPasswordText'");
    view = finder.findRequiredView(source, 2131493000, "field '_signupButton'");
    target._signupButton = finder.castView(view, 2131493000, "field '_signupButton'");
    view = finder.findRequiredView(source, 2131493003, "field '_loginLink'");
    target._loginLink = finder.castView(view, 2131493003, "field '_loginLink'");
  }

  @Override public void unbind(T target) {
    target._nameText = null;
    target._emailText = null;
    target._passwordText = null;
    target._reEnterPasswordText = null;
    target._signupButton = null;
    target._loginLink = null;
  }
}
