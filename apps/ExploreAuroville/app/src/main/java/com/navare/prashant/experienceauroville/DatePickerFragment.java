package com.navare.prashant.experienceauroville;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by prashant on 23-Apr-17.
 */

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        if (ApplicationStore.activeDatePicker == R.id.event_date_from) {
            CurrentEventsActivity.mFromCalendar.clear();
            CurrentEventsActivity.mFromCalendar.set(Calendar.YEAR, year);
            CurrentEventsActivity.mFromCalendar.set(Calendar.MONTH, month);
            CurrentEventsActivity.mFromCalendar.set(Calendar.DAY_OF_MONTH, day);
            CurrentEventsActivity.setDateFieldText();
            ApplicationStore.activeDatePicker = 0;
        }
        else if (ApplicationStore.activeDatePicker == R.id.event_date_to) {
            CurrentEventsActivity.mToCalendar.clear();
            CurrentEventsActivity.mToCalendar.set(Calendar.YEAR, year);
            CurrentEventsActivity.mToCalendar.set(Calendar.MONTH, month);
            CurrentEventsActivity.mToCalendar.set(Calendar.DAY_OF_MONTH, day);
            CurrentEventsActivity.setDateFieldText();
            ApplicationStore.activeDatePicker = 0;
        }
    }
}
