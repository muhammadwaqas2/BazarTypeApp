package com.app.bizlinked.helpers.validation;

//import es.dmoral.toasty.Toasty;

public class CustomValidation {

//    public static boolean validateEmail(EditText editText, CustomTextInputLayout textInputLayout, String error) {
//        String email = editText.getText().toString().trim();
//        textInputLayout.errorEnable(false);
//        if (email.isEmpty() || !isValidEmail(email)) {
//            textInputLayout.setError(error);
//            CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//            textInputLayout.requestFocus();
////            btnLogin.setAlpha(0.5f);
//            editText.requestFocus();
//            return false;
//        } else {
//
//            textInputLayout.setError("");
//
//        }
//
//        return true;
//    }

//    public static boolean validatePassword(EditText txtPassword, final CustomTextInputLayout textInputLayout, String error) {
//        if (txtPassword.getText().toString().trim().isEmpty() && txtPassword.getText().toString().trim().length() < 8) {
//            textInputLayout.setError(error);
//            CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//            textInputLayout.requestFocus();
////            btnLogin.setAlpha(0.5f);
//            txtPassword.requestFocus();
//            return false;
//        } else {
//            textInputLayout.setError("");
//        }
//
//        txtPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                textInputLayout.setError("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        return true;
//    }
//
//    public static boolean isValidEditText(EditText text, final CustomTextInputLayout textInputLayout, String error) {
//        String emailPattern = "^(?=\\s*\\S).*$";
//        text.requestFocus();
//        CharSequence inputStr = text.getText().toString();
//        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(inputStr.toString().trim());
//        if (matcher.matches()) {
//            textInputLayout.setError("");
//            return true;
//        }
//        textInputLayout.requestFocus();
//        textInputLayout.setError(error);
//        CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//        text.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                textInputLayout.setError("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        return false;
//    }
//
//    public static boolean isValidNumericField(EditText text, CustomTextInputLayout textInputLayout, String error) {
//        String emailPattern = "[0-9]{0,100}$";
//        CharSequence inputStr = text.getText().toString();
//        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            textInputLayout.errorEnable(false);
//            return true;
//        }
//        textInputLayout.requestFocus();
//        textInputLayout.setErrorEnabled();
//        textInputLayout.setError(error);
//        CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//        text.requestFocus();
//        return false;
//    }
//
//    public static boolean isValidPassword(String text, String conformpass, CustomTextInputLayout textInputLayout, String error) {
//        if (text.equals(conformpass)) {
//            textInputLayout.setError("");
//            return true;
//        }
//        textInputLayout.setError(error);
//        CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//        return false;
//    }
//
//    public static boolean validateLength(EditText editText, final CustomTextInputLayout textInputLayout, String error, Integer min, Integer max) {
//        String emailPattern = "^.{" + min + "," + max + "}$";
//        CharSequence inputStr = editText.getText().toString().trim();
//        Pattern pattern = Pattern.compile(emailPattern,  Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            textInputLayout.setError("");
//            return true;
//        }
//        textInputLayout.requestFocus();
//        textInputLayout.setError(error);
//        CustomAnimationHelpers.animate(Techniques.RubberBand, 300, textInputLayout);
//        editText.requestFocus();
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                textInputLayout.setError("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//        return false;
//    }
//
//
//    public static boolean validateLength(Context ctx, TextView textview, String error, Integer min, Integer max) {
//        String emailPattern = "^.{" + min + "," + max + "}(\\s|$)?";
//        CharSequence inputStr = textview.getText().toString().trim();
//        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
////            Utils.showToast(ctx, error);
//            Toasty.error(ctx, error, Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return false;
//    }
//
//
//    public static boolean validateEmptyOrNull(Context ctx, TextView textview, String error) {
//        if (!Utils.isEmptyOrNull(textview.getText().toString().trim())) {
//            return true;
//        }
////        Utils.showToast(ctx, error);
//        Toasty.error(ctx, error, Toast.LENGTH_SHORT).show();
//        return false;
//    }

}
