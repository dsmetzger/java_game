/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_test;

/**
 *
 * @author Dylan
 */
 
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
 
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLUtil;
import org.lwjgl.opencl.Info;
import org.lwjgl.system.MemoryUtil;
 
public class _SimpleCLTest
{
    public static void main(String[] args)
    {
        //Does anyone know why I cant call this? If I call this I get an error message that OpenCL has already been created.
        //CL.create();
         
        String[] platformNames = getPlatformNames();
        for(int i = 0; i < platformNames.length; i++)
        {
            String[] deviceNames = getDeviceNames(i);
            System.out.println("Platform: " + platformNames[i]);
            for(int j = 0; j < deviceNames.length; j++)
            {
                System.out.println("\tDevices: " + deviceNames[j]);
            }
        }
         
         
        for(int i = 0; i < platformNames.length; i++)
        {
            String[] deviceNames = getDeviceNames(i);
            CLPlatform platform = CLPlatform.getPlatforms().get(i);
            for(int j = 0; j < deviceNames.length; j++)
            {
                // Create an OpenCL context, this is where we could create an
                // OpenCL-OpenGL compatible context
                CLDevice device = platform.getDevices(CL10.CL_DEVICE_TYPE_ALL).get(j);
                 
                IntBuffer errorBuf = BufferUtils.createIntBuffer(1);
                PointerBuffer ctxProps = BufferUtils.createPointerBuffer(3);
                long context = CL10.clCreateContext(ctxProps, device.address(), CONTEXT_CALLBACK, MemoryUtil.NULL, errorBuf);
                CLUtil.checkCLError(errorBuf.get(0));
                System.out.println("Context: " + context);
                // Create a command queue
                long queue = CL10.clCreateCommandQueue(context, device.address(), CL10.CL_QUEUE_PROFILING_ENABLE, errorBuf);
                // Check for any errors
                CLUtil.checkCLError(errorBuf.get(0));
                System.out.println("Queue: " + queue);
                 
                FloatBuffer posBuffer = BufferUtils.createFloatBuffer(100 * 3);
                errorBuf = BufferUtils.createIntBuffer(1);
 
                float[] position = new float[100 * 3];
                 
                // Create a buffer containing our array of numbers, we can use the
                // buffer to create an OpenCL memory object
                posBuffer.put(position);
                posBuffer.rewind();
                // Create an OpenCL memory object containing a copy of the data buffer
                long positionMemory = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, posBuffer, errorBuf);
                // Check if the error buffer now contains an error
                CLUtil.checkCLError(errorBuf.get(0));
                 
                /**
                 * Until here everything seems to work fine - now comes the problematic code
                 */
                 
                // Create an OpenCL 'program' from a source code file
                long nBodyProgram = CL10.clCreateProgramWithSource(context, loadText(), errorBuf);
                CLUtil.checkCLError(errorBuf.get(0));
                // Build the OpenCL program, store it on the specified device
                int returnValue = CL10.clBuildProgram(nBodyProgram, device.address(), "-cl-fast-relaxed-math", null, MemoryUtil.NULL);
                boolean success = checkReturnValueBuildProgram(returnValue, nBodyProgram, device.address());
                if(success)
                {
                    // Create a kernel instance of our OpenCl program
                    long kernel = CL10.clCreateKernel(nBodyProgram, "test", null);
                    CL10.clSetKernelArg(kernel, 0, positionMemory);
                     
                    // Create a buffer of pointers defining the multi-dimensional size of
                    // the number of work units to execute
                    final int dimensions = 1;
                    PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions);
                    globalWorkSize.put(0, 100);
                    // Run the specified number of work units using our OpenCL program
                    // kernel
                    CL10.clEnqueueNDRangeKernel(queue, kernel, dimensions, null, globalWorkSize, null, null, null);
                    CL10.clFinish(queue);
                }
            }
        }
 
        CL.destroy();
    }
     
    //Down here some conventience methods
     
    public static String[] getPlatformNames()
    {
        ArrayList<string> platformNames = new ArrayList<>(0);
        CLPlatform.getPlatforms().forEach(element -> platformNames.add(getPlatformInfo(element, CL10.CL_PLATFORM_VENDOR).trim()));
        return platformNames.toArray(new String[platformNames.size()]);
    }
 
    public static String[] getDeviceNames(int index)
    {
        ArrayList<string> deviceNames = new ArrayList<>(0);
        CLPlatform.getPlatforms().get(index).getDevices(CL10.CL_DEVICE_TYPE_ALL).forEach(element -> deviceNames.add(getDeviceInfo(element, CL10.CL_DEVICE_NAME).trim()));
        return deviceNames.toArray(new String[deviceNames.size()]);
    }
     
    private static String getPlatformInfo(CLPlatform platform, int param) {
        return Info.clGetPlatformInfoStringUTF8(platform.address(), param);
    }
 
    private static String getDeviceInfo(CLDevice device, int param) {
        return Info.clGetDeviceInfoStringUTF8(device.address(), param);
    }
     
    public static final CLContextCallback CONTEXT_CALLBACK = new CLContextCallback() {
        @Override
        public void invoke(long errinfo, long private_info, long cb, long user_data) {
        }
    };
     
    public static String loadText()
    {
        StringBuilder clKernel = new StringBuilder();
        clKernel.append("kernel void test(global float* pos)                  {\n");
        clKernel.append("const int itemId = get_global_id(0);                  \n");
        clKernel.append("if(itemId < 100){                                      \n");
        clKernel.append("pos[itemId * 3 + 0] = pos[itemId * 3 + 0] + 1.0f;     \n");
        clKernel.append("pos[itemId * 3 + 1] = pos[itemId * 3 + 1] + 1.0f;     \n");
        clKernel.append("pos[itemId * 3 + 2] = pos[itemId * 3 + 2] + 1.0f;     \n");
        clKernel.append("}                                                     \n");
        clKernel.append("}                                                     \n");
        System.out.println(clKernel);
        return clKernel.toString();
    }
     
    public static boolean checkReturnValueBuildProgram(int returnValue, long program, long device)
    {
        if(returnValue != CL10.CL_SUCCESS)
        {
            switch(returnValue)
            {
                case CL10.CL_INVALID_PROGRAM : System.err.println("OCL: CL_INVALID_PROGRAM");break;
                case CL10.CL_INVALID_VALUE : System.err.println("OCL: CL_INVALID_VALUE");break;
                case CL10.CL_INVALID_DEVICE : System.err.println("OCL: CL_INVALID_DEVICE");break;
                case CL10.CL_INVALID_BINARY : System.err.println("OCL: CL_INVALID_BINARY");break;
                case CL10.CL_INVALID_BUILD_OPTIONS : System.err.println("OCL: CL_INVALID_BUILD_OPTIONS");break;
                case CL10.CL_INVALID_OPERATION : System.err.println("OCL: CL_INVALID_OPERATION");break;
                case CL10.CL_COMPILER_NOT_AVAILABLE : System.err.println("OCL: CL_COMPILER_NOT_AVAILABLE");break;
                case CL10.CL_BUILD_PROGRAM_FAILURE : System.err.println("OCL: CL_BUILD_PROGRAM_FAILURE");break;
                case CL10.CL_OUT_OF_HOST_MEMORY : System.err.println("OCL: CL_OUT_OF_HOST_MEMORY");break;
                default:System.err.println("OCL: UNKNOWN CL BUILD ERROR");break;                
            }
             
            ByteBuffer errorBuffer = BufferUtils.createByteBuffer(1024 * 10);
            CL10.clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_LOG, errorBuffer, null);
            errorBuffer.rewind();
            byte[] tmp = new byte[1024 * 10];
            errorBuffer.get(tmp);
            try
            {
                System.err.println("CL Build error: " + new String(tmp, "ASCII"));
            }
            catch (UnsupportedEncodingException e)
            {}
          return false;
        }
        else
        {
            return true;
        }
    }
}
