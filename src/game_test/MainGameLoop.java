/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_test;

import displayManager.Display;
import displayManager.KeyboardHandler;
import displayManager.MouseHandler;
import displayManager.MouseKeyHandler;
import entities.Camera;
import entities.Entity;
import entities.Lamp;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

/**
 *
 * @author Dylan
 */
public class MainGameLoop {
    public static float FOV=90;
    public static double target_fps=30;
    public static double fps;
    public static double target_ups=30;
    public static double ups;
    
    private static Camera camera= new Camera(new Vector3f(0,5f,0),0f,0f,0f);
    private static Display display= new Display();
    private static Player player;
    
    
    public static void main(String[] args){        
        init();
        
        loop();
        
        dispose();
    }
    

    private static void loop(){
        
        
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
        
        
        
        
        
        
        //List<GuiTexture> guis = new ArrayList<GuiTexture>();
        List<Light> lights= new ArrayList<Light>();
        lights.add(new Light(new Vector3f(100,1000,-200), new Vector3f(.4f,.4f,.4f)));
        lights.add(new Light(new Vector3f(100,100,0), new Vector3f(0f,0f,10f),new Vector3f(1.0f,.01f,.001f)));
        lights.add(new Light(new Vector3f(0,100,-100), new Vector3f(10f,0f,0f),new Vector3f(1.0f,.01f,.001f)));
        //lights.add(new Light(new Vector3f(0,100,0), new Vector3f(0f,10f,0f),new Vector3f(1.0f,.1f,.001f)));
        // *********TERRAIN TEXTURE STUFF**********
        
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass.png"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt.png"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("moss.png"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("cobblestone.png"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap.png"));
        
        MasterRenderer renderer= new MasterRenderer();
        
        List<Terrain> terrains=new ArrayList<Terrain>();
        terrains.add(new Terrain(0, -1, loader, texturePack, blendMap, "heightmap.png"));
        
        // ************ENTITIES*******************
        List<Entity> entities= new ArrayList<Entity>();
        
        RawModel model= OBJFileLoader.loadOBJ("fern",loader);
        TexturedModel fern= new TexturedModel(model,new ModelTexture(loader.loadTexture("fernAtlas.png")));
        fern.getTexture().setHasTransparency(true);
        fern.getTexture().setNumberOfRows(2);
        
        RawModel model1= OBJFileLoader.loadOBJ("grassModel",loader);
        TexturedModel grass= new TexturedModel(model1,new ModelTexture(loader.loadTexture("grassTexture.png")));
        grass.getTexture().setUseFakeLighting(true);
        grass.getTexture().setHasTransparency(true);
        
        RawModel model2= OBJFileLoader.loadOBJ("box",loader);
        TexturedModel box= new TexturedModel(model2,new ModelTexture(loader.loadTexture("box.png")));
        
        RawModel model3= OBJFileLoader.loadOBJ("dragon",loader);
        TexturedModel dragon= new TexturedModel(model3,new ModelTexture(loader.loadTexture("gold.png")));
        dragon.getTexture().setShineDamper(10.0f);
        dragon.getTexture().setReflectivity(1);
        

        Random random = new Random();
        for (int i = 0; i < 60; i++) {
                if (i % 3 == 0) {
                        float x = random.nextFloat() * 300;
                        float z = random.nextFloat() * -300;
                        /*if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                        } else {*/
                                float y = terrains.get(0).getHeightOfTerrain(x, z);
                                entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0f,random.nextFloat() * 360,0f,1f));
                        //}
                }
                if (i % 2 == 0) {
                        float x = random.nextFloat() * 300;
                        float z = random.nextFloat() * -300;
                        /*if ((x > 50 && x < 100) || (z < -50 && z > -100)) {
                        }else {*/
                                float y = terrains.get(0).getHeightOfTerrain(x, z);
                                entities.add(new Entity(box, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 3f + 3f));
                        //}
                }
        }
        float y = terrains.get(0).getHeightOfTerrain(75, -75);
        entities.add(new Entity(dragon, new Vector3f(75, y, -75), 0, 0, 0, 1f));
        // *********LAMPS**********
        //new Light(new Vector3f(0,100,0), new Vector3f(0f,10f,0f),new Vector3f(1.0f,.1f,.001f))
        Lamp lamp = new Lamp(new Vector3f(0,100,0), new Vector3f(0f,3f,0f),new Vector3f(1.0f,.01f,.002f), random.nextFloat() * 6.28f, terrains.get(0), 1f);
        lights.add(lamp.getLight());
        entities.add(lamp.getEntity());
        
        
        // *********PLAYER**********
        RawModel pModel= OBJFileLoader.loadOBJ("person",loader);
        TexturedModel texturedPlayer= new TexturedModel(pModel,
                new ModelTexture(loader.loadTexture("playerTexture.png")));
        player = new Player(texturedPlayer, new Vector3f(0,0,5),0,0,0,1,70,2,2);
        
        // *******************GUI'S***************

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan.png"), new Vector2f(.5f, .5f),new Vector2f(.25f, .25f));
        guis.add(gui);//gui render depth depends on order of the guis
        
        GuiRenderer guiRenderer= new GuiRenderer(loader);
        
        // *******************GAME LOOP***************
        double dt_update=0;
        double dt_render=0;
        while (glfwWindowShouldClose(display.window) != true) {
            //get metrics
            time=glfwGetTime();
            dt=time-prev_time;
            prev_time=time;
            update(terrains.get(0), dt);
            //for (Entity entity:entities) {
                //entity.increaseRotation((float)dt*0.0f, (float)dt*0.3f, (float)dt*0.0f);
            //}
            
            double time_update=glfwGetTime();
            dt_update=time_update-time;
            
            //render player
            renderer.processEntity(player.entity);
            //render terrain
            for(Terrain terrain:terrains){
                renderer.processTerrain(terrain);
            }
            //renderer.processTerrain(terrain1);
            
            for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            renderer.render(lights, player.camera);
            //Render GUIs
            //guiRenderer.render(guis);
            
            dt_render=glfwGetTime()-time_update;
            //fps delay
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
            
            display.check_input();
            display.updateDisplay();
        }
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        System.out.println("fps         "+calculateAverage(fps_list));
        System.out.println("update time "+dt_update);
        System.out.println("render time "+dt_render);
    }

    private static void update(Terrain terrain, double dt){
        player.move(terrain,dt);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            camera.move_forward((float)dt*6f);
        }if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            camera.move_backward((float)dt*6f);
        }if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            camera.move_left((float)dt*6f);
        }if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            camera.move_right((float)dt*6f);
        }if(KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE)){
            display.close_window();
        }
        //System.out.println("X: " + MouseHandler.xpos + " Y: " + MouseHandler.ypos);
        camera.setYaw((float)(MouseHandler.xpos/display.width)*2f);//sensitivity
        camera.setPitch((float)(MouseHandler.ypos/display.height)*2f);
        if(MouseKeyHandler.isKeyDown(GLFW_MOUSE_BUTTON_1)){
                System.out.println("Mouse 1 Key Pressed");
        }
        
    }
    
    private static void dispose(){
        System.out.println("Main dispose");
        display.dispose();
    }    
    
    private static void init(){
        System.out.println("Main init");
    }
        
    
    
    private static double calculateAverage(List <Double> list) {
        double sum = 0.0;
        for (int i=0; i< list.size(); i++) {
              sum += list.get(i);
        }
        return sum / list.size();
    } 
}
