package com.example.spacecolony.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.ui.adapters.CrewAdapter;
import com.example.spacecolony.model.CrewMember;

import java.util.List;

/**
 * Fragment showing crew members in the Simulator.
 * Allows training (XP gain) and moving back to Quarters.
 */
public class SimulatorFragment extends Fragment {

    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Button btnTrain, btnToQuarters;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulator, container, false);

        recyclerView  = view.findViewById(R.id.rv_simulator_crew);
        btnTrain      = view.findViewById(R.id.btn_train);
        btnToQuarters = view.findViewById(R.id.btn_simulator_to_quarters);
        tvEmpty       = view.findViewById(R.id.tv_empty_simulator);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadData();

        btnTrain.setOnClickListener(v -> trainSelected());
        btnToQuarters.setOnClickListener(v -> returnToQuarters());

        return view;
    }

    private void loadData() {
        List<CrewMember> simulatorCrew = Storage.getInstance()
                .getCrewByLocation(CrewMember.Location.SIMULATOR);

        tvEmpty.setVisibility(simulatorCrew.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(simulatorCrew.isEmpty() ? View.GONE : View.VISIBLE);

        adapter = new CrewAdapter(getContext(), simulatorCrew, true, 0, null);
        recyclerView.setAdapter(adapter);
    }

    private void trainSelected() {
        List<Integer> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members to train", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int id : selectedIds) {
            CrewMember cm = Storage.getInstance().getCrewMember(id);
            if (cm != null) cm.train();
        }
        Toast.makeText(getContext(), selectedIds.size() + " crew trained! +1 XP each", Toast.LENGTH_SHORT).show();
        loadData();
    }

    private void returnToQuarters() {
        List<Integer> selectedIds = adapter.getSelectedIds();
        if (selectedIds.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members to send home", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int id : selectedIds) {
            CrewMember cm = Storage.getInstance().getCrewMember(id);
            if (cm != null) {
                cm.setLocation(CrewMember.Location.QUARTERS);
                cm.restoreEnergy(); // Full energy restore on return
            }
        }
        Toast.makeText(getContext(), selectedIds.size() + " crew returned to Quarters (energy restored)", Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}