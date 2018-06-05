package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.MoveCommandProto.MotorCommand;
import nl.team_goliath.app.proto.MoveCommandProto.MoveCommand;

/**
 * Main UI for the control screen.
 */
public class ControlFragment extends Fragment {
    // 20/sec (every 50ms)
    private static final int DEFAULT_LOOP_INTERVAL = 50; // in milliseconds

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
        joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
            private int oldAngle = 0;
            private int oldStrength = 0;

            @Override
            public void onMove(int angle, int strength) {
                if (oldAngle != angle || oldStrength != strength) {
                    oldAngle = angle;
                    oldStrength = strength;

                    textViewAngleLeft.setText(getString(R.string.angle, angle));
                    textViewStrengthLeft.setText(getString(R.string.strength, strength));
                    moveCallback.sendCommand(buildMoveCommand(angle, strength, Arrays.asList(MotorCommand.Motor.LEFT_FRONT, MotorCommand.Motor.LEFT_BACK)));
                }
            }
        }, DEFAULT_LOOP_INTERVAL);

        textViewAngleRight = view.findViewById(R.id.textView_angle_right);
        textViewStrengthRight = view.findViewById(R.id.textView_strength_right);

        final JoystickView joystickRight = view.findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            private int oldAngle = 0;
            private int oldStrength = 0;

            @Override
            public void onMove(int angle, int strength) {
                if (oldAngle != angle || oldStrength != strength) {
                    oldAngle = angle;
                    oldStrength = strength;

                    textViewAngleRight.setText(getString(R.string.angle, angle));
                    textViewStrengthRight.setText(getString(R.string.strength, strength));
                    moveCallback.sendCommand(buildMoveCommand(angle, strength, Arrays.asList(MotorCommand.Motor.RIGHT_FRONT, MotorCommand.Motor.RIGHT_BACK)));
                }
            }
        }, DEFAULT_LOOP_INTERVAL);
    }

    private CommandMessage buildMoveCommand(int angle, int strength, List<MotorCommand.Motor> motors) {
        List<MotorCommand> motorCommands = new ArrayList<>();

        for (MotorCommand.Motor motor : motors) {
            motorCommands.add(MotorCommand.newBuilder()
                    .setSpeed((int) Math.ceil(strength * 2.55))
                    .setGear(angle < 180 ? MotorCommand.Gear.FORWARD : MotorCommand.Gear.BACKWARD)
                    .setMotor(motor)
                    .build());
        }

        MoveCommand moveCommand = MoveCommand.newBuilder()
                .addAllCommands(motorCommands)
                .build();

        return CommandMessage.newBuilder()
                .setMoveCommand(moveCommand)
                .build();
    }
}
