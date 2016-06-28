/*
Loader holds the VAO VBO utilities
 */
package renderEngine;

/**
 *
 * @author Dylan
 */

import models.RawModel;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import static org.lwjgl.BufferUtils.createByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.stb.STBImage.*;

public class Loader {
    private List<Integer> vaos= new ArrayList<Integer>();
    private List<Integer> vbos= new ArrayList<Integer>();
    private List<Integer> textures= new ArrayList<Integer>();
    
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
            int vaoID = createVAO();
            bindIndicesBuffer(indices);
            storeDataInAttributeList(0, 3, positions);
            storeDataInAttributeList(1, 2, textureCoords);
            storeDataInAttributeList(2, 3, normals);
            unbindVAO();
            return new RawModel(vaoID, indices.length);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
            int vaoID = createVAO();
            storeDataInAttributeList(0, 2, positions);
            storeDataInAttributeList(1, 2, textureCoords);
            unbindVAO();
            return vaoID;
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
            int vaoID = createVAO();
            bindIndicesBuffer(indices);
            storeDataInAttributeList(0, 3, positions);
            storeDataInAttributeList(1, 2, textureCoords);
            storeDataInAttributeList(2, 3, normals);
            storeDataInAttributeList(3, 3, tangents);
            unbindVAO();
            return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
            int vaoID = createVAO();
            this.storeDataInAttributeList(0, dimensions, positions);
            unbindVAO();
            return new RawModel(vaoID, positions.length / dimensions);
    }
     
    public RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices){
        int vaoID=createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }
    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length/2);
    }
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }    
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        System.out.println("load image: "+ resource);
        Path path = Paths.get("res/textures/"+resource);
        if ( Files.isReadable(path) ) {
                try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                        buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                        while ( fc.read(buffer) != -1 ) ;
                }
        } else {
                try (
                        InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream("res/textures/"+resource);
                        ReadableByteChannel rbc = Channels.newChannel(source)
                ) {
                        buffer = createByteBuffer(bufferSize);

                        while ( true ) {
                                int bytes = rbc.read(buffer);
                                if ( bytes == -1 )
                                        break;
                                if ( buffer.remaining() == 0 )
                                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                }
        }

        buffer.flip();
        return buffer;
    }
    public int loadTexture(String fileName) {
        //Texture texture = null;
        //-----
        ByteBuffer imageBuffer;
        try {
                imageBuffer = ioResourceToByteBuffer(fileName, 8192/*8*256*256*/);
        } catch (IOException e) {
                throw new RuntimeException(e);
        }

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if ( stbi_info_from_memory(imageBuffer, w, h, comp) == 0 )
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());

        System.out.println("Image width: " + w.get(0));
        System.out.println("Image height: " + h.get(0));
        System.out.println("Image components: " + comp.get(0));
        System.out.println("Image HDR: " + (stbi_is_hdr_from_memory(imageBuffer) == 1));

        // Decode the image
        ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
        if ( image == null )
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        
        
        // Create a new texture object in memory and bind it
        int texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        // All RGB bytes are aligned to each other and each component is 1 byte
        //GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        //Setup filtering, i.e. how OpenGL will interpolate the pixels when scaling up or down
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        //Setup wrap mode, i.e. how OpenGL will handle pixels outside of the expected range
        //Note: GL_CLAMP_TO_EDGE is part of GL12
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w.get(0), h.get(0), 0, 
                        GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -.4f);
        
        stbi_image_free(image);
        //image.clear();
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        textures.add(texId);
        return texId;
    }

    public int loadTexture1(String fileName) {
        int texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
        //Setup filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
        
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        
        ByteBuffer image = stbi_load("res/textures/"+fileName, w, h, comp, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file!"
                    + System.lineSeparator() + stbi_failure_reason());
        }

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w.get(0), h.get(0), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
        textures.add(texId);
        return texId;
    }
    
    public void cleanUp(){
        for(int vao:vaos){
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo:vbos){
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture:textures){
            GL11.glDeleteTextures(texture);
        }
    
    }
    public int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data){
        int vboID=GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer=storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    private void unbindVAO() {
            GL30.glBindVertexArray(0);
    }
    private void bindIndicesBuffer(int[] indices){
        int vboID=GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);//bind indices buffer
        IntBuffer buffer= storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }
    private IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer=BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
            buffer.put(data);
            buffer.flip();
            return buffer;
    }
}
