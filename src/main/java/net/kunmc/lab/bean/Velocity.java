package net.kunmc.lab.bean;

public class Velocity {
    private double horizontalVelocity;
    private double verticalVelocity;

    public Velocity(double horizontalVelocity,double verticalVelocity){
        this.horizontalVelocity = horizontalVelocity;
        this.verticalVelocity = verticalVelocity;
    }

    public void setHorizontalVelocity(double horizontalVelocity){
        this.horizontalVelocity = horizontalVelocity;
    }
    public void setVerticalVelocity(double verticalVelocity){
        this.verticalVelocity = verticalVelocity;
    }
    public double getHorizontalVelocity(){
        return this.horizontalVelocity;
    }
    public double getVerticalVelocity(){
        return this.verticalVelocity;
    }
}
