package com.app.bizlinked.fragments.baseClass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.app.bizlinked.R;
import com.app.bizlinked.activities.MainActivity;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.helpers.animation.AnimationHelpers;
import com.app.bizlinked.helpers.common.KeyboardHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.preference.BasePreferenceHelper;
import com.daimajia.androidanimations.library.Techniques;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class  BaseFragment extends Fragment implements Validator.ValidationListener {

    public static final String TAG = BaseFragment.class.getSimpleName();

    public abstract void onCustomBackPressed();

    public BasePreferenceHelper preferenceHelper;
    protected BaseActivity activityReference;
    public boolean isLoading = false;
    Validator validator;
    Unbinder unbinder;
    View rootView;

    //Abstract Methods
    protected abstract void setTitleBar(TitleBar titleBar);
    protected abstract int getMainLayout();
    protected abstract void onFragmentViewReady(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState, View rootView);
    //Override Methods
    public void onValidationSuccess() {}
    public void onValidationFail() {}
    //public abstract void setTitleBar(TitleBar titleBar);


    public View getContainerLayout(){
        return rootView;
    }
    public void afterBackStackChange(){};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getBaseActivity() != null) {
            preferenceHelper = getBaseActivity().prefHelper;
            activityReference = getBaseActivity();
        }
    }

    public void validateFields() {

        if(activityReference != null)
            KeyboardHelper.hideSoftKeyboard(activityReference, activityReference.getWindow().getDecorView());

        validator.validate();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(getMainLayout(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        onFragmentViewReady(inflater, container, savedInstanceState, rootView);
        validator = new Validator(this);
        validator.setValidationListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = getBaseActivity();
        preferenceHelper = getBaseActivity().prefHelper;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getBaseActivity() != null) {
            setTitleBar(getBaseActivity().getTitleBar());
        }

//        if (getHomeActivity() != null) {
//            setTitleBar(getHomeActivity().getTitleBar());
//        }
//
//        if (getLoginActivity() != null) {
//            setTitleBar(getLoginActivity().getTitleBar());
//        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getBaseActivity().getWindow() != null)
            if (getBaseActivity().getWindow().getDecorView() != null)
                KeyboardHelper.hideSoftKeyboard(getBaseActivity(), getBaseActivity()
                        .getWindow().getDecorView());

    }

    protected BaseActivity getBaseActivity() {

        if (getActivity() instanceof BaseActivity) {
            return (BaseActivity) getActivity();
        }
        return null;
    }

    protected MainActivity getMainActivity() {

        if (getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        }
        return null;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onValidationSucceeded() {
        //Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        onValidationSuccess();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
//        for (ValidationError error : errors) {
//            View view = error.getView();
//            String message = error.getCollatedErrorMessage(activityReference);
        ValidationError error = errors.get(0);
        View view = error.getView();
        String message = "";
        message = error.getFailedRules().get(0).getMessage(activityReference);

        // Display error messages ;)
        if (view instanceof EditText) {
            //((EditText) view).setError(message);
            Utils.showSnackBar(activityReference, view, message, ContextCompat.getColor(activityReference, R.color.grayColor));
            AnimationHelpers.animate(Techniques.RubberBand, 300, view);
            view.requestFocus();
        } else {
            Utils.showSnackBar(activityReference, view, message, ContextCompat.getColor(activityReference, R.color.grayColor));
        }
        onValidationFail();
    }


    public void loadingStarted() {
        isLoading = true;
        if (getBaseActivity() != null)
            getBaseActivity().onLoadingStarted();
    }

    public void loadingFinished() {
        isLoading = false;
        if (getBaseActivity() != null)
            getBaseActivity().onLoadingFinished();

    }

}
