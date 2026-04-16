package com.example.spacecolony.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.spacecolony.R;
import com.example.spacecolony.ui.adapters.CrewAdapter;
import com.example.spacecolony.data.Storage;
import com.example.spacecolony.model.CrewMember;
import java.util.List;

/** Shows crew members in Medbay (defeated but not dead - No Death bonus). */
public class MedbayFragment extends Fragment {
    private RecyclerView recyclerView;
    private CrewAdapter adapter;
    private Button btnDischarge;
    private TextView tvEmpty;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medbay, container, false);
        recyclerView = view.findViewById(R.id.rv_medbay_crew);
        btnDischarge = view.findViewById(R.id.btn_discharge);
        tvEmpty      = view.findViewById(R.id.tv_empty_medbay);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadData();
        btnDischarge.setOnClickListener(v -> discharge());
        return view;
    }

    private void loadData() {
        List<CrewMember> medbayCrew = Storage.getInstance().getCrewByLocation(CrewMember.Location.MEDBAY);
        tvEmpty.setVisibility(medbayCrew.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(medbayCrew.isEmpty() ? View.GONE : View.VISIBLE);
        adapter = new CrewAdapter(getContext(), medbayCrew, true, 0, null);
        recyclerView.setAdapter(adapter);
    }

    private void discharge() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) { Toast.makeText(getContext(), "Select crew to discharge", Toast.LENGTH_SHORT).show(); return; }
        for (int id : ids) {
            CrewMember cm = Storage.getInstance().getCrewMember(id);
            if (cm != null) { cm.setLocation(CrewMember.Location.QUARTERS); cm.restoreEnergy(); }
        }
        Toast.makeText(getContext(), ids.size() + " crew discharged to Quarters!", Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override public void onResume() { super.onResume(); loadData(); }
}
