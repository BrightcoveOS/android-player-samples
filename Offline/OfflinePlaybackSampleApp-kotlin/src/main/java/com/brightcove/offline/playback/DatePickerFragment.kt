package com.brightcove.offline.playback

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import java.util.Calendar
import java.util.Date

/**
 * Date picker fragment can be used to show a date picker dialog to the user.
 */
class DatePickerFragment : DialogFragment() {
    /**
     * Constructor for a listener that can handle the selected date.
     */
    interface Listener {
        /**
         * This method will be called back when a valid date is selected.
         *
         * @param date the selected date.
         */
        fun onDateSelected(date: Date)
    }

    /**
     * The title of the date picker dialog
     */
    private var title: String? = null

    /**
     * The listener that will be called back when a valid date is selected.
     */
    private var listener: Listener? = null

    /**
     * Sets the dialog title.
     *
     * @param title the dialog title.
     * @return reference to this date picker fragment
     */
    fun setTitle(title: String?): DatePickerFragment {
        this.title = title
        return this
    }

    /**
     * Sets the listener that must be called back when a valid date is selected.
     *
     * @param listener the listener.
     * @return reference to this date picker fragment
     */
    fun setListener(listener: Listener?): DatePickerFragment {
        this.listener = listener
        return this
    }

    /**
     * Provides an onDateSetListener which passes the selected date to the [Listener].
     */
    private val onDateSetListener = OnDateSetListener { _, year, month, day ->
        listener?.let {
            val calendar = Calendar.getInstance()
            calendar[year, month, day, 0, 0] = 0
            it.onDateSelected(calendar.time)
        }
    }

    /**
     * Constructs a new date picker.
     */
    init {
        isCancelable = true
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val activity = activity
        val dialog = DatePickerDialog(
            activity,
            R.style.DialogWithTitle, onDateSetListener, year, month, day
        )
        title?.let { dialog.setTitle(it) }

        return dialog
    }
}