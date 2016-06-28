/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderEngine;

import entities.Entity;
import java.util.List;
import java.util.Map;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;
/**
 *
 * @author Dylan
 */
public class EntityRenderer{

    private StaticShader shader;
    
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
        this.shader=shader;
        
        //creatProjectionMatrix(Startup.width, Startup.height, Startup.FOV);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    
    public void render_init(){
    }

    
    public void render(Map<TexturedModel, List<Entity>> entities){
        for(TexturedModel model:entities.keySet()){
            prepareTexturedModel(model);
            List<Entity> batch=entities.get(model);
            for(Entity entity:batch){
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);//draw arrays
            }
        unbindTexturedModel();
        }
    }
    
    private void prepareTexturedModel(TexturedModel model){
        RawModel rawModel= model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());//bind vertices
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture= model.getTexture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if(texture.isHasTransparency()){
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        
    }
    
    private void unbindTexturedModel(){
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);//zero attribute list
        GL20.glDisableVertexAttribArray(1);//zero attribute list
        GL20.glDisableVertexAttribArray(2);//zero attribute list
        GL30.glBindVertexArray(0);//unbind vao
    }
    
    private void prepareInstance(Entity entity){
        Matrix4f transformationMatrix= Maths.createTransformationMatrix(entity.getPosition(),
                                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
    /*
    public void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel= model.getRawModel();
        
        GL30.glBindVertexArray(rawModel.getVaoID());//bind vertices
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        
        Matrix4f transformationMatrix= Maths.createTransformationMatrix(entity.getPosition(),
                                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        
        ModelTexture texture= model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);//draw arrays
        GL20.glDisableVertexAttribArray(0);//zero attribute list
        GL20.glDisableVertexAttribArray(1);//zero attribute list
        GL20.glDisableVertexAttribArray(2);//zero attribute list
        GL30.glBindVertexArray(0);//unbind vao
    }
    */
 
    
    public void render_end() {
    }
    
}
