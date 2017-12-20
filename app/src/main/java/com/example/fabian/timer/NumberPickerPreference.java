package com.example.fabian.timer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {

    // allowed range
    public static final int MAX_VALUE = 20;
    public static final int MIN_VALUE = 1;
    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = false;

    private NumberPicker pickerTens;
    private NumberPicker pickerOnes;
    private int value;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER;
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;

        pickerTens = new NumberPicker(getContext());
        pickerOnes = new NumberPicker(getContext());
        pickerTens.setLayoutParams(layoutParams1);
        pickerOnes.setLayoutParams(layoutParams2);

        LinearLayout dialogView = new LinearLayout(getContext());
        dialogView.addView(pickerTens);
        dialogView.addView(pickerOnes);
        dialogView.setLayoutDirection(LinearLayout.HORIZONTAL);
        dialogView.setGravity(Gravity.CENTER);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        pickerTens.setMinValue(0);
        pickerTens.setMaxValue(2);
        pickerTens.setWrapSelectorWheel(false);
        pickerTens.setValue(getValue()/10);

        pickerOnes.setMinValue(0);
        pickerOnes.setMaxValue(9);
        pickerOnes.setWrapSelectorWheel(false);
        pickerOnes.setValue(getValue()%10);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            pickerTens.clearFocus();
            pickerOnes.clearFocus();
            int newValue = pickerTens.getValue() * 10 + pickerOnes.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
        setSummary(""+this.value);
    }

    public int getValue() {
        return this.value;
    }


}