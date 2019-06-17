package com.izzat.syamil.tradecrate.Message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.izzat.syamil.tradecrate.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Message_Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.message_fragment_layout, container, false);
    /*    FloatingActionButton newItemFAB = v.findViewById(R.id.new_inventory_fab);
        newItemFAB.setVisibility(View.INVISIBLE);*/

        //testcommitGit

        return v;
    }
}
