package com.app.bizlinked.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by Ahsan Ali on 25/March/2020.
 */

public class DialogClass {


    public static Dialog createYesNoDialog(Activity activity,
                                           DialogInterface.OnClickListener dialogPositive, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageId)
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, dialogPositive)
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();


                            }
                        });
        return builder.create();
    }


    public static Dialog createMessageDialog(Activity activity,
                                             DialogInterface.OnClickListener dialogPositive,
                                             CharSequence message, CharSequence titleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(titleId)
                .setMessage(message).setCancelable(true)
                .setPositiveButton(android.R.string.ok, dialogPositive);
        return builder.create();

    }


    public static Dialog radioButtonListDialog(Activity context, String title, final String[] animals,
                                               DialogInterface.OnClickListener dialogPositive, DialogInterface.OnClickListener checkListener) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // add a radio button list
        //String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        int checkedItem = 1; // cow
        builder.setSingleChoiceItems(animals, checkedItem, checkListener);
        /*builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

            }
        });*/

        //this button will be implemented
        builder.setPositiveButton(android.R.string.yes, dialogPositive);
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }


    public static Dialog checkboxListDialog(Activity context, String title, CharSequence[] animals, boolean[] checkedItems,
                                            DialogInterface.OnClickListener dialogPositive, DialogInterface.OnMultiChoiceClickListener checkListener) {

   /* public static Dialog checkboxListDialog(Activity context, String title, CharSequence[] animals,
                                            DialogInterface.OnClickListener dialogPositive, DialogInterface.OnMultiChoiceClickListener checkListener) {*/
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // add a radio button list
        //String[] animals = {"horse", "cow", "camel", "sheep", "goat"};
        //int checkedItem = 1; // cow
        /*boolean[] checkedItems = new boolean[animals.length];
        checkedItems[0]= true;
        checkedItems[1]= false;
        checkedItems[2]= true;
        checkedItems[3]= true;*/

        builder.setMultiChoiceItems(animals, checkedItems, checkListener);
//        builder.setMultiChoiceItems(animals,null,checkListener);

        /*builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

            }
        });*/

        //this button will be implemented
        builder.setPositiveButton(android.R.string.yes, dialogPositive);
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }


    public static Dialog chooseListDialog(Activity context, String title, List<String> data, DialogInterface.OnClickListener dialogClickListener) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);


        CharSequence[] dataCharSequence = data.toArray(new CharSequence[data.size()]);

        builder.setItems(dataCharSequence, dialogClickListener);

        /*builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

            }
        });*/

        return builder.create();

    }








}
