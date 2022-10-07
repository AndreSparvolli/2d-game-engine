package br.com.sparvolli.jade;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import br.com.sparvolli.util.Time;

public class Window {

	private static Window window = null; 
	private static Scene currentScene = null;

	private int width, height;
	private String title;
	private long glfwWindow;

	public float r, g, b, a;

	private Window() {
		this.width = 1600;
		this.height = 900;
		this.title = "Mario";
		this.r = 1;
		this.g = 1;
		this.b = 1;
		this.a = 1;
	}

	public static void changeScene( int newScene ) {
		switch ( newScene ) {
			case 0:
				currentScene = new LevelEditorScene();
				currentScene.init();
				break;
			case 1:
				currentScene = new LevelScene();
				currentScene.init();
				break;
			default:
				assert false : "Unknown Scene '" + newScene + "'"; 
				break;
		}
	}

	public static Window get() {
		if ( Window.window == null ) {
			Window.window = new Window();
		}

		return Window.window;
	}

	public void run() {
		System.out.println( "Hello LWGJL " + Version.getVersion() + "!" );

		init();
		loop();

		// Free the memory
		glfwFreeCallbacks( glfwWindow );
		glfwDestroyWindow( glfwWindow );

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback( null ).free();
	}

	private void init() {
		// Setup an error callback
		GLFWErrorCallback.createPrint( System.err );

		// Initialize GLFW
		if ( !glfwInit() ) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE );
		glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE );
		glfwWindowHint( GLFW_MAXIMIZED, GLFW_FALSE );

		// Create the window
		glfwWindow = glfwCreateWindow( this.width, this.height, this.title, NULL, NULL );

		if ( glfwWindow == NULL ) {
			throw new IllegalStateException( "Failed to create the GLFW window." );
		}

		glfwSetCursorPosCallback( glfwWindow, MouseListener::mousePosCallback );
		glfwSetMouseButtonCallback( glfwWindow,  MouseListener::mouseButtonCallback );
		glfwSetScrollCallback( glfwWindow,  MouseListener::mouseScrollCallback );
		glfwSetKeyCallback( glfwWindow,  KeyListener::keyCallback );

		// Make the OpenGL context current
		glfwMakeContextCurrent( glfwWindow );

		// Enable v-sync
		glfwSwapInterval( 1 );

		// Make the window visible
		glfwShowWindow( glfwWindow );

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		Window.changeScene( 0 );
	}

	private void loop() {
		float beginTime = Time.getTime();
		float endTime;
		float dt = -1.0f;

		while ( !glfwWindowShouldClose( glfwWindow ) ) {
			// Poll Events
			glfwPollEvents();

			glClearColor( r, g, b, a );
			glClear( GL_COLOR_BUFFER_BIT );

			if ( dt >= 0 ) {
				currentScene.update( dt );
			}

			glfwSwapBuffers( glfwWindow );

			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
}