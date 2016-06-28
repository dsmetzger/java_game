package updateEngine;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;

/**
 *
 * @author Dylan
 */
public class Update_Loop extends Thread{
    private boolean active=true;
    public double ups;
    public double target_ups;
    @Override
    public void run() {
        System.out.println("update thread run");
        //physics engine
        double time;
        double prev_time=0;
        double t_diff;
        double sleep_time=0;
        double target_t_diff=1/target_ups;
        while (active) {
            time=glfwGetTime();
            t_diff=time-prev_time;
            prev_time=time;
            //
            
            
            //input();
            
            
            //
            ups=1/(t_diff);
            sleep_time=target_t_diff-(glfwGetTime()-prev_time);
            if (sleep_time>0){
                try{
                //System.out.println("update loop sleep time: "+sleep_time);
                sleep((long)(sleep_time*1000.0));
                } catch (InterruptedException ex) {
                ex.printStackTrace();}
            }
        }
    }
    public void init(double target_ups1) {
        target_ups=target_ups1;
        System.out.println("Updater initiated");
    }
    public void input() {
        //glfwPollEvents();
    }
    public void end() {
        active=false;
    }
}
