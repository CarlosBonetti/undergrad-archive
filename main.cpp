#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>

#include "person.h"

#define ESCAPE 27

int window;
float rtri = 90;
int time = 0;
int KEY_STATES[256];

void init_light() {
	// Let there be light
	GLfloat light_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	GLfloat light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	GLfloat light_position[]= { 0.0f, 0.0f, 20.0f, 1.0f };

	glLightfv(GL_LIGHT0, GL_POSITION, light_position);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	//glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);
}

void initGL() {
	glShadeModel(GL_SMOOTH); // Habilita sombreamento suavizado
	// Mistura cores de forma suave me um polígono

	glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Fundo negro
	glClearDepth(1.0f); // Inicialização do Depth Buffer
	glDepthFunc(GL_LEQUAL); // Define qual teste de profundidade vai ser feito
	glDepthMask(true);

	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Calculos de perspectiva legaizinhos
	init_light();
}

void windowResize(int width, int height) {
	glViewport(0, 0, width, height);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	if (height == 0)
		height = 1;

	// Calculate The Aspect Ratio Of The Window
	gluPerspective(80, (GLfloat) width / (GLfloat) height, 1.0f, 500.0);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void redraw() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Limpe a tela e o buffer
	//glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE); // Indique que os dois lados de qualquer superfície devem ser representados.
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);

	glLoadIdentity(); // Resete a corrente Modelview Matrix

	rtri+=0.7f;
	glTranslatef(0.0f, 3.0f, -23.0f);
	glRotatef(rtri,0.0f,1.0f,0);
	Person().draw(time);
	glutSwapBuffers();
}

void handle_keys() {
	if (KEY_STATES[GLUT_KEY_RIGHT]) {
		time++;
	}
    if (KEY_STATES[GLUT_KEY_LEFT]) {
		time--;
	}
}

void idle() {
	handle_keys();
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

void key_special_down(int key, int x, int y) {
	KEY_STATES[key] = true;
	switch(key) {
		case GLUT_KEY_UP:
			glutFullScreen();
			break;
		case GLUT_KEY_DOWN:
			glutReshapeWindow(640, 480);
			break;
	}
}

void key_special_up(int key, int x, int y) {
	KEY_STATES[key] = false;
}

int main(int argc, char **argv) {
	glutInit(&argc, argv);
	initGL();
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH);
	glutInitWindowSize(800, 800);
	glutInitWindowPosition(0, 0);
	window = glutCreateWindow("Testing OpenGL");
	glutReshapeFunc(windowResize);
	glutDisplayFunc(redraw);
	glutIdleFunc(idle);
	glutKeyboardFunc(key_pressed);
	glutSpecialFunc(key_special_down);
	glutSpecialUpFunc(key_special_up);
	glutMainLoop();
	return 0;
}
