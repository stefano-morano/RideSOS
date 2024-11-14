package com.example.crashsimulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;


public class AppHelper {
    static boolean HasText(EditText text) {
        return hasTextEditable(text.getText());
    }

    static boolean HasTextHint(EditText text) {
        return hasTextEditable((Editable) text.getHint());
    }

    static private boolean hasTextEditable(Editable s) {
        return s != null && s.length() != 0;
    }


    static void PutString(SharedPreferences.Editor editor, String entry_key, EditText text) {
        if (HasText(text)) {
            editor.putString(entry_key, String.valueOf(text.getText()));
        }
    }

    static void PutStringHint(SharedPreferences.Editor editor, String entry_key, EditText text) {
        if (HasTextHint(text)) {
            editor.putString(entry_key, String.valueOf(text.getHint()));
        }
    }

    static void CreateDropdown(Context cxt, MaterialAutoCompleteTextView autocompleteTextView, int valuesId) {
        // Create an ArrayAdapter that will contain all list items
        String[] myValues = cxt.getResources().getStringArray(valuesId);

        /* Assign the name array to that adapter and
        also choose a simple layout for the list items */
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(cxt, R.layout.item_dropdown, myValues);

        // Assign the adapter to the tv
        autocompleteTextView.setAdapter(arrayAdapter);
    }

    static boolean CheckText(EditText value, TextInputLayout layout) {
        // TODO: We need also to sanify input
        String errorText = "Mandatory";
        boolean saneInput = true;

        if (!HasText(value)) {
            layout.setError(errorText);
            saneInput = false;
        } else {
            layout.setError(null);
            layout.setErrorEnabled(false);
        }

        return saneInput;
    }

    static void SetTextChangedListener(EditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("HELPER", "aftertextchanged: " + s);
                CheckText(editText, layout);
            }
        });
    }
}
