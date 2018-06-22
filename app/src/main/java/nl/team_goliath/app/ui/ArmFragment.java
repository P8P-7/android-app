package nl.team_goliath.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import nl.team_goliath.app.R;
import nl.team_goliath.app.model.CommandSender;
import nl.team_goliath.app.proto.CommandMessageProto.CommandMessage;
import nl.team_goliath.app.proto.GripCommandProto.GripCommand;
import nl.team_goliath.app.proto.MoveArmCommandProto.MoveArmCommand;

public class ArmFragment extends Fragment {
    private boolean gripping = false;

    private final CommandSender moveCallback = (commandMessage) -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            ((CommandSender) getActivity()).sendCommand(commandMessage);
        }
    };

    public static ArmFragment newInstance() {
        return new ArmFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.arm_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.armTucked_button).setOnClickListener(v -> {
            moveCallback.sendCommand(CommandMessage.newBuilder()
                        .setMoveArmCommand(MoveArmCommand.newBuilder()
                                .setPosition(MoveArmCommand.ArmPosition.TUCKED)
                                .build())
                    .build());
        });

        view.findViewById(R.id.armHigh_button).setOnClickListener(v -> {
            moveCallback.sendCommand(CommandMessage.newBuilder()
                    .setMoveArmCommand(MoveArmCommand.newBuilder()
                            .setPosition(MoveArmCommand.ArmPosition.HIGH)
                            .build())
                    .build());
        });

        view.findViewById(R.id.armMed_button).setOnClickListener(v -> {
            moveCallback.sendCommand(CommandMessage.newBuilder()
                    .setMoveArmCommand(MoveArmCommand.newBuilder()
                            .setPosition(MoveArmCommand.ArmPosition.MED)
                            .build())
                    .build());
        });

        view.findViewById(R.id.armLow_button).setOnClickListener(v -> {
            moveCallback.sendCommand(CommandMessage.newBuilder()
                    .setMoveArmCommand(MoveArmCommand.newBuilder()
                            .setPosition(MoveArmCommand.ArmPosition.LOW)
                            .build())
                    .build());
        });

        view.findViewById(R.id.grip_button).setOnClickListener((v) -> {
            if (gripping) {
                ((Button) v).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_swap_horiz_white_24dp);
            } else {
                ((Button) v).setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_compare_arrows_white_24dp);
            }

            moveCallback.sendCommand(CommandMessage.newBuilder()
                    .setGripCommand(GripCommand.newBuilder()
                            .setGripping(gripping)
                            .build())
                    .build());

            gripping = !gripping;
        });
    }
}
