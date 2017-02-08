package com.blackteam.testbox.ui;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;

/**
 * Базовая активити, содержит общие для всех активити элементы, действия и т.д.
 */
public class BaseActivity extends AppCompatActivity {

    protected MenuItem mUserTypeMenuItem;
    protected MenuItem mTimerMenuItem;

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
            Log.i("BaseActivity", ex.getMessage());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Переключаем между режимами пользователь/редактор.
        if (id == R.id.mi_userType) {
            switch (((TestBoxApp)getApplicationContext()).getUserType()) {
                case USER:
                    ((TestBoxApp)getApplicationContext()).setsUserType(TestBoxApp.UserType.EDITOR);
                    break;
                case EDITOR:
                    ((TestBoxApp)getApplicationContext()).setsUserType(TestBoxApp.UserType.USER);
                    break;
            }
            setViewByUserType();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Устанавливаем правильное отображение состояния пользователя (пользователь/редактор).
     */
    private void setViewByUserType() {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                setModeUser();
                break;
            case EDITOR:
                setModeEditor();
                break;
        }
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
}
