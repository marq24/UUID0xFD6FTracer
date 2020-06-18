package com.emacberry.uuid0xfd6ftracer.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.emacberry.uuid0xfd6ftracer.R;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mTextView;
    private String iTextOnBind;

    private static HashMap<Integer, PlaceholderFragment> map = new HashMap<>();
    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = map.get(index);
        if(fragment == null) {
            fragment = new PlaceholderFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_SECTION_NUMBER, index);
            fragment.setArguments(bundle);
            map.put(index, fragment);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mTextView = root.findViewById(R.id.section_label);
        if(iTextOnBind != null){
            mTextView.setText(iTextOnBind);
        }
        return root;
    }

    public void setText(String txt) {
        if(mTextView != null) {
            mTextView.setText(txt);
        }else{
            iTextOnBind = txt;
        }
    }
}