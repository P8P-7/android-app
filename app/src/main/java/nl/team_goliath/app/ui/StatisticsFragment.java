package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
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
import nl.team_goliath.app.proto.BatteryRepositoryProto.BatteryRepository;
import nl.team_goliath.app.proto.SystemStatusRepositoryProto.SystemStatusRepository;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;

public class StatisticsFragment extends Fragment {
    private List<Entry> temperatureData;
    private LineChart temperatureChart;
    private int temperatureX = 0;

    private List<Entry> batteryData;
    private LineChart batteryChart;
    private int batteryX = 0;

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
        batteryData = new ArrayList<>();
        return inflater.inflate(R.layout.statistics_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Description temperatureDescription = new Description();
        temperatureDescription.setText("Temperature");

        temperatureChart = getView().findViewById(R.id.temperatureChart);
        temperatureChart.setDescription(temperatureDescription);

        Description batteryDescription = new Description();
        batteryDescription.setText("Battery");

        batteryChart = getView().findViewById(R.id.batteryChart);
        batteryChart.setDescription(batteryDescription);

        initList(ViewModelProviders.of(getActivity()).get(RepositoryViewModel.class));
    }

    private void initList(RepositoryViewModel viewModel) {
        viewModel.getMessages().observe(this, resource -> {
            if (resource.status == Status.SUCCESS && !resource.data.isEmpty()) {
                for (Message message : resource.data) {
                    if (message instanceof SystemStatusRepository) {
                        updateTemperatureGraph(((SystemStatusRepository) message).getTemperature());
                    } else if (message instanceof BatteryRepository) {
                        updateBatteryGraph(((BatteryRepository) message).getLevel());
                    }
                }
            }
        });
    }

    private void updateTemperatureGraph(double newData) {
        if (temperatureData.size() == 20) {
            temperatureData.remove(0);
        }

        temperatureData.add(new Entry((float) temperatureX++, (float) newData));

        LineDataSet lineDataSet = new LineDataSet(temperatureData, "°C");
        lineDataSet.setCircleColor(getResources().getColor(R.color.fatalColor));
        lineDataSet.setColor(getResources().getColor(R.color.fatalColor));

        temperatureChart.setData(new LineData(lineDataSet));
        temperatureChart.invalidate();
    }

    private void updateBatteryGraph(double newData) {
        if (batteryData.size() == 20) {
            batteryData.remove(0);
        }

        batteryData.add(new Entry((float) batteryX++, (float) newData));

        LineDataSet lineDataSet = new LineDataSet(batteryData, "%");
        lineDataSet.setCircleColor(getResources().getColor(R.color.debugColor));
        lineDataSet.setColor(getResources().getColor(R.color.debugColor));

        batteryChart.setData(new LineData(lineDataSet));
        batteryChart.invalidate();
    }
}
