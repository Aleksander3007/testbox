package com.blackteam.testbox.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blackteam.testbox.R;

/**
 * Ползунок с редактируемым полем и заголовком.
 * По умолчанию диапазон значений равен 0..100.
 */
public class EditableSeekBar extends RelativeLayout
        implements SeekBar.OnSeekBarChangeListener, TextWatcher, View.OnFocusChangeListener, View.OnTouchListener {

    private static final int sSeekBarDefaultMin = 0;
    private static final int sSeekBarDefaultMax = 100;

    private TextView mTitleTextView;
    private AppCompatSeekBar mSeekBar;
    private EditText mEditText;

    private int mMinValue = sSeekBarDefaultMin;
    private int mMaxValue = sSeekBarDefaultMax;
    private int mValue = sSeekBarDefaultMin;

    public EditableSeekBar(Context context) {
        super(context);
    }

    public EditableSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(getContext(), R.layout.editable_seek_bar, this);

        setSaveEnabled(true);

        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mSeekBar = (AppCompatSeekBar) findViewById(R.id.sb_seek_bar);
        mEditText = (EditText) findViewById(R.id.et_edit_text);

        TypedArray attrsArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.EditableSeekBar, 0, 0);

        try {
            setTitle(attrsArray.getString(R.styleable.EditableSeekBar_esbTitle));
            setRange(attrsArray.getInteger(R.styleable.EditableSeekBar_esbMin, sSeekBarDefaultMin),
                    attrsArray.getInteger(R.styleable.EditableSeekBar_esbMax, sSeekBarDefaultMax));
            setValue(attrsArray.getInteger(R.styleable.EditableSeekBar_esbValue, sSeekBarDefaultMin));
        }
        finally {
            attrsArray.recycle();
        }

        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setOnTouchListener(this);
        mEditText.addTextChangedListener(this);
        mEditText.setOnFocusChangeListener(this);
    }

    /**
     * Установка заголовка. Заголовок будет спрятан если он равен null и пустой.
     * @param title текст заголовка.
     */
    public void setTitle(String title) {
        if (title != null && !title.equals("")) {
            mTitleTextView.setText(title);
            mTitleTextView.setVisibility(VISIBLE);
        }
    }

    /**
     * Получить текст заголовка.
     * @return текст заголовка.
     */
    public CharSequence getTitle() {
        return mTitleTextView.getText();
    }

    /**
     * Установка границ величины. Минимальная величина должна быть меньше максимальной.
     * @param min минимум.
     * @param max максимум.
     */
    public void setRange(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("Min value must be smaller than max value.");

        mMaxValue = max;
        mMinValue = min;

        mSeekBar.setMax(getRange());

        normalizationValue();
    }

    /**
     * Получить диапазон.
     * @return абсолютный диапазон.
     */
    public int getRange() {
        return Math.abs(mMaxValue - mMinValue);
    }

    /**
     * Установка текущий величины.
     */
    public void setValue(int value) {
        mValue = value;
        normalizationValue();
        mSeekBar.setProgress(convertToSeekBarValue(mValue));
        mEditText.setText(String.valueOf(mValue));
    }

    /**
     * Получение текущей величины.
     * @return текущая величина.
     */
    public int getValue() { return mValue; }

    private boolean isInRange(int value) {
        return (mValue >= mMinValue) && (mValue <= mMaxValue);
    }

    /**
     * Нормализация величины (чтобы находилась в заданном диапазоне).
     */
    private void normalizationValue() {
        if (!isInRange(mValue)) {
            if (mValue > mMaxValue) mValue = mMaxValue;
            if (mValue < mMinValue) mValue = mMinValue;
        }
    }

    /**
     * Перевод из величины SeekBar в реальную величину.
     * SeekBar всегда лежит в диапазоне 0..max.
     * Реальная величина в диапазоне minValue..maxValue.
     * @param seekBarValue величина SeekBar.
     * @return реальную величину.
     */
    private int convertToRealValue(int seekBarValue) {
        return mMinValue + seekBarValue;
    }

    /**
     * Перевод из реальной величины в величину SeekBar.
     * SeekBar всегда лежит в диапазоне 0..max.
     * Реальная величина в диапазоне minValue..maxValue.
     * @param realValue реальная величина.
     * @return величина SeekBar.
     */
    private int convertToSeekBarValue(int realValue) {
        return Math.abs(realValue - mMinValue);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mValue = convertToRealValue(progress);
            mEditText.setText(String.valueOf(mValue));
        }
    }

    /**
     * Является ли указанная строка integer величиной.
     * @param s строка.
     * @return true - если является.
     */
    private boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException nfex) {
            return false;
        }
    }

    /**
     * Скрыть клавиатуру для EditText.
     */
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            mValue = isNumber(s.toString()) ? Integer.parseInt(s.toString()) : mMaxValue;
        }
        // Если строка пустая, то упирается в минимум.
        else {
            mValue = mMinValue;
        }

        normalizationValue();
        mSeekBar.setProgress(convertToSeekBarValue(mValue));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            mEditText.setText(String.valueOf(mValue));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideKeyboard();
        return false;
    }
}
