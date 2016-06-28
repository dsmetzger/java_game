/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.joml.Vector3f;

/**
 *
 * @author Dylan
 */
public class Camera {
    //virtual camera class
    public Vector3f position;
    public float pitch;
    public float yaw;
    public float roll;
    
    public Camera(Vector3f position, float p,float y,float r){
        this.position=position;
        this.pitch=p;
        this.yaw=y;
        this.roll=r;
    }
    
    public void addAngles(float p,float y,float r){
        pitch+=p;
        yaw+=y;
        roll+=r;
    }
    
    public void move(Vector3f dp){
        position.add(dp);
    }

    
    public void move_forward(float dx){
        position.add(new Vector3f(dx*(float)sin(yaw),0,-dx*(float)cos(yaw)));
    }
    
    public void move_backward(float dx){
        position.add(new Vector3f(-dx*(float)sin(yaw),0,dx*(float)cos(yaw)));
    }
    
    public void move_left(float dx){
        position.add(new Vector3f(-dx*(float)cos(yaw),0,-dx*(float)sin(yaw)));
    }
    
    public void move_right(float dx){
        position.add(new Vector3f(dx*(float)cos(yaw),0,dx*(float)sin(yaw)));
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
    
    
}
