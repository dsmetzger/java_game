/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package displayManager;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Dylan
 */
public class Display {
    public long window;
    
    //init callbacks
    
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private GLFWCursorPosCallback mouseCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    
    //threads
    public static int width=1280;
    public static int height=820;
    
    public Display(){
        System.out.println("Display constructor");
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
        
        glfwSetKeyCallback(window, keyCallback = new KeyboardHandler());
        glfwSetCursorPosCallback(window, mouseCallback = new MouseHandler());
        glfwSetMouseButtonCallback(window, mouseButtonCallback = new MouseKeyHandler());
        
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
        
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        
        //glfwSwapInterval(1);//v-sync
        //glfwShowWindow(window);
        
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

    public void updateDisplay(){
        glfwSwapBuffers(window);
    }
    
    public void check_input(){
        glfwPollEvents();  
    }
    
    public void close_window(){
        glfwSetWindowShouldClose(window, true);
    }
    
    public void dispose() {
        System.out.println("Display dispose");
        //dispose window
        glfwFreeCallbacks(window);
        
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
