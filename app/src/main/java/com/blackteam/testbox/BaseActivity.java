package com.blackteam.testbox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Базовая активити, содержит общие для всех активити элементы, действия и т.д.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        try {
            MenuItem userTypeMenuItem = (MenuItem) menu.findItem(R.id.mi_userType);
            switch (AppState.sUserType) {
                case USER:
                    userTypeMenuItem.setTitle(R.string.user_type_user);
                    break;
                case EDITOR:
                    userTypeMenuItem.setTitle(R.string.user_type_editor);
                    break;
            }
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
            if (item.getTitle() == getResources().getString(R.string.user_type_user)) {
                item.setTitle(R.string.user_type_editor);
                AppState.sUserType = AppState.UserType.EDITOR;
            }
            else {
                item.setTitle(R.string.user_type_user);
                AppState.sUserType = AppState.UserType.USER;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
