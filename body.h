#include <GL/glut.h>

void draw_box(double width, double height, double depth){
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

class Hand{
	public:
		void draw();
};

void Hand::draw(){
	draw_box(2,1,3);
}

class Forearm{
	public:
		void draw();
	private:
		double arm_length = 4;
		double arm_radius = 1;
};

void Forearm::draw(){
	glColor3f(0.435294f,0.258824f,0.258824f);
	
	auto quad = gluNewQuadric();
	gluCylinder(quad,arm_radius, arm_radius,  arm_length, 30,30);
	gluDeleteQuadric(quad);
	
	double joint_radius = 1.0;
	glTranslatef(0.0f,0.0f,arm_length + joint_radius/2);
	glutSolidSphere(joint_radius, 30,30);

	Hand().draw();
}
