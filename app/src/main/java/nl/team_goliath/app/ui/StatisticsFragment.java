package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import nl.team_goliath.app.R;
import nl.team_goliath.app.databinding.StatisticsFragmentBinding;
import nl.team_goliath.app.viewmodel.BatteryViewModel;

/**
 * Main UI for the statistics screen.
 */
public class StatisticsFragment extends Fragment {

    private StatisticsFragmentBinding binding;

    static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.statistics_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final BatteryViewModel batteryViewModel = ViewModelProviders.of(this).get(BatteryViewModel.class);

        // Observe battery status
        batteryViewModel.getObservableBatteryRepo().observe(this, resource -> {
            binding.setBatteryRepo(resource == null ? null : resource.data);
            binding.setBatteryResource(resource);
            binding.executePendingBindings();
        });
    }
}