package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * Main UI for the motor control screen.
 */
public class MotorFragment extends Fragment {
    // 20/sec (every 50ms)
    private static final int DEFAULT_LOOP_INTERVAL = 50; // in milliseconds

    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    public static MotorFragment newInstance() {
        return new MotorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.motor_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final JoystickView joystickLeft = view.findViewById(R.id.joystickView_left);
        joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
            private int oldAngle = 0;
            private int oldStrength = 0;

            @Override
            public void onMove(int angle, int strength) {
                if (oldAngle != angle || oldStrength != strength) {
                    oldAngle = angle;
                    oldStrength = strength;

                    moveCallback.sendCommand(buildMoveCommand(angle, strength, Arrays.asList(MotorCommand.Motor.LEFT_FRONT, MotorCommand.Motor.LEFT_BACK)));
                }
            }
        }, DEFAULT_LOOP_INTERVAL);

        final JoystickView joystickRight = view.findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            private int oldAngle = 0;
            private int oldStrength = 0;

            @Override
            public void onMove(int angle, int strength) {
                if (oldAngle != angle || oldStrength != strength) {
                    oldAngle = angle;
                    oldStrength = strength;

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
