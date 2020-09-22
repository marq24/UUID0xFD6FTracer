package com.emacberry.uuid0xfd6fscan.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.emacberry.uuid0xfd6fscan.R;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mTOTTextView;
    private TextView mENFTextView;
    private TextView mSCFTextView;
    private String iTOTTextOnBind;
    private String iENFTextOnBind;
    private String iSCFTextOnBind;

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
        mTOTTextView = root.findViewById(R.id.tot_txt);
        if(iTOTTextOnBind != null){
            mTOTTextView.setText(iTOTTextOnBind);
        }else{
            mTOTTextView.setVisibility(View.GONE);
        }
        mENFTextView = root.findViewById(R.id.enf_txt);
        if(iENFTextOnBind != null){
            mENFTextView.setText(iENFTextOnBind);
        }else{
            mENFTextView.setVisibility(View.GONE);
        }
        mSCFTextView = root.findViewById(R.id.scf_txt);
        if(iSCFTextOnBind != null){
            mSCFTextView.setText(iSCFTextOnBind);
        }else{
            mSCFTextView.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mENFTextView = null;
        mSCFTextView = null;
    }

    public void setText(String txtTOT, String txtENF, String txtSCF) {
        iTOTTextOnBind = handleTxt(txtTOT, mTOTTextView);
        iENFTextOnBind = handleTxt(txtENF, mENFTextView);
        iSCFTextOnBind = handleTxt(txtSCF, mSCFTextView);
    }

    public void setInfoText(String info) {
        handleTxt(info, mTOTTextView);
        handleTxt(null, mENFTextView);
        handleTxt(null, mSCFTextView);
    }

    private String handleTxt(String txt, TextView view) {
        if(txt != null) {
            if (view != null) {
                if(view.getVisibility() != View.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }
                view.setText(txt);
            }
            return txt;
        }else{
            if (view != null && view.getVisibility() != View.GONE) {
                view.setVisibility(View.GONE);
            }
            return null;
        }
    }
}