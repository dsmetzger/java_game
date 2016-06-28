/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import displayManager.Display;
import displayManager.KeyboardHandler;
import displayManager.MouseHandler;
import static java.lang.Math.*;
import models.TexturedModel;
import org.joml.Vector3d;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import terrains.Terrain;

/**
 *
 * @author Dylan
 */
public class Player{
    public Camera camera;
    public static Entity entity;
    
    private static float xSensitivity;
    private static float ySensitivity;
    
    
    private static Vector3d velocity;
    private static float mass;
    
    
    private static int state=0;//0=running, 1= jumping, 2= in air
    //running constants
    private static double acceleration;
    private static final double deceleration=2.45;
    //jumping constants
    private static final double jumpAccel=120;
    private static final double gravAccel=-40;
    private static final double spaceTimeLim=.2;
    //jumping varibles
    private static double spaceTime=0;
    private static float TERRAIN_HEIGHT=0;
    //private static final double max_velocity_ground=1;//for bunny hopping
    
    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, 
            float mass, float xSens, float ySens) {
        //super(model, position, rotX, rotY, rotZ, scale);
        this.mass=mass;
        this.acceleration=7000/mass;//N/kg, needs to be normalized
        this.xSensitivity=xSens/Display.width;
        this.ySensitivity=ySens/Display.height;
        this.velocity= new Vector3d(0,0,0);
        this.camera= new Camera(position, rotX, rotY, rotZ);
        this.entity=new Entity(model, position, rotX, rotY, rotZ, scale);
    }
    
    /*
    private Vector3d accelerate(Vector3d accelDir, Vector3d prevVelocity, double accelerate, double max_velocity, double dt){
        double projVel = prevVelocity.dot(accelDir); // Vector projection of Current velocity onto accelDir.
        double accelVel = accelerate * dt; // Accelerated velocity in direction of movment
        // If necessary, truncate the accelerated velocity so the vector projection does not exceed max_velocity
        if(projVel + accelVel > max_velocity)
            accelVel = max_velocity - projVel;
        return prevVelocity.add(accelDir.mul(accelVel));
    }
    private Vector3d MoveGround(Vector3d accelDir, Vector3d prevVelocity, double dt){
        // Apply Friction
        double speed = prevVelocity.length();
        if (speed != 0) // To avoid divide by zero errors
        {
                double drop = speed * deceleration * dt;
                prevVelocity.mul(max(speed - drop, 0) / speed); // Scale the velocity based on friction.
        }

        // ground_accelerate and max_velocity_ground are server-defined movement variables
        return accelerate(accelDir, prevVelocity, acceleration, max_velocity_ground, dt);
    }
    
    public void bunnyMove(double dt){
        //update camera angle
        camera.setYaw((float)(MouseHandler.xpos)*xSensitivity);//sensitivity
        camera.setPitch((float)(MouseHandler.ypos)*ySensitivity);
        //update velocity
        if(state==0){
            Vector3d accelDir= new Vector3d(0,0,0);
            if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
                accelDir= new Vector3d(sin(camera.yaw), 0, -cos(camera.yaw));
            }else if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
                accelDir= new Vector3d(-sin(camera.yaw), 0, cos(camera.yaw));
            }
            
            if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
                accelDir.add(-cos(camera.yaw), 0, -sin(camera.yaw));
            }else if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
                accelDir.add(cos(camera.yaw), 0, sin(camera.yaw));
            }
            
            System.out.println(accelDir);
            //accelerate
            if (accelDir.x!=0){accelDir.normalize();}
            System.out.println(accelDir);
            velocity=MoveGround(accelDir, velocity, dt);
            //update position
            entity.increasePosition((float)(velocity.x*dt),(float)(velocity.y*dt),(float)(velocity.z*dt));
            System.out.println("velocity "+velocity);
            //System.out.println("position "+entity.getPosition());
        }
    }
    */
    public void move(Terrain terrain, double dt){
        //update camera angle
        camera.setYaw((float)(MouseHandler.xpos)*xSensitivity);//sensitivity
        camera.setPitch((float)(MouseHandler.ypos)*ySensitivity);
        entity.setRotY(-camera.getYaw()+3.14159f);
        //update velocity-- constant
        /*
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            velocity.add(dt*sin(camera.yaw), 0, -dt*cos(camera.yaw));
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            velocity.add(-dt*sin(camera.yaw), 0, dt*cos(camera.yaw));
        }
        
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            velocity.add(-dt*cos(camera.yaw), 0, -dt*sin(camera.yaw));
        }else if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            velocity.add(dt*cos(camera.yaw), 0, dt*sin(camera.yaw));
        }
        */
        //update velocity-- acceleration based
        TERRAIN_HEIGHT=terrain.getHeightOfTerrain(entity.position.x, entity.position.z);
        if(state==0){
            Vector3d accelDir= new Vector3d(0,0,0);
            if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
                accelDir= new Vector3d(sin(camera.yaw), 0, -cos(camera.yaw));
            }else if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
                accelDir= new Vector3d(-sin(camera.yaw), 0, cos(camera.yaw));
            }
            
            if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
                accelDir.add(-cos(camera.yaw), 0, -sin(camera.yaw));
            }else if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
                accelDir.add(cos(camera.yaw), 0, sin(camera.yaw));
            }
            
            if(KeyboardHandler.isKeyDown(GLFW_KEY_SPACE)){
                spaceTime+=dt;
            }else if(spaceTime>0){
                if (spaceTime>spaceTimeLim){
                    spaceTime=spaceTimeLim;
                }
                state=1;
                System.out.println(spaceTime);
            }
            
            //decelerate(when on ground
            /*double len=velocity.length();
            
            if(len>.0000000001){
                double mag=pow(len*dt*deceleration,2);
                if(mag!=0){
                    velocity.x/=mag;
                    velocity.z/=mag;
                }
            }else{
                velocity.x=0;
                velocity.z=0;
            }
            */
            //decelerate(when on ground
            if (accelDir.x!=0){accelDir.normalize();}
            double speed = velocity.add(accelDir.mul(dt*acceleration)).length();
            if (speed != 0) // To avoid divide by zero errors
            {
                double drop = speed * deceleration * dt;
                velocity.mul(max(speed - drop, 0) / speed); // Scale the velocity based on friction.
            }
            //update position
            //camera.move(new Vector3f((float)(velocity.x*dt),(float)(velocity.y*dt),(float)(velocity.z*dt)));
            //entity.increaseRotation(mass, mass, mass);
            //entity.setRotX(camera.getPitch());//head up and down
            
            entity.increasePosition((float)(velocity.x*dt),(float)(velocity.y*dt),(float)(velocity.z*dt));
            entity.position.y=(float)TERRAIN_HEIGHT;
        }else if(state==1){//jumping
            /*private static double jumpAccel=.1;
                private static double spaceTime=0;
                private static double gravAccel=-9.8;*/
            spaceTime-=dt;
            
            //increase vertical velocity
            velocity.y+=jumpAccel*dt;
            entity.increasePosition((float)(velocity.x*dt),0,(float)(velocity.z*dt));
            
            entity.position.y=(float)TERRAIN_HEIGHT;
            
            if(spaceTime<=0){
                state=2;
                spaceTime=0;
            }
        }else if(state==2){//in air
            //increase entity position
            velocity.y+=gravAccel*dt;
            entity.increasePosition((float)(velocity.x*dt),(float)(velocity.y*dt),(float)(velocity.z*dt));
            if(entity.position.y<=TERRAIN_HEIGHT){
                entity.position.y=(float)TERRAIN_HEIGHT;
                velocity.y=0;
                state=0;
            }
        }
        
        
        //set camera position
        Vector3f cameraPos=new Vector3f(entity.getPosition());
        camera.setPosition(cameraPos.add(0,10,0));//y=10
    }
}
