/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencl;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.Configuration;


/**
 *
 * @author Dylan
 */
public class OpenclManager {
    
    public OpenclManager(){ 
        //Throws LWJGLException if an error occurs. If you get a message saying could not find CL Source(Or something similar), then
        //your hardware does not support openCL. (Try updating drivers first though). 
        CL.create();
        PointerBuffer pb = BufferUtils.createPointerBuffer(4);
        IntBuffer num = BufferUtils.createIntBuffer(1);
        int ret = CL10.clGetDeviceIDs(0L, CL10.CL_DEVICE_TYPE_GPU, pb, num); // <- if no CL.create() then crashes here
        
        //CL.createDeviceCapabilities(ret, platformCapabilities);
        CL.createPlatformCapabilities(ret); 
        
    }
    public void Destroy(){
        CL.destroy();
    }
}
