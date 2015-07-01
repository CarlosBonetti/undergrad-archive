#include "person.h"
#include "utils.h"

void Person::draw() {
	glColor3f(0.435294f, 0.258824f, 0.258824f);

	trunk.draw();

	glPushMatrix();
		glTranslated(0, head.height * 2, 0);
		head.draw();
	glPopMatrix();

	// Left side (upper part)
	glPushMatrix();
		glTranslatef(trunk.width / 2 + larm.joint_radius / 2, trunk.height / 2 - larm.joint_radius / 2, 0);
		larm.draw();

		glTranslatef(0, 0, larm.length + lforearm.joint_radius / 2);
		lforearm.draw();

		glTranslatef(0, 0, larm.length + lhand.joint_radius / 2);
		lhand.draw();
	glPopMatrix();

	// Right side  (upper part)
	glPushMatrix();
		glTranslatef(-trunk.width / 2 - rarm.joint_radius / 2, trunk.height / 2 - rarm.joint_radius / 2, 0);
		larm.draw();

		glTranslatef(0, 0, rarm.length + rforearm.joint_radius / 2);
		lforearm.draw();

		glTranslatef(0, 0, rarm.length + rhand.joint_radius / 2);
		lhand.draw();
	glPopMatrix();

	// Left lower part
	glPushMatrix();
		glTranslatef(trunk.width * 0.3 + lthigh.joint_radius / 2, -trunk.height / 2 - lthigh.joint_radius / 2, lthigh.width / 2);
		lthigh.draw();

		glTranslatef(0, -lthigh.length / 2 - lleg.radius / 2, lleg.radius);
		lleg.draw();

		glTranslatef(0, -lleg.length - lfoot.joint_radius / 2, 0);
		lfoot.draw();
	glPopMatrix();

	// Right lower part
	glPushMatrix();
		glTranslatef(-trunk.width * 0.3 + rthigh.joint_radius / 2, -trunk.height / 2 - rthigh.joint_radius / 2, rthigh.width / 2);
		lthigh.draw();

		glTranslatef(0, -rthigh.length / 2 - rleg.radius / 2, rleg.radius);
		lleg.draw();

		glTranslatef(0, -rleg.length - rfoot.joint_radius / 2, 0);
		lfoot.draw();
	glPopMatrix();
}

void Trunk::draw() {
	draw_box(width, height, depth);
}

void Head::draw() {
	draw_box(width, height, depth);
}

void Hand::draw(){
	// Draw wrist joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0, 0, joint_radius / 2);

	// Draw hand
	draw_box(2,1,3);
}

void Forearm::draw() {
	// Draw elbow joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, 0.0f, joint_radius / 2);

	// Draw forearm
	auto quad = gluNewQuadric();
	gluCylinder(quad, radius, radius * 0.8, length, 30, 30);
	gluDeleteQuadric(quad);
}

void Arm::draw() {
	// Draw shoulder joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, 0.0f, joint_radius / 2);

	// Draw arm
	auto quad = gluNewQuadric();
	gluCylinder(quad, radius, radius, length, 30, 30);
	gluDeleteQuadric(quad);
}

void Thigh::draw() {
	// Draw joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, -length / 2, -width / 2);

	// Draw thigh
	draw_box(width, length, width);
}

void Leg::draw() {
	// Draw knee
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, -joint_radius / 2, 0);

	// Draw Leg
	auto quad = gluNewQuadric();
	glPushMatrix();
		glRotatef(90, 1.0f, 0.0f, 0.0f);
		gluCylinder(quad, radius, radius * 0.8, length, 30, 30);
	glPopMatrix();
	gluDeleteQuadric(quad);
}

void Foot::draw() {
	// Draw ankle
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0, 0, joint_radius / 2);

	// Draw foot
	draw_box(2, 1, 3);
}
