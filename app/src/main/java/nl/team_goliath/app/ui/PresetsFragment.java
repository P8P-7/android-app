package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import goliath.proto.commands.Command;
import nl.team_goliath.app.R;
import nl.team_goliath.app.adapter.PresetAdapter;
import nl.team_goliath.app.databinding.PresetFragmentBinding;
import nl.team_goliath.app.model.Preset;
import nl.team_goliath.app.model.Status;
import nl.team_goliath.app.proto.CommandMessageProto;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusItem;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusItem.CommandStatus;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusRepository;
import nl.team_goliath.app.proto.DanceCommandProto.DanceCommand;
import nl.team_goliath.app.proto.LineDanceCommandProto.LineDanceCommand;
import nl.team_goliath.app.proto.ObstacleCourseCommandProto.ObstacleCourseCommand;
import nl.team_goliath.app.proto.TransportRebuildCommandProto.TransportRebuildCommand;
import nl.team_goliath.app.proto.WunderhornCommandProto.WunderhornCommand;
import nl.team_goliath.app.util.AutoClearedValue;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;

public class PresetsFragment extends Fragment {
    private RepositoryViewModel repositoryViewModel;

    private AutoClearedValue<PresetFragmentBinding> binding;
    private AutoClearedValue<PresetAdapter> adapter;

    public static PresetsFragment newInstance() {
        return new PresetsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PresetFragmentBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.preset_fragment, container, false);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repositoryViewModel = ViewModelProviders.of(this).get(RepositoryViewModel.class);
        repositoryViewModel.watchRepo(CommandStatusRepository.class);

        PresetAdapter adapter = new PresetAdapter();
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().presetList.setAdapter(adapter);
        initList(repositoryViewModel);
    }

    private void initList(RepositoryViewModel viewModel) {
        viewModel.getMessages().observe(this, resource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (resource.status == Status.SUCCESS && !resource.data.isEmpty()) {
                ArrayList<Preset> presetList = new ArrayList<>();

                for (CommandStatusItem item : ((CommandStatusRepository) resource.data.get(0)).getStatusList()) {
                    boolean status = item.getCommandStatus() != CommandStatus.STALE;
                    String name = "";

                    switch (item.getId()) {
                        // TODO: Add entering the Arena
                        case CommandMessageProto.CommandMessage.DANCECOMMAND_FIELD_NUMBER:
                            name = DanceCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                            break;
                        case CommandMessageProto.CommandMessage.LINEDANCECOMMAND_FIELD_NUMBER:
                            name = LineDanceCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                            break;
                        case CommandMessageProto.CommandMessage.OBSTACLECOURSECOMMAND_FIELD_NUMBER:
                            name = ObstacleCourseCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                            break;
                        case CommandMessageProto.CommandMessage.WUNDERHORNCOMMAND_FIELD_NUMBER:
                            name = WunderhornCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                            break;
                        case CommandMessageProto.CommandMessage.TRANSPORTREBUILDCOMMAND_FIELD_NUMBER:
                            name = TransportRebuildCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                            break;
                    }

                    if (!name.isEmpty()) {
                        presetList.add(new Preset(item.getId(), name, status));
                    }
                }

                adapter.get().replace(presetList);
            } else {
                //noinspection ConstantConditions
                adapter.get().replace(Collections.emptyList());
            }
        });
    }
}
