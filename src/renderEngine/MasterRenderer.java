/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import game_test._Startup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

/**
 *
 * @author Dylan
 */
public class MasterRenderer {
    private static final float FOV=90;
    private static final float NEAR_PLANE = .1f;
    private static final float FAR_PLANE = 1000f;// how far you can see in the distance
    
    private Matrix4f projectionMatrix;
    
    private StaticShader shader= new StaticShader();
    private EntityRenderer renderer;
    
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader= new TerrainShader();
    
    
    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
    
    //Sky color
    private static final float RED=.5f;
    private static final float GREEN=.5f;
    private static final float BLUE=.5f;
    
    
    
    public MasterRenderer(){
        enableCulling();
        creatProjectionMatrix();
        renderer= new EntityRenderer(shader, projectionMatrix);
        terrainRenderer= new TerrainRenderer(terrainShader, projectionMatrix);
    }
    
    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BACK);
    }
    
    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }
    
    public void prepare() {
        //create background color
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED,GREEN,BLUE,1);
    }
    public void render(List<Light> lights, Camera camera){
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
    }
    
    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }
    
    public void processEntity(Entity entity){
        TexturedModel entityModel=entity.getModel();
        List<Entity> batch= entities.get(entityModel);
        if(batch!=null){
            batch.add(entity);
        }else{
            List<Entity> newBatch= new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
            
        }
    }
    
    private void creatProjectionMatrix(){
        float aspectRatio = (float) _Startup.width / (float) _Startup.height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(_Startup.FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
 
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;        
    }
    
    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
    }
    
}
