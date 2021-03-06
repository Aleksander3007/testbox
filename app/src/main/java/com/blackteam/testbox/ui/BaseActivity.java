package com.blackteam.testbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;

import icepick.Icepick;

/**
 * Базовая активити, содержит общие для всех активити элементы, действия и т.д.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected MenuItem mUserTypeMenuItem;
    protected MenuItem mTimerMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mUserTypeMenuItem = menu.findItem(R.id.mi_userType);
        mTimerMenuItem = menu.findItem(R.id.mi_timer);

        // По умолчанию таймер не отображается,
        // т.к. для большинства Activity он не нужен.
        mTimerMenuItem.setVisible(false);

        try {
            setViewByUserType();
        }
        catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Если приложение было уже запущено (и не было разрушено до тек. момента),
        // то устанавливаем вид относительно типа пользователя,
        // в противном случаи Меню еще не создано (null), поэтому всё переносится в
        // onCreateOptionsMenu().
        if (mUserTypeMenuItem != null)
            setViewByUserType();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // элеменент пользователь/редактор.
        if (id == R.id.mi_userType) {
            switch (((TestBoxApp)getApplicationContext()).getUserType()) {
                // Если был пользователь, то переключаемся на редактора.
                case USER:
                    onModeUserClick();
                    break;
                case EDITOR:
                    onModeEditorClick();
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Нажатие на элемент "Пользователь".
     */
    protected void onModeUserClick() {
        // переключаемся на режим "Редактор".
        setModeEditor();
    }

    /**
     * Нажатие на элемент "Редактор".
     */
    protected void onModeEditorClick() {
        // переключаемся на режим "Пользователь".
        setModeUser();
    }

    /**
     * Устанавливаем режим отображения для пользователя.
     */
    protected void setModeUser() {
        ((TestBoxApp)getApplicationContext()).setsUserType(TestBoxApp.UserType.USER);
        mUserTypeMenuItem.setTitle(R.string.user_type_user);
    }

    /**
     * Устанавливаем режим отображения для редактора.
     */
    protected void setModeEditor() {
        ((TestBoxApp)getApplicationContext()).setsUserType(TestBoxApp.UserType.EDITOR);
        mUserTypeMenuItem.setTitle(R.string.user_type_editor);
    }

    /**
     * Установка отображения в зависимости от типа пользователя (пользователь/редактор).
     */
    private void setViewByUserType() {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            // Если был пользователь, то переключаемся на редактора.
            case USER:
                setModeUser();
                break;
            case EDITOR:
                setModeEditor();
                break;
        }
    }
}
