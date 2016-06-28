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
public class Entity {
    private TexturedModel model;
    public Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    private int textureIndex = 0;
    
    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotY = rotZ;
        this.scale = scale;
    }
    
    public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotY = rotZ;
        this.scale = scale;
    }
    
    public float getTextureXOffset() {
            int column = textureIndex % model.getTexture().getNumberOfRows();
            return (float) column / (float) model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset() {
            int row = textureIndex / model.getTexture().getNumberOfRows();
            return (float) row / (float) model.getTexture().getNumberOfRows();
    }
    public void increasePosition(float dx, float dy, float dz){
        position.add(dx,dy,dz);
    }
    
    public void increaseRotation(float dx, float dy, float dz ){
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }
    
    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
    
}
