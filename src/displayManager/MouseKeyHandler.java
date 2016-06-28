/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package displayManager;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 *
 * @author Dylan
 */
public class MouseKeyHandler extends GLFWMouseButtonCallback{
    
    public static boolean[] keys = new boolean[8];
    
    @Override
    public void invoke(long window, int button, int action, int mods) {
        keys[button] = action != GLFW_RELEASE;
    }
    
    public static boolean isKeyDown(int keycode) {
            return keys[keycode];
    }
}
