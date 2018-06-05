package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import nl.team_goliath.app.R;
import nl.team_goliath.app.adapter.MessageAdapter;
import nl.team_goliath.app.databinding.StatisticsFragmentBinding;
import nl.team_goliath.app.formatter.BatteryRepositoryFormatter;
import nl.team_goliath.app.formatter.ConfigRepositoryFormatter;
import nl.team_goliath.app.formatter.LogRepositoryFormatter;
import nl.team_goliath.app.proto.BatteryRepositoryProto.BatteryRepository;
import nl.team_goliath.app.proto.LogRepositoryProto.LogRepository;
import nl.team_goliath.app.proto.ZmqConfigRepositoryProto.ConfigRepository;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;
import util.AutoClearedValue;

/**
 * Main UI for the statistics screen.
 */
public class StatisticsFragment extends Fragment {

    private RepositoryViewModel repositoryViewModel;

    private AutoClearedValue<StatisticsFragmentBinding> binding;
    private AutoClearedValue<MessageAdapter> adapter;

    static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StatisticsFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.statistics_fragment, container, false);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repositoryViewModel = ViewModelProviders.of(this).get(RepositoryViewModel.class);
        repositoryViewModel.watchRepo(BatteryRepository.class, new BatteryRepositoryFormatter());
        repositoryViewModel.watchRepo(ConfigRepository.class, new ConfigRepositoryFormatter());
        repositoryViewModel.watchRepo(LogRepository.class, new LogRepositoryFormatter(getContext()));

        MessageAdapter adapter = new MessageAdapter();
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().statisticsList.setAdapter(adapter);
        initList(repositoryViewModel);
    }

    private void initList(RepositoryViewModel viewModel) {
        viewModel.getMessages().observe(this, list -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (list != null && !list.isEmpty()) {
                adapter.get().replace(list);
            } else {
                //noinspection ConstantConditions
                adapter.get().replace(Collections.emptyList());
            }
        });
    }
}