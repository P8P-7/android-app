package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import goliath.proto.commands.Command;
import nl.team_goliath.app.R;
import nl.team_goliath.app.adapter.PresetAdapter;
import nl.team_goliath.app.databinding.PresetFragmentBinding;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.model.Preset;
import nl.team_goliath.app.model.Status;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusItem.CommandStatus;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusItem;
import nl.team_goliath.app.proto.CommandStatusRepositoryProto.CommandStatusRepository;
import nl.team_goliath.app.proto.DanceCommandProto.DanceCommand;
import nl.team_goliath.app.proto.EnterCommandProto.EnterCommand;
import nl.team_goliath.app.proto.InteruptCommandCommandProto.InterruptCommandCommand;
import nl.team_goliath.app.proto.LineDanceCommandProto.LineDanceCommand;
import nl.team_goliath.app.proto.ObstacleCourseCommandProto.ObstacleCourseCommand;
import nl.team_goliath.app.proto.SynchronizeCommandsCommandProto.SynchronizeCommandsCommand;
import nl.team_goliath.app.proto.TransportRebuildCommandProto.TransportRebuildCommand;
import nl.team_goliath.app.proto.WunderhornCommandProto.WunderhornCommand;
import nl.team_goliath.app.util.AutoClearedValue;
import nl.team_goliath.app.viewmodel.RepositoryViewModel;

public class PresetsFragment extends Fragment {
    private RepositoryViewModel repositoryViewModel;

    private AutoClearedValue<PresetFragmentBinding> binding;
    private AutoClearedValue<PresetAdapter> adapter;

    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

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
        repositoryViewModel = ViewModelProviders.of(getActivity()).get(RepositoryViewModel.class);

        PresetAdapter adapter = new PresetAdapter();
        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().presetList.setAdapter(adapter);
        initList(repositoryViewModel);
    }

    private void initList(RepositoryViewModel viewModel) {
        viewModel.getMessages().observe(this, resource -> {
            if (resource.status == Status.SUCCESS && !resource.data.isEmpty()) {
                ArrayList<Preset> presetList = new ArrayList<>();

                CommandStatusRepository repository = null;

                for (Message repo : resource.data) {
                    if (repo instanceof CommandStatusRepository) {
                        repository = (CommandStatusRepository) repo;
                    }
                }

                if (repository != null) {
                    for (CommandStatusItem item : repository.getStatusList()) {
                        boolean status = item.getCommandStatus() == CommandStatus.STARTED;

                        Preset preset = getPreset(item.getId(), status);

                        if (preset != null) {
                            presetList.add(preset);
                        }
                    }
                }

                if (adapter.get().getItems() == presetList) {
                    return;
                }

                for (Preset preset : presetList) {
                    final Observer<Boolean> commandObserver = newValue -> {
                        if (!preset.isActive() && preset.getCommandActive().getValue()) {
                            moveCallback.sendCommand(buildPresetCommand(preset));
                        }
                        else if (preset.isActive() && !preset.getCommandActive().getValue()){
                            CommandMessage commandMessage = CommandMessage.newBuilder()
                                    .setInterruptCommandCommand(InterruptCommandCommand.newBuilder()
                                            .setCommandId(preset.getId())
                                            .build())
                                    .build();

                            moveCallback.sendCommand(commandMessage);
                        }
                    };

                    preset.getCommandActive().observe(this, commandObserver);
                }

                adapter.get().replace(presetList);
            } else {
                adapter.get().replace(Collections.emptyList());

                moveCallback.sendCommand(CommandMessage.newBuilder()
                        .setSynchronizeCommandsCommand(SynchronizeCommandsCommand.newBuilder().build())
                        .build());
            }
        });
    }

    private Preset getPreset(int commandId, boolean status) {
        String name = "";

        switch  (commandId) {
            case CommandMessage.ENTERCOMMAND_FIELD_NUMBER:
                name = EnterCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
            case CommandMessage.DANCECOMMAND_FIELD_NUMBER:
                name = DanceCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
            case CommandMessage.LINEDANCECOMMAND_FIELD_NUMBER:
                name = LineDanceCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
            case CommandMessage.OBSTACLECOURSECOMMAND_FIELD_NUMBER:
                name = ObstacleCourseCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
            case CommandMessage.WUNDERHORNCOMMAND_FIELD_NUMBER:
                name = WunderhornCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
            case CommandMessage.TRANSPORTREBUILDCOMMAND_FIELD_NUMBER:
                name = TransportRebuildCommand.getDescriptor().getOptions().getExtension(Command.commandName);
                break;
        }

        if (!name.isEmpty()) {
            return new Preset(commandId, name, status);
        }
        return null;
    }

    private CommandMessage buildPresetCommand(Preset preset) {
        CommandMessage.Builder commandMessage = CommandMessage.newBuilder();

        switch (preset.getId()) {
            case CommandMessage.ENTERCOMMAND_FIELD_NUMBER:
                EnterCommand enterCommand = EnterCommand.newBuilder().build();
                commandMessage.setEnterCommand(enterCommand);
                break;
            case CommandMessage.DANCECOMMAND_FIELD_NUMBER:
                DanceCommand danceCommand = DanceCommand.newBuilder().build();
                commandMessage.setDanceCommand(danceCommand);
                break;
            case CommandMessage.LINEDANCECOMMAND_FIELD_NUMBER:
                LineDanceCommand lineDanceCommand = LineDanceCommand.newBuilder().build();
                commandMessage.setLineDanceCommand(lineDanceCommand);
                break;
            case CommandMessage.OBSTACLECOURSECOMMAND_FIELD_NUMBER:
                ObstacleCourseCommand obstacleCourseCommand = ObstacleCourseCommand.newBuilder().build();
                commandMessage.setObstacleCourseCommand(obstacleCourseCommand);
                break;
            case CommandMessage.WUNDERHORNCOMMAND_FIELD_NUMBER:
                WunderhornCommand wunderhornCommand = WunderhornCommand.newBuilder().build();
                commandMessage.setWunderhornCommand(wunderhornCommand);
                break;
            case CommandMessage.TRANSPORTREBUILDCOMMAND_FIELD_NUMBER:
                TransportRebuildCommand transportRebuildCommand = TransportRebuildCommand.newBuilder().build();
                commandMessage.setTransportRebuildCommand(transportRebuildCommand);
                break;
        }

        return commandMessage.build();
    }
}
