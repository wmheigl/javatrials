package JogAmp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class SimpleJoglApp extends JFrame {

  private static final long serialVersionUID = 1L;

  private GLU glu;
  private GLUT glut;

  public SimpleJoglApp() {

    // set the JFrame title
    super("JOGL 3D Sphere");

    // kill the process when the JFrame is closed
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // only three JOGL lines of code ... and here they are
    GLProfile glprofile = GLProfile.getDefault();
    GLCapabilities glcapabilities = new GLCapabilities(glprofile);
    final GLCanvas glcanvas = new GLCanvas(glcapabilities);
    glcanvas.addGLEventListener(new GLEventListener() {

      @Override
      public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	GL2 gl = drawable.getGL().getGL2();
	//
	gl.glViewport(0, 0, width, height);
	gl.glMatrixMode(GL2.GL_PROJECTION);
	gl.glLoadIdentity();
	glu.gluPerspective(100.0, (double) width / (double) height, 1.0, 20.0);
	gl.glMatrixMode(GL2.GL_MODELVIEW);
      }

      @Override
      public void init(GLAutoDrawable drawable) {
	GL2 gl = drawable.getGL().getGL2();
	drawable.setGL(new DebugGL2(gl));

	// Enable z- (depth) buffer for hidden surface removal. 
	gl.glEnable(GL2.GL_DEPTH_TEST);
	gl.glDepthFunc(GL2.GL_LEQUAL);
	
	// Enable smooth shading.
	gl.glShadeModel(GL2.GL_SMOOTH);

	// Define "clear" color.
	gl.glClearColor(0f, 0f, 0f, 1f);

	// We want a nice perspective.
	gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

	glu = new GLU();
	glut = new GLUT();

        // Prepare light parameters.
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

        // Enable lighting in GL.
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_LIGHTING);

        // Set material properties.
        float[] rgba = {0.3f, 0.5f, 1f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

      }

      @Override
      public void dispose(GLAutoDrawable drawable) {
      }

      @Override
      public void display(GLAutoDrawable drawable) {

	GL2 gl = drawable.getGL().getGL2();
	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

	setCamera(gl, glu, 50);

        gl.glColor3d(0.3, 0.5, 1.0);
	gl.glPushMatrix();
	/* glTranslatef() as viewing transformation */
	gl.glTranslatef(0.0f, 0.0f, -5.0f);
	glut.glutWireSphere(10, 20, 20);
	gl.glPopMatrix();
	gl.glFlush();

      }
    });

    // add the GLCanvas just like we would any Component
    getContentPane().add(glcanvas, BorderLayout.CENTER);
    setSize(500, 300);

    // center the JFrame on the screen
    centerWindow(this);

  }

  private void setCamera(GL2 gl, GLU glu, float distance) {
    // Change to projection matrix for the camera.
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();

    // Perspective.
    float widthHeightRatio = (float) getWidth() / (float) getHeight();
    glu.gluPerspective(20, widthHeightRatio, 1, 1000);
    glu.gluLookAt(distance, distance, distance, 0, 0, 0, 0, 1, 0);

    // Change back to model view matrix.
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  public static void main(String[] args) {
    final SimpleJoglApp app = new SimpleJoglApp();
    // show what we've done
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
	app.setVisible(true);
      }
    });
  }

  private void centerWindow(JFrame frame) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }
}
