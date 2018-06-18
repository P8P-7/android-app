package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.MoveWingCommandProto.MoveWingCommand;
import nl.team_goliath.app.proto.MoveWingCommandProto.ServoCommand;
import nl.team_goliath.app.proto.MoveWingCommandProto.ServoCommand.Direction;
import nl.team_goliath.app.proto.MoveWingCommandProto.ServoCommand.Motor;

/**
 * Main UI for the wing control screen.
 */
public class WingFragment extends Fragment {
    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    public static WingFragment newInstance() {
        return new WingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.wing_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SeekBar wingSpeed = view.findViewById(R.id.wingSpeed);

        ArrayList<ImageButton> buttons = new ArrayList<>();

        buttons.add(view.findViewById(R.id.leftSide_up));
        buttons.add(view.findViewById(R.id.leftSide_down));
        buttons.add(view.findViewById(R.id.rightSide_up));
        buttons.add(view.findViewById(R.id.rightSide_down));

        ArrayList<ToggleSwitch> toggleSwitches = new ArrayList<>();

        toggleSwitches.add(view.findViewById(R.id.leftFront_wing));
        toggleSwitches.add(view.findViewById(R.id.leftBottom_wing));
        toggleSwitches.add(view.findViewById(R.id.rightFront_wing));
        toggleSwitches.add(view.findViewById(R.id.rightBottom_wing));

        for (ToggleSwitch toggleSwitch : toggleSwitches) {
            toggleSwitch.setOnToggleSwitchChangeListener((position, isChecked) -> {
                if (position == 0) {
                    toggleSwitch.setActiveBgColor(ContextCompat.getColor(getContext(), R.color.wingLeftColor));
                } else if (position == 2) {
                    toggleSwitch.setActiveBgColor(ContextCompat.getColor(getContext(), R.color.wingRightColor));
                } else {
                    toggleSwitch.setActiveBgColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }

                toggleSwitch.setCheckedTogglePosition(position, false);
            });

            toggleSwitch.setCheckedTogglePosition(1);
        }

        for (ImageButton button : buttons) {
            button.setOnClickListener(v -> {
                Direction direction = button.getId() == R.id.leftSide_up || button.getId() == R.id.rightSide_up ? Direction.UP : Direction.DOWN;
                int position = button.getId() == R.id.leftSide_up || button.getId() == R.id.leftSide_down ? 0 : 2;

                ArrayList<ServoCommand> servoCommands = new ArrayList<>();

                for (ToggleSwitch toggleSwitch : toggleSwitches) {
                    if (toggleSwitch.getCheckedTogglePosition() == position) {
                        servoCommands.add(buildServoCommand(wingSpeed.getProgress(), direction, idToMotor(toggleSwitch.getId())));
                    }
                }

                if (!servoCommands.isEmpty()) {
                    moveCallback.sendCommand(CommandMessage.newBuilder()
                            .setMoveWingCommand(buildMoveWingCommand(servoCommands))
                            .build());
                }
            });
        }
    }

    private Motor idToMotor(int id) {
        switch (id) {
            case R.id.leftFront_wing:
                return Motor.LEFT_FRONT;
            case R.id.leftBottom_wing:
                return Motor.LEFT_BACK;
            case R.id.rightFront_wing:
                return Motor.RIGHT_FRONT;
            case R.id.rightBottom_wing:
                return Motor.RIGHT_BACK;
            default:
                return null;
        }
    }

    private ServoCommand buildServoCommand(int speed, Direction direction, Motor motor) {
        return ServoCommand.newBuilder()
                .setSpeed(speed * 1023 / 100 + 1023)
                .setDirection(direction)
                .setMotor(motor)
                .build();
    }

    private MoveWingCommand buildMoveWingCommand(List<ServoCommand> servoCommands) {
        return MoveWingCommand.newBuilder().addAllCommands(servoCommands).build();
    }
}
