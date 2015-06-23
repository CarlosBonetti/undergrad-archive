#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>

#define ESCAPE 27

int window;
int WIDTH;
int HEIGHT;

float rtri;
float rquad;

void initGL() {
	glShadeModel(GL_SMOOTH); // Habilita sombreamento suavizado
	// Mistura cores de forma suave me um polígono

	glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Fundo negro
	glClearDepth(1.0f); // Inicialização do Depth Buffer
	glEnable(GL_DEPTH_TEST); // Habilita teste de profundidade
	glDepthFunc(GL_LEQUAL); // Define qual teste de profundidade vai ser feito

	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Calculos de perspectiva legaizinhos
}

void windowResize(int width, int height) {
	WIDTH = width;
	HEIGHT = height;

	glViewport(0, 0, width, height);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	if (height==0 )  // Calculate The Aspect Ratio Of The Window
		gluPerspective ( 80, ( float ) width, 1.0, 5000.0 );
	else
		gluPerspective ( 80, ( float ) width / ( float ) height, 1.0, 5000.0 );

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void redraw() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Limpe a tela e o buffer
	//glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE); // Indique que os dois lados de qualquer superfície devem ser representados.
	glLoadIdentity(); // Resete a corrente Modelview Matrix

	glTranslatef(-1.5f,0.0f,-6.0f);
	glRotatef(rtri,0.0f,1.0f,0.0f);

	glBegin(GL_TRIANGLES); // início triângulo
		glColor3f(1.0f,0.0f,0.0f);          // Red
		glVertex3f( 0.0f, 1.0f, 0.0f);          // Top Of Triangle (Front)
		glColor3f(0.0f,1.0f,0.0f);          // Green
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Left Of Triangle (Front)
		glColor3f(0.0f,0.0f,1.0f);          // Blue
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Right Of Triangle (Front)
		glColor3f(1.0f,0.0f,0.0f);          // Red
		glVertex3f( 0.0f, 1.0f, 0.0f);          // Top Of Triangle (Right)
		glColor3f(0.0f,0.0f,1.0f);          // Blue
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Left Of Triangle (Right)
		glColor3f(0.0f,1.0f,0.0f);          // Green
		glVertex3f( 1.0f,-1.0f, -1.0f);         // Right Of Triangle (Right)
		glColor3f(1.0f,0.0f,0.0f);          // Red
		glVertex3f( 0.0f, 1.0f, 0.0f);          // Top Of Triangle (Back)
		glColor3f(0.0f,1.0f,0.0f);          // Green
		glVertex3f( 1.0f,-1.0f, -1.0f);         // Left Of Triangle (Back)
		glColor3f(0.0f,0.0f,1.0f);          // Blue
		glVertex3f(-1.0f,-1.0f, -1.0f);         // Right Of Triangle (Back)
		glColor3f(1.0f,0.0f,0.0f);          // Red
		glVertex3f( 0.0f, 1.0f, 0.0f);          // Top Of Triangle (Left)
		glColor3f(0.0f,0.0f,1.0f);          // Blue
		glVertex3f(-1.0f,-1.0f,-1.0f);          // Left Of Triangle (Left)
		glColor3f(0.0f,1.0f,0.0f);          // Green
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Right Of Triangle (Left)
	glEnd();                        // Done Drawing The Pyramid

	glLoadIdentity();
	glTranslatef(1.5f,0.0f,-6.0f);
	glRotatef(rquad,1.0f,0.0f,0.0f);
	glColor3f(0.5f,0.5f,1.0f);
	glBegin(GL_QUADS); // Draw A Quad
		glColor3f(0.0f,1.0f,0.0f);          // Set The Color To Green
		glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Top)
		glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Top)
		glVertex3f(-1.0f, 1.0f, 1.0f);          // Bottom Left Of The Quad (Top)
		glVertex3f( 1.0f, 1.0f, 1.0f);          // Bottom Right Of The Quad (Top)
		glColor3f(1.0f,0.5f,0.0f);          // Set The Color To Orange
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Top Right Of The Quad (Bottom)
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Top Left Of The Quad (Bottom)
		glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Bottom)
		glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Bottom)
		glColor3f(1.0f,0.0f,0.0f);          // Set The Color To Red
		glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
		glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Front)
		glColor3f(1.0f,1.0f,0.0f);          // Set The Color To Yellow
		glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Back)
		glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Back)
		glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Back)
		glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Back)
		glColor3f(0.0f,0.0f,1.0f);          // Set The Color To Blue
		glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Left)
		glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Left)
		glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Left)
		glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Left)
		glColor3f(1.0f,0.0f,1.0f);          // Set The Color To Violet
		glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Right)
		glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Right)
		glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Right)
		glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Right)
	glEnd(); // Done Drawing The Quad

	rtri+=0.4f;
	rquad-=0.3f;

	glutSwapBuffers();
}

void idle() {
	redraw();
}

void key_pressed(unsigned char key, int x, int y) {
	switch(key) {
		case ESCAPE:
			glutDestroyWindow(window);
			exit(0);
		default:
			printf("Key pressed: %c\n", key);
			break;
	}
}

void key_special(int key, int x, int y) {
	switch(key) {
		case GLUT_KEY_UP:
			glutFullScreen();
			break;
		case GLUT_KEY_DOWN:
			glutReshapeWindow(640, 480);
			break;
		default:
			printf("Key special: %c\n", key);
			break;
	}
}

int main(int argc, char **argv) {
	glutInit(&argc, argv);
	initGL();
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(800, 600);
	glutInitWindowPosition(0, 0);
	window = glutCreateWindow("Testing OpenGL");
	glutReshapeFunc(windowResize);
	glutDisplayFunc(redraw);
	glutIdleFunc(idle);
	glutKeyboardFunc(key_pressed);
	glutSpecialFunc(key_special);
	glutMainLoop();
	return 0;
}
