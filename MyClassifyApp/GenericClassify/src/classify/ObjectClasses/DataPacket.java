package classify.ObjectClasses;

import classify.Constants.C;

/**
 * Created by joe on 20/07/15.
 */
public class DataPacket {
    private double accelMag;
    private double accelX;
    private double accelY;
    private double accelZ;
    private double gyroMag;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
    private double quatW;
    private double quatX;
    private double quatY;
    private double quatZ;
    private double pitch;
    private double roll;
    private double yaw;
    private double batt;


    public DataPacket() {
    }

    public DataPacket(double accelMag, double accelX, double accelY, double accelZ, double gyroMag, double gyroX, double gyroY, double gyroZ, double quatW, double quatX, double quatY, double quatZ, double pitch, double roll, double yaw) {
        this.accelMag = accelMag;
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.gyroMag = gyroMag;
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.quatW = quatW;
        this.quatX = quatX;
        this.quatY = quatY;
        this.quatZ = quatZ;
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }

    public DataPacket(double accelMag, double accelX, double accelY, double accelZ, double gyroMag, double gyroX, double gyroY, double gyroZ, double quatW, double quatX, double quatY, double quatZ, double pitch, double roll, double yaw, double batt) {
        this.accelMag = accelMag;
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.gyroMag = gyroMag;
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.quatW = quatW;
        this.quatX = quatX;
        this.quatY = quatY;
        this.quatZ = quatZ;
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.batt = batt;
    }

    @Override
    public String toString() {
        return
                "accelMag= " + accelMag +
                        "\naccelX= " + accelX +
                        "\naccelY= " + accelY +
                        "\naccelZ= " + accelZ +
                        "\ngyroMag= " + gyroMag +
                        "\ngyroX= " + gyroX +
                        "\ngyroY= " + gyroY +
                        "\ngyroZ= " + gyroZ +
                        "\nquatW= " + quatW +
                        "\nquatX= " + quatX +
                        "\nquatY= " + quatY +
                        "\nquatZ= " + quatZ +
                        "\npitch= " + pitch +
                        "\nroll= " + roll +
                        "\nyaw= " + yaw +
                        "\nbatt= " + batt;
    }

    public double get(String signal) {
        switch (signal) {
            case C.ACCEL_MAG:
                return accelMag;
            case C.ACCEL_X:
                return accelX;
            case C.ACCEL_Y:
                return accelY;
            case C.ACCEL_Z:
                return accelZ;
            case C.GYRO_MAG:
                return gyroMag;
            case C.GYRO_X:
                return gyroX;
            case C.GYRO_Y:
                return gyroY;
            case C.GYRO_Z:
                return gyroZ;
            case C.QUAT_W:
                return quatW;
            case C.QUAT_X:
                return quatX;
            case C.QUAT_Y:
                return quatY;
            case C.QUAT_Z:
                return quatZ;
            case C.PITCH:
                return pitch;
            case C.ROLL:
                return roll;
            case C.YAW:
                return yaw;
            default:
                return 0;
        }

    }

    public double getAccelMag() {
        return accelMag;
    }

    public void setAccelMag(double accelMag) {
        this.accelMag = accelMag;
    }

    public double getAccelX() {
        return accelX;
    }

    public void setAccelX(double accelX) {
        this.accelX = accelX;
    }

    public double getAccelY() {
        return accelY;
    }

    public void setAccelY(double accelY) {
        this.accelY = accelY;
    }

    public double getAccelZ() {
        return accelZ;
    }

    public void setAccelZ(double accelZ) {
        this.accelZ = accelZ;
    }

    public double getGyroMag() {
        return gyroMag;
    }

    public void setGyroMag(double gyroMag) {
        this.gyroMag = gyroMag;
    }

    public double getGyroX() {
        return gyroX;
    }

    public void setGyroX(double gyroX) {
        this.gyroX = gyroX;
    }

    public double getGyroY() {
        return gyroY;
    }

    public void setGyroY(double gyroY) {
        this.gyroY = gyroY;
    }

    public double getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(double gyroZ) {
        this.gyroZ = gyroZ;
    }

    public double getQuatW() {
        return quatW;
    }

    public void setQuatW(double quatW) {
        this.quatW = quatW;
    }

    public double getQuatX() {
        return quatX;
    }

    public void setQuatX(double quatX) {
        this.quatX = quatX;
    }

    public double getQuatY() {
        return quatY;
    }

    public void setQuatY(double quatY) {
        this.quatY = quatY;
    }

    public double getQuatZ() {
        return quatZ;
    }

    public void setQuatZ(double quatZ) {
        this.quatZ = quatZ;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getBatt() {
        return batt;
    }

    public void setBatt(double batt) {
        this.batt = batt;
    }
}
