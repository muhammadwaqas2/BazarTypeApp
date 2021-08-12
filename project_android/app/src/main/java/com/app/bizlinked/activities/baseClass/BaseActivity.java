package com.app.bizlinked.activities.baseClass;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.app.bizlinked.R;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.customViews.TitleBar;
import com.app.bizlinked.fragments.baseClass.BaseFragment;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.preference.BasePreferenceHelper;
import com.app.bizlinked.listener.MediaTypePicker;
import com.github.ybq.android.spinkit.SpinKitView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yalantis.ucrop.UCrop;
import com.yovenny.videocompress.MediaController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import io.sentry.Sentry;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int CAMERA_PIC_REQUEST = 110;
    private static final int PHOTO_EDITOR_REQUEST_CODE = 112;
    public BasePreferenceHelper prefHelper;
    public static final String KEY_FRAG_FIRST = "firstFrag";
    protected Context context;
    MediaTypePicker mediaPickerListener;
    ArrayList<String> photoPaths;
    public static final String APP_DIR = "VideoCompressor";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed_Videos/";
    public static final String TEMP_DIR = "/Temp/";

    //Abstract Methods
    public abstract TitleBar getTitleBar();

    public abstract int getMainLayoutId();

    public abstract int getFragmentFrameLayoutId();

    protected abstract void onViewReady();

    public BaseFragment baseFragment;
    private boolean loading = false;

    SpinKitView progressBar;
    FrameLayout progressBarContainer;

    //Boolean variable to mark if the transaction is safe
    private boolean isTransactionSafe;

    //Boolean variable to mark if there is any transaction pending
    private boolean isTransactionPending;

    protected ViewGroup getMainView() {
        return (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getMainLayoutId());
        ButterKnife.bind(this);
        context = BaseActivity.this;
        prefHelper = new BasePreferenceHelper(context);

        if (getMainView() != null && getMainView().findViewById(R.id.progressBarContainer) != null)
        //&& getMainView().findViewById(R.id.progressBar) != null)
        {
            progressBarContainer = findViewById(R.id.progressBarContainer);
//            progressBar = findViewById(R.id.progressBar);
        }

        onViewReady();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(getFragmentFrameLayoutId());
                if (currentFragment != null) {
                    Log.e("fragment=", currentFragment.getClass().getSimpleName());
                    baseFragment = (BaseFragment) currentFragment;
                    fragmentBackStackChangeListener(baseFragment);
                    baseFragment.afterBackStackChange();
                }
            }
        });


    }

    public abstract void fragmentBackStackChangeListener(BaseFragment fragment);

    public <T> void changeActivity(Class<T> cls, boolean isActivityFinish) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        startActivity(intent);
        if (isActivityFinish) {
            finish();
        }

    }

    protected <T> void changeActivity(Class<T> cls, Bundle data) {
        Intent resultIntent = new Intent(this, cls);
        if (data != null)
            resultIntent.putExtras(data);
        startActivity(resultIntent);
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    public void addSupportFragment(BaseFragment frag, int transition) {

//        if(isTransactionSafe){
        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        if (transition == AppConstant.TRANSITION_TYPES.FADE)
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        else if (transition == AppConstant.TRANSITION_TYPES.SLIDE)
//            transaction.setCustomAnimations(R.anim.anim_enter, 0);
        //transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );

        transaction.replace(getFragmentFrameLayoutId(), frag, frag.getClass().getName());
        transaction.addToBackStack(getSupportFragmentManager().getBackStackEntryCount() == 0 ?
                KEY_FRAG_FIRST : null)
                .commitAllowingStateLoss();// AllowingStateLoss();
//        }else{
//             /*
//     If any transaction is not done because the activity is in background. We set the
//     isTransactionPending variable to true so that we can pick this up when we come back to
//foreground
//     */
//            isTransactionPending = true;
//        }
    }


    public void replaceSupportFragment(BaseFragment frag, int transition) {


        try {
            baseFragment = frag;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (transition == AppConstant.TRANSITION_TYPES.FADE)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            else if (transition == AppConstant.TRANSITION_TYPES.SLIDE)
                transaction.setCustomAnimations(R.anim.anim_enter, 0);
            //transaction.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
            transaction.replace(getFragmentFrameLayoutId(), frag, frag.getClass().getName());
            transaction.commitAllowingStateLoss();// AllowingStateLoss();
        }catch (Exception e){
            e.printStackTrace();
            Sentry.captureException(e);
        }
    }


    public void addSupportFragmentWithOutBackStack(BaseFragment frag, int transition) {

        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(getFragmentFrameLayoutId(), frag, frag.getClass().getName());
        transaction.commitAllowingStateLoss();
    }

    public void realAddSupportFragment(BaseFragment frag, int transition) {

        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (transition == AppConstant.TRANSITION_TYPES.FADE)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        else if (transition == AppConstant.TRANSITION_TYPES.SLIDE)
            transaction.setCustomAnimations(R.anim.anim_enter, 0);

        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.add(getFragmentFrameLayoutId(), frag, frag.getClass().getName());

        transaction.addToBackStack(
                getSupportFragmentManager().getBackStackEntryCount() == 0 ? KEY_FRAG_FIRST
                        : null).commitAllowingStateLoss();// AllowingStateLoss();

    }

    public void addSupportFragmentReplace(BaseFragment frag, int transition) {
        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (transition == AppConstant.TRANSITION_TYPES.FADE)
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        else if (transition == AppConstant.TRANSITION_TYPES.SLIDE)
            transaction.setCustomAnimations(R.anim.anim_enter, 0);
        transaction.replace(getFragmentFrameLayoutId(), frag, frag.getClass().getName());
        transaction.addToBackStack(
                getSupportFragmentManager().getBackStackEntryCount() == 0 ? KEY_FRAG_FIRST
                        : null).commitAllowingStateLoss();

    }

    public void addSupportFragmentRemove(BaseFragment frag, String tag) {

        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(getFragmentFrameLayoutId(), frag, tag);
        transaction
                .addToBackStack(null).commitAllowingStateLoss();// AllowingStateLoss();

    }

    public void addSupportFragmentWithData(BaseFragment frag, String tag, Bundle args) {

        frag.setArguments(args);
        baseFragment = frag;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(getFragmentFrameLayoutId(), frag, tag);
        transaction.addToBackStack(null).commitAllowingStateLoss();// AllowingStateLoss();

    }

    public void addSupportFragmentWithContainerView(Fragment frag, int layoutId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(layoutId, frag, frag.getClass().getName());
        transaction.commitAllowingStateLoss();
    }


//    public void addAndShowDialogFragment(DialogFragment dialog) {
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction();
//        dialog.show(transaction, "tag");
//
//    }
//
//    public void prepareAndShowDialog(DialogFragment frag, String TAG) {
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag(TAG);
//
//        if (prev != null)
//            transaction.remove(prev);
//
//        transaction.addToBackStack(null);
//
//        frag.show(transaction, TAG);
//    }
//
//    public void onPageBack() {
//        super.onBackPressed();
//    }
//
//
//    @Override
//    public void onBackPressed() {
//
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
//            baseFragment.onCustomBackPressed();
//        else
//            finish();
//
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        baseFragment.onActivityResult(requestCode, resultCode, data);
//    }


    public void onPageBack() {
        if (!loading) {
            super.onBackPressed();
        } else {
            Utils.showToast(context, context.getString(R.string.please_wait_data_is_loading), AppConstant.TOAST_TYPES.INFO);
        }
    }


    @Override
    public void onBackPressed() {

        if (!loading) {
            if (getSupportFragmentManager().getBackStackEntryCount() >= 1)
                baseFragment.onCustomBackPressed();
            else
                finish();
        } else {
            Utils.showToast(context, context.getString(R.string.please_wait_data_is_loading), AppConstant.TOAST_TYPES.INFO);
        }
    }


    public BaseFragment getLastAddedSuppFragment() {
        return baseFragment;
    }

    public void emptyBackStack() {
        try {
            popBackStackTillEntry(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param entryIndex is the index of fragment to be popped to, for example the
     *                   first fragment will have a index 0;
     */
    public void popBackStackTillEntry(int entryIndex) {
        if (getSupportFragmentManager() == null)
            return;
        if (getSupportFragmentManager().getBackStackEntryCount() <= entryIndex)
            return;
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                entryIndex);
        if (entry != null) {
            getSupportFragmentManager().popBackStackImmediate(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public Fragment isFragmentPresent(String tag) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        if (frag != null) {
            return frag;
        }
//        else {
//         s   for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
//                Fragment selectedFrag = getSupportFragmentManager().getFragments().get(i);
//                if (baseFragment != null && !Utils.isEmptyOrNull(baseFragment.getTag()) && baseFragment.getTag().equalsIgnoreCase(selectedFrag.getTag())) {
//                    return selectedFrag;
//                }
//            }
//        }
        return null;
    }

    public void popFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }


//    @Override
//    public void onValidationSucceeded() {
////        ValidationHelpers.resetAllErrors(getMainContainerView());
//        onValidationSuccess();
//
//    }


//    @Override
//    public void onValidationFailed(List<ValidationError> errors) {
//        ValidationError error = errors.get(0);
//        View view = error.getView();
//        String message = "";
//        message = error.getFailedRules().get(0).getMessage(BaseActivity.this);
//        //ArrayList<CustomTextInputLayout> customInputLayout = ValidationHelpers.traverseAllCustomInputLayout(getMainContainerView());
//        if (view instanceof EditText) {
//             for (int layoutViewIndex = 0; layoutViewIndex < customInputLayout.size(); layoutViewIndex++) {
//                CustomTextInputLayout customTextInputLayout = (CustomTextInputLayout) customInputLayout.get(layoutViewIndex);
//                EditText editText = ValidationHelpers.getEditText(customTextInputLayout);
//
//                if (editText != null) {
//                    if (editText.getId() == view.getId()) {
//
//                        AnimationHelpers.animate(Techniques.RubberBand, 300, customTextInputLayout);
//                        editText.requestFocus();
//
//                        if (!customTextInputLayout.isErrorEnable())
//                            customTextInputLayout.setError(message);
//                        else
//                            customTextInputLayout.setErrorMessage(message);
//                    } else {
//                        customTextInputLayout.errorEnable(false);
//                    }
//                }
//            }
//        } else {
//            Utils.showSnackBar(BaseActivity.this, view, message, ContextCompat.getColor(BaseActivity.this, R.color.grayColor));
//        }
//        onValidationFail();
//    }


    //    @Override
    public void onLoadingStarted() {

        if (progressBarContainer != null) {
            progressBarContainer.setVisibility(View.VISIBLE);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            loading = true;
        }

    }

    //    @Override
    public void onLoadingFinished() {
        progressBarContainer.setVisibility(View.GONE);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        loading = false;

    }


    public void openImagePicker(final MediaTypePicker listener) {

        TedPermission.with(this)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaPickerListener = listener;
                        FilePickerBuilder.getInstance()
//                                    .setActivityTheme(getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource())
                                .setMaxCount(AppConstant.SELECT_IMAGE_COUNT)
                                //.setSelectedFiles(photoPaths)
//                                    .setActivityTheme(R.style.AppPrimaryTheme)
                                .enableVideoPicker(false)
                                .enableCameraSupport(true)
                                .showGifs(false)
                                .enableSelectAll(false)
                                .showFolderView(false)
                                .enableImagePicker(true)
                                .withOrientation(Orientation.UNSPECIFIED)
                                .pickPhoto(BaseActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        //Utils.showToast(context, context.getString(R.string.permission_denied));
                        Toasty.warning(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();

                    }
                }).check();

    }


    public void openCameraPicker(final MediaTypePicker listener) {

        TedPermission.with(this)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaPickerListener = listener;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        //Utils.showToast(context, context.getString(R.string.permission_denied));
                        Toasty.warning(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }).check();


    }

    public void openMediaPicker(final MediaTypePicker listener) {

        TedPermission.with(this)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaPickerListener = listener;
                        FilePickerBuilder.getInstance()
                                .setMaxCount(AppConstant.SELECT_IMAGE_COUNT)
                                //.setSelectedFiles(photoPaths)
                                .setActivityTheme(R.style.AppImagePickerTheme)
                                .enableVideoPicker(false)
                                .enableCameraSupport(true)
                                .showFolderView(false)
                                .showGifs(false)
                                .enableSelectAll(false)
                                .enableImagePicker(true)
                                .withOrientation(Orientation.UNSPECIFIED)
                                .pickPhoto(BaseActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        //Utils.showToast(context, context.getString(R.string.permission_denied));
                        Toasty.warning(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    public void openFilePicker(final MediaTypePicker listener) {


//        final String fileTypes[] = {".jpg", ".jpeg", ".mp4"};
        final String fileTypes[] = {".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx"};
        TedPermission.with(this)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaPickerListener = listener;
                        FilePickerBuilder.getInstance()
                                .setMaxCount(AppConstant.SELECT_MAX_FILE_COUNT)
                                .setSelectedFiles(photoPaths)
                                //.setActivityTheme(R.style.NormalTheme)
                                .enableVideoPicker(false)
                                .enableCameraSupport(true)
                                .showGifs(false)
                                .enableSelectAll(false)
                                .enableImagePicker(true)
                                .withOrientation(Orientation.UNSPECIFIED)
                                .addFileSupport("File", fileTypes)
                                .pickPhoto(BaseActivity.this);

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        //Utils.showToast(context, context.getString(R.string.permission_denied));
                        Toasty.warning(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    public void openDocPicker(final MediaTypePicker listener) {

        TedPermission.with(this)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        mediaPickerListener = listener;
                        FilePickerBuilder.getInstance()
                                .setMaxCount(AppConstant.SELECT_MAX_DOC_FILE_COUNT)
                                //.setSelectedFiles(filePaths)
                                //.setActivityTheme(R.style.NormalTheme)
                                .pickFile(BaseActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> arrayList) {
                        //Utils.showToast(context, context.getString(R.string.permission_denied));
                        Toasty.warning(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    new AsyncTaskRunner().execute(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    final Uri resultPhotoUri = UCrop.getOutput(data);
                    if (mediaPickerListener != null) {
                        ArrayList<File> files = new ArrayList<>();
                        files.add(new File(resultPhotoUri.getPath()));
                        mediaPickerListener.onPhotoClicked(files);
                    }
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mediaPickerListener.onDocClicked(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
            case CAMERA_PIC_REQUEST:
                if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                    ArrayList<String> cameraPic = new ArrayList<>();
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(this, (Bitmap) data.getExtras().get("data"));
                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    cameraPic.add(getRealPathFromURI(tempUri));

                    new AsyncTaskRunner().execute(cameraPic);
                }
                break;
            default:
                break;
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private class AsyncTaskRunner extends AsyncTask<ArrayList<String>, ArrayList<File>, ArrayList<File>> {

        ProgressDialog progressDialog;

        @Override
        protected ArrayList<File> doInBackground(ArrayList<String>... params) {

            ArrayList<File> compressedAndVideoImageFileList = new ArrayList<>();

            for (int index = 0; index < params[0].size(); index++) {

                File file = new File(params[0].get(index));

                if (file.toString().endsWith(".jpg") ||
                        file.toString().endsWith(".jpeg") ||
                        file.toString().endsWith(".png") ||
                        file.toString().endsWith(".gif")) {
                    try {
                        File compressedImageFile = new Compressor(BaseActivity.this).compressToFile(file, "compressed_" + file.getName());
                        compressedAndVideoImageFileList.add(compressedImageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {


                    if (!file.toString().endsWith(".3gp")) {
                        createCompressDir();
                        String compressVideoPath = Environment.getExternalStorageDirectory()
                                + File.separator
                                + APP_DIR
                                + COMPRESSED_VIDEOS_DIR
                                + "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4";

                        boolean isCompressSuccess = MediaController.getInstance().convertVideo(file.getAbsolutePath(), compressVideoPath);

                        if (isCompressSuccess) {
                            compressedAndVideoImageFileList.add(new File(compressVideoPath));
                        } else {
                            compressedAndVideoImageFileList.add(file);
                        }

                    } else {
                        compressedAndVideoImageFileList.add(file);
                    }
                }
            }
            return compressedAndVideoImageFileList;
        }


        @Override
        protected void onPostExecute(ArrayList<File> result) {
            // execution of result of Long time consuming operation

            if (progressDialog != null)
                progressDialog.dismiss();

            //mediaPickerListener.onPhotoClicked(result);

            if (result != null && result.size() > 0) {
                openImageEditor(result.get(0));
            }
        }


        @Override
        protected void onPreExecute() {
            try {
                progressDialog = ProgressDialog.show(BaseActivity.this,
                        context.getString(R.string.app_name),
                        context.getString(R.string.compressing_please_wait));
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private void openImageEditor(File file) {

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        @ColorInt int color;
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = typedValue.data;


        UCrop.Options options = new UCrop.Options();
        options.setCropFrameColor(color);
        options.setStatusBarColor(color);
        options.setToolbarColor(color);
        options.setActiveWidgetColor(color);

        UCrop.of(Uri.fromFile(file), Uri.fromFile(file))
                .withOptions(options)
                .withAspectRatio(16, 16)
                .start(BaseActivity.this);
    }

    private void createCompressDir() {
        File filedir = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        filedir = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        filedir = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }

    }


//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
////        isTransactionSafe = true;
////        /* Here after the activity is restored we check if there is any transaction pending from the last restoration */
////        if (isTransactionPending) {
////            if(baseFragment != null)
////                addSupportFragment(baseFragment, AppConstant.TRANSITION_TYPES.SLIDE);
////        }
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        isTransactionSafe = false;
//    }
}