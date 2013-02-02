package org.jafelker.screensaver;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class Main {
	static boolean running = true;
	static String bool = "true";
	static int posX = 0, posY = 0, rowWidth = 110, boxSize, rowCount = 1;
	static double color = .1, bgColor = 0;

	static int DISPLAY_WIDTH, DISPLAY_HEIGHT;

	public static void main(String[] args) {
		try {
			setDisplayMode(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight(), false);
			System.setProperty("org.lwjgl.opengl.Window.undecorated", bool);
			posX = 0;
			//posX = -1366;
			posY = 0;
			Display.setLocation(posX, posY);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		DISPLAY_WIDTH = Display.getDesktopDisplayMode().getWidth();
		DISPLAY_HEIGHT = Display.getDesktopDisplayMode().getHeight();
		boxSize = DISPLAY_HEIGHT / 10;
		initGL();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		while (running) {
			update();
			Display.sync(60);
			render();
			if (Display.isCloseRequested())
				running = false;
		}
		Display.destroy();
	}

	private static void checkKeys() {
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			if (key == Keyboard.KEY_SPACE) {
				if (bool == "true")
					bool = "false";
				else if (bool == "false")
					bool = "true";
				posX = Display.getX();
				posY = Display.getY();
				Display.destroy();
				setDisplayMode(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight(), false);
				System.setProperty("org.lwjgl.opengl.Window.undecorated", bool);
				Display.setLocation(posX, posY);
				try {
					Display.create();
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				initGL();
			}
			if (key == Keyboard.KEY_ESCAPE)
				running = false;
			if (key == Keyboard.KEY_1)
				Display.setLocation(-1366, 0);
			if (key == Keyboard.KEY_2)
				Display.setLocation(0, 0);
		}
	}

	private static void update() {
		checkKeys();
	}

	private static void render() {
		glColor3d(bgColor, bgColor, bgColor);
		glRecti(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		glColor3d(color, color, color);

		for (int i = 1; i <= rowCount; i++) {
			if (i != rowCount)
				glRecti(0, (i - 1) * boxSize, DISPLAY_WIDTH, i * boxSize);
			else
				glRecti(0, (i - 1) * boxSize, rowWidth, i * boxSize);
		}

		rowWidth += boxSize;
		if (rowWidth > DISPLAY_WIDTH) {
			rowWidth = 0;
			rowCount++;
		}

		if ((rowCount - 1) * boxSize >= DISPLAY_HEIGHT) {
			rowCount = 1;
			color += .1;
			bgColor += .1;
		}

		if (color >= .49999)
			color = 0;
		if (bgColor >= .49999)
			bgColor = 0;

		Display.update();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	}

	private static void initGL() {
		glShadeModel(GL_SMOOTH);
		glDisable(GL_LIGHTING);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, DISPLAY_WIDTH, DISPLAY_HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
	}

	public static void setDisplayMode(int width, int height, final boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen)) {
			return;
		}

		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				int freq = 0;
				for (DisplayMode current : Display.getAvailableDisplayModes()) {
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}
}
