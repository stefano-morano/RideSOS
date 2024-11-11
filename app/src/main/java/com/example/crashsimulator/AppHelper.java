package com.example.crashsimulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class AppHelper {
    static boolean HasText(EditText text) {
        return text.getText() != null && text.getText().length() != 0;
    }

    static void PutString(SharedPreferences.Editor editor, String entry_key, EditText text) {
        if (HasText(text)) {
            editor.putString(entry_key, String.valueOf(text.getText()));
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

}
