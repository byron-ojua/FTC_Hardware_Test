package org.firstinspires.ftc.teamcode.Autonomous.Modules;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Utilitys.Constants;
import org.firstinspires.ftc.teamcode.FTC_API.Autonomous.Modules.Module;
import org.firstinspires.ftc.teamcode.FTC_API.Examples.Drive;
import org.firstinspires.ftc.teamcode.FTC_API.Robot.SubSystems.SidedDriveSystemTemplate;

/**
 * Created by Byron.Nice on 8/30/2018.
 */

public class EncoderDrive extends Module {
    private SidedDriveSystemTemplate drive;
    private boolean isDone = false;
    private double startTime;

    private double leftRotations;
    private double rightRotations;
    private double speed = Constants.DEFAULT_SPEED;

    private int leftTarget;
    private int rightTarget;
    private double leftSpeed;
    private double rightSpeed;

    @Override
    public void start() {
        drive = (SidedDriveSystemTemplate) robot.getDriveSystem();
        startTime = robot.getTimeMilliseconds();


        //create speed for each motors in order to scale properly
        leftSpeed = speed;
        rightSpeed = speed;

        /*
        scale speed so that turns are relatively smooth, doesn't change anything if they are the same
        note that we are scaling by 2 so we have a differential effort
        */
        if (Math.abs(leftRotations) > Math.abs(rightRotations)) {
            double scale = Math.abs(rightRotations) / Math.abs(leftRotations);
            rightSpeed = rightSpeed * scale;
        } else if (Math.abs(rightRotations) > Math.abs(leftRotations)) {
            double scale = Math.abs(leftRotations) / Math.abs(rightRotations);
            leftSpeed = leftSpeed * scale;
        }


        //sets targets
        leftTarget = (int) (leftRotations * drive.getLeftSideMotors()[0].getMotorType().getTicksPerRev());
        rightTarget = (int) (rightRotations * drive.getRightSideMotors()[0].getMotorType().getTicksPerRev());

        //Note we use absolute value so if they already reversed them we don' have to worry
        if (leftTarget < 0) {
            leftSpeed = -(Math.abs(leftSpeed));//turn left speed negative if the left wheel is moving backwards
        }
        if (rightTarget < 0) {
            rightSpeed = -(Math.abs(rightSpeed));//turn right speed negative if the left wheel is moving backwards
        }

        drive.resetAllEncoders();
        drive.setLeftSideTarget(leftTarget);
        drive.setRightSideTarget(rightTarget);
        drive.runToPositionAllEncoders();

        //if either motor doesn't need to move then don't move it
        if (leftTarget == 0) {
            leftSpeed = 0;
        }
        if (rightTarget == 0) {
            rightSpeed = 0;
        }

        drive.driveTank(leftSpeed, rightSpeed);

        if (hasTelemetry()) {
            telemetry.log().add("Right Target:" + rightTarget + " Left Target:" + leftTarget);
            telemetry.log().add("Right Rotations:" + rightRotations + " Left Rotations:" + leftRotations);
            //telemetry.log().add("Position in Array:" + positionInArray);
        }
    }

    @Override
    public void tick() {
        int currentLeft = 0;
        int motorsLeft = 0;
        int currentRight = 0;
        int motorsRight = 0;

        for (DcMotor motor : drive.getLeftSideMotors()) {
            currentLeft += motor.getCurrentPosition();
            motorsLeft++;
        }
        for (DcMotor motor : drive.getRightSideMotors()) {
            currentRight += motor.getCurrentPosition();
            motorsRight++;
        }

        if (isDoneAtPosition(currentLeft / motorsLeft, leftTarget)) {
            drive.stopLeftMotors();
            leftSpeed = 0;
        }
        if (isDoneAtPosition(currentRight / motorsRight, rightTarget)) {
            drive.stopRightMotors();
            rightSpeed = 0;
        }

        if (leftSpeed == 0 && rightSpeed == 0) {
            isDone = true;
        }
    }

    //@Override

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public int stop() {
        drive.runUsingAllEncoders();
        //just pass through the position, this allows for "multithreaded" things that can be called
        return positionInArray;
    }

    /**
     * Resets the position in array number so you can changing it in the next step
     *
     * @return this object for building
     */
    public EncoderDrive resetPositionInArray() {
        positionInArray = 0;
        return this;
    }

    @Override
    public String[] requiredSubSystems() {
        return new String[]{Drive.ID};
    }


    public EncoderDrive setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * @param distanceLeft  distance in inches the left motor(s) should go
     * @param distanceRight distance in inches the right motor(s) should go
     * @return this object for building
     */
    public EncoderDrive setDistances(double distanceLeft, double distanceRight) {
        leftRotations = distanceLeft / Constants.WHEEL_CIRCUMFERENCE;
        rightRotations = distanceRight / Constants.WHEEL_CIRCUMFERENCE;
        return this;
    }

    private boolean isDoneAtPosition(double current, double target) {
        if (target >= 0) {//target is positive
            return current + 15 >= target || current - 15 >= target;
        } else {//otherwise it must be negative and should be treated as such
            return !(target < 0) || current + 15 <= target || current - 15 <= target;
        }
    }
}
