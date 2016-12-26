package com.blackteam.testbox.ui;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;

/**
 * Базовая активити, содержит общие для всех активити элементы, действия и т.д.
 */
public class BaseActivity extends AppCompatActivity {

    private MenuItem mUserTypeMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mUserTypeMenuItem = menu.findItem(R.id.mi_userType);

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
                mUserTypeMenuItem.setTitle(R.string.user_type_user);
                break;
            case EDITOR:
                mUserTypeMenuItem.setTitle(R.string.user_type_editor);
                break;
        }
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
