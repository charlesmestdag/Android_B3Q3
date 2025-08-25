package com.mestdag.gestionpointsetudiants.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mestdag.gestionpointsetudiants.R;
import com.mestdag.gestionpointsetudiants.viewmodel.ForcedGradeViewModel;

public class ForcedGradeFragment extends Fragment {
    private ForcedGradeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forced_grade, container, false);

        long evaluationId = -1;
        if (getArguments() != null) {
            evaluationId = getArguments().getLong("evaluationId", -1);
        }

        viewModel = new ViewModelProvider(this).get(ForcedGradeViewModel.class);
        if (evaluationId != -1) {
            viewModel.init(evaluationId);
        }

        return view;
    }
}