package com.example.lab4;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private final static String FILENAME = "sample.txt"; // имя файла
    private EditText mEditText;

    Toolbar myToolbar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mEditText = findViewById(R.id.editText);
        String mystring = "text";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if(mEditText.getText().toString().isEmpty())
                fos.write(mystring.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_open) {
                openFile(FILENAME);
                return true;
            }
            if (item.getItemId() == R.id.action_save) {
                saveFile(FILENAME);
                return true;
            }
           if (item.getItemId() == R.id.action_settings) {
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
           if(item.getItemId() == R.id.action_clear){
               FragmentManager manager = getSupportFragmentManager();
               MyDialogFragment myDialogFragment = new MyDialogFragment();
               myDialogFragment.show(manager, "myDialog");
               return true;
           }
           if(item.getItemId() == R.id.action_exit){
               this.finish();
           }
           return true;
    }

    public static class MyDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Вы действительно хотите очистить?")
                    .setTitle("Внимание!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((MainActivity) getActivity()).okClicked();
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((MainActivity) getActivity()).cancelClicked();
                        }
                    });

            return builder.create();
        }
    }
    public void okClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку OK!",
                Toast.LENGTH_SHORT).show();
        mEditText.setText("");
    }

    public void cancelClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку отмены!",
                Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onResume() {
        try {
            super.onResume();

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            // читаем установленное значение из CheckBoxPreference
            if (prefs.getBoolean(getString(R.string.pref_openmode), false)) {
                openFile(FILENAME);
            }
            if (prefs.getBoolean(getString(R.string.pref_background), false)) {
                mEditText.setBackground(getDrawable(R.drawable.backgr));
            }
            else {
                mEditText.setBackground(getDrawable(R.drawable.deff));
            }
            // читаем размер шрифта из EditTextPreference
            float fSize = Float.parseFloat(prefs.getString(
                    getString(R.string.pref_size), "20"));
// применяем настройки в текстовом поле
            mEditText.setTextSize(fSize);
            // читаем стили текста из ListPreference
            String regular = prefs.getString(getString(R.string.pref_style), "");
            int typeface = Typeface.NORMAL;

            if (regular.contains("Полужирный"))
                typeface += Typeface.BOLD;

            if (regular.contains("Курсив"))
                typeface += Typeface.ITALIC;
            mEditText.setTypeface(null, typeface);
            if(regular.contains("Пиксель")) {
                String s = prefs.getString(getString(R.string.pref_style_pixel), "Como");
                //Из папки font
                Typeface font1 = ResourcesCompat.getFont(this, R.font.como);
                mEditText.setTypeface(font1);
            }

            String regcolor = prefs.getString(getString(R.string.pref_color), "");
            int color = Color.BLACK;

            if(regcolor.contains("Красный цвет"))
                color += Color.RED;
            if(regcolor.contains("Зеленый цвет"))
                color += Color.GREEN;
            if(regcolor.contains("Синий цвет"))
                color +=Color.BLUE;

// меняем настройки в EditText
            mEditText.setTextColor(color);
        }
        catch (Exception ex){
            Toast toast = Toast.makeText(this, "Введена ошибка", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    // Метод для открытия файла
    private void openFile(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                inputStream.close();
                mEditText.setText(builder.toString());
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // Метод для сохранения файла
    private void saveFile(String fileName) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(mEditText.getText().toString());
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}