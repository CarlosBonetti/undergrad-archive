#ifndef UTILS_H_
#define UTILS_H_

#include <GL/glut.h>
#include "movement.h"
void apply_state(const movement::state& s){
    glRotated(s.rx,1,0,0);
    glRotated(s.ry,0,1,0);
    glRotated(s.rz,0,0,1);
}


void draw_box(double width, double height, double depth) {
	double _width = width/2,
		   _height = height/2,
		   _depth = depth;

	glBegin(GL_QUADS); // Draw A Quad
		glVertex3f(-_width, _height, 0.0f);
		glVertex3f( _width, _height, 0.0f);
		glVertex3f( _width,-_height, 0.0f);
		glVertex3f(-_width,-_height, 0.0f);

		glVertex3f(-_width, _height, _depth);
		glVertex3f( _width, _height, _depth);
		glVertex3f( _width,-_height, _depth);
		glVertex3f(-_width,-_height, _depth);

		glVertex3f(-_width, _height, 0.0f);
		glVertex3f(-_width,-_height, 0.0f);
		glVertex3f(-_width,-_height, _depth);
		glVertex3f(-_width, _height, _depth);

		glVertex3f(_width, _height, 0.0f);
		glVertex3f(_width,-_height, 0.0f);
		glVertex3f(_width,-_height, _depth);
		glVertex3f(_width, _height, _depth);

		glVertex3f( _width,-_height, 0.0f);
		glVertex3f(-_width,-_height, 0.0f);
		glVertex3f(-_width,-_height, _depth);
		glVertex3f( _width,-_height, _depth);
	glEnd();
}



#endif
