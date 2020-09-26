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

    private TextView mNearView;
    private TextView mMedView;
    private TextView mFarView;
    private TextView mBadView;

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

        mTOTTextView = generateTextViewAndSetInitalValueIfPresent(root, R.id.tot_txt, iTOTTextOnBind);
        mENFTextView = generateTextViewAndSetInitalValueIfPresent(root, R.id.enf_txt, iENFTextOnBind);
        mSCFTextView = generateTextViewAndSetInitalValueIfPresent(root, R.id.scf_txt, iSCFTextOnBind);

        mNearView = generateTextViewAndSetInitalValueIfPresent(root, R.id.near_txt, null);
        mMedView = generateTextViewAndSetInitalValueIfPresent(root, R.id.medium_txt, null);
        mFarView = generateTextViewAndSetInitalValueIfPresent(root, R.id.far_txt, null);
        mBadView = generateTextViewAndSetInitalValueIfPresent(root, R.id.bad_txt, null);

        return root;
    }

    private TextView generateTextViewAndSetInitalValueIfPresent(View root, int id, String initValue) {
        TextView aView = root.findViewById(id);
        if(initValue != null){
            aView.setText(initValue);
        }else{
            aView.setVisibility(View.GONE);
        }
        return aView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTOTTextView = null;
        mENFTextView = null;
        mSCFTextView = null;
        iTOTTextOnBind = null;
        iENFTextOnBind = null;
        iSCFTextOnBind = null;

        mNearView = null;
        mMedView = null;
        mFarView = null;
        mBadView = null;
    }

    public void setText(String txtTOT, String txtENF, String txtSCF) {
        iTOTTextOnBind = updateTextView(txtTOT, mTOTTextView);
        iENFTextOnBind = updateTextView(txtENF, mENFTextView);
        iSCFTextOnBind = updateTextView(txtSCF, mSCFTextView);
    }

    public void setNoBluetoothInfoText(String info) {
        updateTextView(info, mTOTTextView);
        updateTextView(null, mENFTextView);
        updateTextView(null, mSCFTextView);
    }

    public void setRangeInfo(String txtNEAR, String txtMEDIUM, String txtFAR, String txtBAD) {
        updateTextView(txtNEAR, mNearView);
        updateTextView(txtMEDIUM, mMedView);
        updateTextView(txtFAR, mFarView);
        updateTextView(txtBAD, mBadView);
    }

    private String updateTextView(String txt, TextView view) {
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