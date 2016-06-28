/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package displayManager;

import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 *
 * @author Dylan
 */
public class MouseHandler extends GLFWCursorPosCallback {
        public static double xpos;
        public static double ypos;
	@Override
	public void invoke(long window, double x, double y) {
                xpos=x;
                ypos=y;
		//System.out.println("X: " + xpos + " Y: " + ypos);
	}	
}
