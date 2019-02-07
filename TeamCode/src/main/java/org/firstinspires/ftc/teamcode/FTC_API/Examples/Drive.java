package org.firstinspires.ftc.teamcode.FTC_API.Examples;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.FTC_API.Robot.SubSystems.SidedDriveSystemTemplate;

/**
 * Created by Ethan Hampton on 8/19/17.
 * <p>
 * Simple Drive class that can be implemented and used
 */

public class Drive extends SidedDriveSystemTemplate {
    protected DcMotor leftMotor;
    protected DcMotor rightMotor;

    private String leftMotorName;
    private String rightMotorName;

    public static final String ID = "Drive";

    @Override
    public boolean init(HardwareMap hardwareDevices) {
        leftMotor = hardwareDevices.dcMotor.get(leftMotorName);
        rightMotor = hardwareDevices.dcMotor.get(rightMotorName);
        return true;
    }

    public Drive setMotorNames(String left, String right) {
        leftMotorName = left;
        rightMotorName = right;
        return this;
    }

    public void drive(double leftPower, double rightPower) {
        leftMotor.setPower(leftPower);
        rightMotor.setPower(rightPower);
    }

    @Override
    public DcMotor[] getRightSideMotors() {
        return new DcMotor[]{rightMotor};
    }

    @Override
    public DcMotor[] getLeftSideMotors() {
        return new DcMotor[]{leftMotor};
    }

    @Override
    public void driveTank(double leftPower, double rightPower) {
        drive(leftPower, rightPower);
    }

    @Override
    public void driveArcade(double forward, double turn) {
        double left = forward + turn;
        double right = forward - turn;

        driveTank(left, right);
    }

    @Override
    public void driveMecanum(double forward, double turn, double strafe) {
        driveArcade(forward, turn);
    }
}
