/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_test;


/*
 * @author Dylan
 */

import entities.Camera;
import entities.Entity;
import entities.Light;
import static java.lang.Math.max;
import java.util.ArrayList;
import java.util.List;
import updateEngine.Update_Loop;
import renderEngine.Loader;
import models.RawModel;


import textures.ModelTexture;

import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.joml.Vector3f;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.system.MemoryUtil.NULL;
import renderEngine.MasterRenderer;
import terrains.Terrain;


public class Startup {
    private long window;
    
    //init callbacks
    
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback   cursorPosCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback   scrollCallback;
    
    private Camera camera= new Camera(new Vector3f(0,0,0),0f,0f,0f);
    
    
    private int scale;
    private boolean ctrlDown;
    //threads
    public static int width=1280;
    public static int height=820;
    public static float FOV=90;
    public double target_fps=60;
    public double fps;
    public double target_ups=60;
    public double ups;
    
    public void startGame() {
        init();
        
        Update_Loop updater= new Update_Loop();
        Loader loader= new Loader();
        
        
        
        double time;
        double prev_time=0;
        double dt;
        double sleep_time;
        double target_t_diff=1/target_fps;
        List<Double> fps_list= new ArrayList<Double>(8);
        for (int x=0; x<12; x++) {
           fps_list.add(target_fps);
        }
        
        //RawModel model= loader.loadToVAO(vertices, textureCoords, indices);
        RawModel model= OBJFileLoader.loadOBJ("dragon",loader);
        //ModelTexture texture= new ModelTexture(loader.loadTexture("sand.png"));
        TexturedModel staticModel= new TexturedModel(model,new ModelTexture(loader.loadTexture("gold.png")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10.0f);
        texture.setReflectivity(1);
        
        
        List<Entity> entities= new ArrayList<Entity>();
        //Entity dragon = new Entity(staticModel, new Vector3f(0f,-4.0f,-10f),0f,0f,0f,1f);
        
        for (int i=0; i< 1; i++) {
            //Entity dragon = new Entity(staticModel, new Vector3f(-10f+5*i,-4.0f,-10f),0f,0f,0f,1f);
            entities.add(new Entity(staticModel, new Vector3f(20f+60*i,0.0f,-40f),0f,0f,0f,5f));
        }
        
        
        
        Light light = new Light(new Vector3f(100,200,100), new Vector3f(1,1,1));
        
        Terrain terrain= new Terrain(0,0,loader,new ModelTexture(loader.loadTexture("grass.png")));
        Terrain terrain1= new Terrain(0,-1,loader,new ModelTexture(loader.loadTexture("brick.png")));
        updater.init(target_ups);
        updater.start();
        MasterRenderer renderer= new MasterRenderer();
        while (glfwWindowShouldClose(window) != true) {
            check_input();
            //update time
            time=glfwGetTime();
            dt=time-prev_time;
            prev_time=time;
            //
            
            //entity.increasePosition(0f, 0f, 0f);
            update();
            for (Entity entity:entities) {
                entity.increaseRotation((float)dt*0.0f, (float)dt*0.3f, (float)dt*0.0f);
            }
            //
            //render
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain1);
            for (Entity entity:entities){
                renderer.processEntity(entity);
            }
            
            renderer.render(light, camera);
            //constant fps
            fps=1/(dt);
            fps_list.remove(0);
            fps_list.add(fps);
            sleep_time=target_t_diff-(glfwGetTime()-prev_time);
            if (sleep_time>0){
                try{
                //System.out.println("render loop sleep time: "+sleep_time);
                Thread.currentThread().sleep((long)(sleep_time * 1000.0));
                } catch (InterruptedException ex) {
                ex.printStackTrace();}
            }
            
            glfwSwapBuffers(window);
        }
        renderer.cleanUp();
        updater.end();
        loader.cleanUp();
        dispose();
        
        //average fps
        System.out.println("fps "+calculateAverage(fps_list));
        
    }
    
    private void update(){
        
    }
    
    private double calculateAverage(List <Double> list) {
        double sum = 0.0;
        for (int i=0; i< list.size(); i++) {
              sum += list.get(i);
        }
        return sum / list.size();
    } 

    public void check_input(){
        glfwPollEvents();
        
    }
    
    public void init() {
        System.out.println("init");
        //glfwSetErrorCallback(errorCallback);
        errorCallback.createPrint().set();
        //setup window
        if (glfwInit() != true) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        //set opengl context (3.2)
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        window=glfwCreateWindow(width, height, "Game Window", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //key-key pressed
            //action- GLFW_PRESS, GLFW_RELEASE or GLFW_REPEAT
            //mods- a bitfield of modifier keys 
            //that were pressed, it can contain GLFW_MOD_SHIFT, 
            //GLFW_MOD_CONTROL, GLFW_MOD_ALT and GLFW_MOD_SUPER
            ctrlDown = (mods & GLFW_MOD_CONTROL) != 0;
            if ( action == GLFW_RELEASE )
                    return;

            switch ( key ) {
                    case GLFW_KEY_W:
                            camera.move_forward(.8f);
                            break;
                    case GLFW_KEY_S:
                            camera.move_backward(.8f);
                            break;
                    case GLFW_KEY_A:
                            camera.move_left(.8f);
                            break;        
                    case GLFW_KEY_D:
                            camera.move_right(.8f);
                            break;   
                    case GLFW_KEY_ESCAPE:
                            glfwSetWindowShouldClose(window, true);
                            break;
                    case GLFW_KEY_KP_ADD:
                    case GLFW_KEY_EQUAL:
                            setScale(scale + 1);
                            break;
                    case GLFW_KEY_KP_SUBTRACT:
                    case GLFW_KEY_MINUS:
                            setScale(scale - 1);
                            break;
                    case GLFW_KEY_0:
                    case GLFW_KEY_KP_0:
                            if ( ctrlDown )
                                    setScale(0);
                            break;
            }
        });
        
        glfwSetMouseButtonCallback(window, mouseButtonCallback = GLFWMouseButtonCallback.create((window, button, action, mods) -> {
            if ( action == GLFW_RELEASE )
                    return;

            switch ( button ) {
                    case GLFW_MOUSE_BUTTON_1:
            }
        }));
        
        glfwSetCursorPosCallback(window, cursorPosCallback = GLFWCursorPosCallback.create((window, xpos, ypos) -> {
            //System.out.println("CursorPos: " + xpos + "," + ypos);
            camera.setYaw((float)(xpos/width)*2f);//sensitivity
            camera.setPitch((float)(ypos/height)*2f);
            return;
        }));
        
        
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
        
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        
        //glfwSwapInterval(1);//v-sync
        //glfwShowWindow(window);
        
        //set callbacks
        //glfwSetKeyCallback(window, keyCallback);
        
        //input modes
        //Keyboard
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        
        //Mouse
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);//Mouse cursor
        //GLFW_CURSOR_NORMAL is the default mode
        //GLFW_CURSOR_HIDDEN makes the cursor invisible if it is over the window
        //GLFW_CURSOR_DISABLED will capture the cursor to the window and hides it, useful if you want to make a mouse motion camera control
        glfwSetInputMode(window, GLFW_STICKY_MOUSE_BUTTONS, GLFW_TRUE);
        
    }
    private void setScale(int scale) {
        this.scale = max(-3, scale);
    }
    public void dispose() {
        System.out.println("dispose");
        
        
        //dispose window
        glfwFreeCallbacks(window);
        
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
