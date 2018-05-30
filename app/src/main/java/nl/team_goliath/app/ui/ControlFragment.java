package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.protos.CommandMessageProtos.CommandMessage;
import nl.team_goliath.app.protos.MoveCommandProtos.MotorCommand;
import nl.team_goliath.app.protos.MoveCommandProtos.MoveCommand;
import nl.team_goliath.app.protos.MoveWingCommandProtos.MoveWingCommand;
import nl.team_goliath.app.protos.MoveWingCommandProtos.ServoCommand;

/**
 * Main UI for the control screen.
 */
public class ControlFragment extends Fragment {
    private TextView textViewAngleLeft;
    private TextView textViewStrengthLeft;

    private TextView textViewAngleRight;
    private TextView textViewStrengthRight;

    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewAngleLeft = view.findViewById(R.id.textView_angle_left);
        textViewStrengthLeft = view.findViewById(R.id.textView_strength_left);

        JoystickView joystickLeft = view.findViewById(R.id.joystickView_left);
        joystickLeft.setOnMoveListener((angle, strength) -> {
            textViewAngleLeft.setText(getString(R.string.angle, angle));
            textViewStrengthLeft.setText(getString(R.string.strength, strength));
            moveCallback.sendCommand(buildMoveWingCommand(angle, strength));
        }, 500);

        textViewAngleRight = view.findViewById(R.id.textView_angle_right);
        textViewStrengthRight = view.findViewById(R.id.textView_strength_right);

        final JoystickView joystickRight = view.findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener((angle, strength) -> {
            textViewAngleRight.setText(getString(R.string.angle, angle));
            textViewStrengthRight.setText(getString(R.string.strength, strength));
            moveCallback.sendCommand(buildMoveWingCommand(angle, strength));
        }, 500);
    }

    private CommandMessage buildMoveWingCommand(int angle, int strength) {
        MoveWingCommand moveWingCommand = MoveWingCommand.newBuilder()
                .addCommands(ServoCommand.newBuilder()
                        .setSpeed((int) Math.round(strength * 10.23))
                        .setDirection(angle < 180 ? ServoCommand.Direction.UP : ServoCommand.Direction.DOWN)
                        .setMotor(ServoCommand.Motor.LEFT_BACK))
                .build();

        MoveCommand moveCommand = MoveCommand.newBuilder()
                .addCommands(MotorCommand.newBuilder()
                        .setSpeed((int) Math.round(strength * 2.55))
                        .setGear(angle < 180 ? MotorCommand.Gear.FORWARD : MotorCommand.Gear.BACKWARD)
                        .setMotor(MotorCommand.Motor.LEFT_BACK))
                .build();

        return CommandMessage.newBuilder()
                //.setMoveWingCommand(moveWingCommand)
                .setMoveCommand(moveCommand)
                .build();
    }
}
