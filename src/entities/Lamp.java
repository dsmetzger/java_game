/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import org.joml.Vector3f;
import renderEngine.Loader;
import terrains.Terrain;
import textures.ModelTexture;
/**
 *
 * @author Dylan
 */
public class Lamp {
    private Light light;
    private Entity entity;

    public Light getLight() {
        return light;
    }

    public Entity getEntity() {
        return entity;
    }
    
    public Lamp(Vector3f position, Vector3f color, Vector3f attenuation, float Roty, Terrain terrain, Float scale){
        float y1 = terrain.getHeightOfTerrain(position.x, position.z);
        this.light=new Light(new Vector3f(position.x, y1+15f*scale, position.z),color,attenuation);
        //Vector3f position= light.getPosition();
        
        Loader loader= new Loader();
        RawModel model1= OBJFileLoader.loadOBJ("lamp",loader);
        TexturedModel lamp= new TexturedModel(model1,new ModelTexture(loader.loadTexture("lamp.png")));
        lamp.getTexture().setUseFakeLighting(true);
        
        this.entity= new Entity(lamp, new Vector3f(position.x, y1, position.z), 0, Roty, 0, scale);
    }
}
