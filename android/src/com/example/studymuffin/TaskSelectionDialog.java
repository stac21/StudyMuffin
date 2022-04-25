package com.example.studymuffin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

public class TaskSelectionDialog {
    private final AlertDialog alertDialog;
    private final RadioGroup group;

    public TaskSelectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View v = LayoutInflater.from(context).inflate(R.layout.task_selection_dialog, null);

        this.group = v.findViewById(R.id.task_selection_group);

        final RadioButton assignmentRB = v.findViewById(R.id.assignment_rb);

        builder.setTitle(R.string.task_selection_dialog_title);
        builder.setView(v);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        assignmentRB.setChecked(true);

        this.alertDialog = builder.create();
        this.alertDialog.setTitle(context.getString(R.string.task_selection_dialog_title));
    }

    public AlertDialog getAlertDialog() {
        return this.alertDialog;
    }

    public int getCheckedId() {
        return this.group.getCheckedRadioButtonId();
    }
}
