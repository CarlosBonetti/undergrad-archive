#ifndef UTILS_H_
#define UTILS_H_

#include <GL/glut.h>
#include "movement.h"

void apply_state(const movement::state& s){
    glRotated(s.rx,1,0,0);
    glRotated(s.ry,0,1,0);
    glRotated(s.rz,0,0,1);
}

void draw_box(double scaleX, double scaleY, double scaleZ) {
	glPushMatrix();
		glTranslatef(0, 0, scaleZ/2);
		glScalef(scaleX, scaleY, scaleZ);
		glutSolidCube(1);
	glPopMatrix();
}

#endif
