/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import models.TexturedModel;
import org.joml.Vector3f;

/**
 *
 * @author Dylan
 */
public class Dragon extends Entity{
    
    
    private Camera camera;
    
    private float totalMass;
    private float tailMass;
    
    //camera angles
    private float dp=0;
    private float dy=0;
    private float dr=0;
    private float maxAngle=100.0f;
    
    
 
    public Dragon(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float mass) {
        super(model, position, rotX, rotY, rotZ, scale);
        this.totalMass=totalMass;
        this.tailMass=tailMass;
        this.camera=new Camera(new Vector3f(0,0,0),0,0,0);   
    }
    
    
    
    public void move(float dt){
        
        
        
        adjustAngle(dt, new Vector3f(dp,dy,dr));
        
        
        updateTail();
        cleanUp();
    }
    
    private void adjustAngle(float dt, Vector3f dtheta){
        //change in angle of the head is too large
        dtheta.div(dt);
        float mag=dtheta.length();
        if (mag<maxAngle){
            camera.addAngles(dtheta.x, dtheta.y, dtheta.z);
        }else{
            dtheta.mul(maxAngle/mag);
            camera.addAngles(dtheta.x, dtheta.y, dtheta.z);
        }   
    }
     
    private void updateTail(){
        
    }
    
    private void cleanUp(){
        dp=0;dy=0;dr=0;
    }
}
