package com.brightcove.player.samples.offlineplayback;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Date picker fragment can be used to show a date picker dialog to the user.
 */
public class DatePickerFragment extends DialogFragment {

    /**
     * Constructor for a listener that can handle the selected date.
     */
    interface Listener {
        /**
         * This method will be called back when a valid date is selected.
         *
         * @param date the selected date.
         */
        void onDateSelected(@NonNull Date date);
    }

    /**
     * The title of the date picker dialog
     */
    private String title;

    /**
     * The listener that will be called back when a valid date is selected.
     */
    private Listener listener;

    /**
     * Sets the dialog title.
     *
     * @param title the dialog title.
     * @return reference to this date picker fragment
     */
    public DatePickerFragment setTitle(@Nullable String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the listener that must be called back when a valid date is selected.
     *
     * @param listener the listener.
     * @return reference to this date picker fragment
     */
    public DatePickerFragment setListener(@Nullable Listener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Provides an onDateSetListener which passes the selected date to the {@link Listener}.
     */
    private final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (listener != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, 0, 0, 0);
                listener.onDateSelected(calendar.getTime());
            }
        }
    };

    /**
     * Constructs a new date picker.
     */
    public DatePickerFragment() {
        setCancelable(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Activity activity = getActivity();
        DatePickerDialog dialog = new DatePickerDialog(activity,
                R.style.DialogWithTitle, onDateSetListener, year, month, day);
        if (title != null) {
            dialog.setTitle(title);
        }

        return dialog;
    }
}
