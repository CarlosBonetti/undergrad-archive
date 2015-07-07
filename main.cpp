#include <stdlib.h>
#include <stdio.h>
#include <GL/glut.h>
#include <math.h>

#include "person.h"
#include "floor.h"

#define ESCAPE 27

int window;
float rotation = 90; // Camera rotation
int time = 0;
int KEY_STATES[256];

void init_light() {
	// Let there be light
	GLfloat light_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	GLfloat light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	GLfloat light_position[]= { -30.0f, 30.0f, -20.0f, 1.0f };
	glPushMatrix();
		// Draw the light position just for debugging purposes...
		glTranslatef(light_position[0], light_position[1], light_position[2]);
		glutSolidCube(1);
	glPopMatrix();

	glLightfv(GL_LIGHT0, GL_POSITION, light_position);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	//glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);

	// Global ambient light
	GLfloat lmodel_ambient[] = { 0.5, 0.5, 0.5, 1.0 };
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);

	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);
	glEnable(GL_COLOR_MATERIAL); // Fiz para a cor funcionar junto com luz
}

void initGL() {
	glShadeModel(GL_SMOOTH); // Habilita sombreamento suavizado
	// Mistura cores de forma suave me um polígono

	glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Fundo negro
	glClearDepth(1.0f); // Inicialização do Depth Buffer
	glDepthFunc(GL_LEQUAL); // Define qual teste de profundidade vai ser feito
	glDepthMask(true);
	glEnable(GL_DEPTH_TEST);

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

	glLoadIdentity(); // Resete a corrente Modelview Matrix

	//gluLookAt(	sinf(rotation), 0, cosf(rotation),
	//			0, 0, 0,
	//			0, 1, 0);

	glPushMatrix();
		glTranslatef(0.0f, 3.0f, -23.0f);
		glRotatef(rotation, 0, 1, 0);
		Person().draw(time);
	glPopMatrix();

	glPushMatrix();
		glTranslatef(-50, -14.6, -100);
		Floor().draw();
	glPopMatrix();

	glutSwapBuffers();
}

void handle_keys() {
	if (KEY_STATES[GLUT_KEY_UP]) {
		time++;
	} else if (KEY_STATES[GLUT_KEY_DOWN]) {
		time--;
	}

	if (KEY_STATES[GLUT_KEY_RIGHT]) {
		rotation -= 2.0f;
	} else if (KEY_STATES[GLUT_KEY_LEFT]) {
		rotation += 2.0f;
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
		case GLUT_KEY_F1:
			glutFullScreen();
			break;
		case GLUT_KEY_F2:
			glutReshapeWindow(640, 480);
			break;
	}
}

void key_special_up(int key, int x, int y) {
	KEY_STATES[key] = false;
}

int main(int argc, char **argv) {
	glutInit(&argc, argv);
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
	initGL();
	glutMainLoop();
	return 0;
}
