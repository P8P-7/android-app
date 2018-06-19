package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.model.Status;
import nl.team_goliath.app.proto.SystemStatusRepositoryProto.SystemStatusRepository;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;

public class StatisticsFragment extends Fragment {
    private List<Entry> temperatureData;
    LineChart temperatureChart;

    private final CommandSender callback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        temperatureData = new ArrayList<>();
        return inflater.inflate(R.layout.statistics_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        temperatureChart = getView().findViewById(R.id.temperatureChart);
        initList(ViewModelProviders.of(getActivity()).get(RepositoryViewModel.class));
    }

    private void initList(RepositoryViewModel viewModel) {
        viewModel.getMessages().observe(this, resource -> {
            if (resource.status == Status.SUCCESS && !resource.data.isEmpty()) {
                SystemStatusRepository systemStatusRepository = null;

                for (Message message : resource.data) {
                    if (message instanceof SystemStatusRepository) {
                        systemStatusRepository = (SystemStatusRepository) message;
                    }
                }

                if (systemStatusRepository != null) {
                    updateTemperatureGraph(systemStatusRepository.getTemperature());
                }
            }
        });
    }

    private void updateTemperatureGraph(double newData) {
        temperatureData.add(new Entry((float) temperatureData.size() + 1, (float) newData));

        LineDataSet lineDataSet = new LineDataSet(temperatureData, "Temperature");
        lineDataSet.setColor(R.color.fatalColor);

        temperatureChart.setData(new LineData(lineDataSet));
        temperatureChart.invalidate();
    }
}
