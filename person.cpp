#include "person.h"
#include "moonwalk.h"
#include "run.h"
#include "utils.h"


void Person::draw(int t) {
    if(currentMode == MOONWALKING)
        draw_aux<movement::moonwalk_t, movement::moonwalk>(t);
    if(currentMode == RUNNING)
        draw_aux<movement::run_t, movement::run>(t);
}

template<typename T, T& mode>
void Person::draw_aux(int t) {
    glPushMatrix();

    	glColor3f(0.435294f, 0.258824f, 0.258824f);
        auto d = mode.bodyMovement(t);
        glTranslatef(0, d.ty, 0);
    
    
    	trunk.draw(t);
    
    	glPushMatrix();
    		glTranslated(0, head.height * 2, 0);
    		head.draw(t);
    	glPopMatrix();
    
    	// Left side (upper part)
    	glPushMatrix();
    		glTranslatef(trunk.width / 2 + larm.joint_radius / 2, trunk.height / 2 - larm.joint_radius / 2, 0);
    		larm.draw(t);
    
    		glTranslatef(0, 0, larm.length + lforearm.joint_radius / 2);
    		lforearm.draw(t);
    
    		glTranslatef(0, 0, larm.length + lhand.joint_radius / 2);
    		lhand.draw(t);
    	glPopMatrix();
    
    	// Right side  (upper part)
    	glPushMatrix();
            //coloring for debug
            glColor3f(1, 1, 1);
    
    		glTranslatef(-trunk.width / 2 - rarm.joint_radius / 2, trunk.height / 2 - rarm.joint_radius / 2, 0);
            //arms should start in the vertical position, poiting to the floor
            glRotatef(90, 1,0,0);
            apply_state(mode.rightArmMovement(t));
    		larm.draw(t);
    
    		glTranslatef(0, 0, rarm.length + rforearm.joint_radius / 2);
            apply_state(mode.rightForearmMovement(t));
    		lforearm.draw(t);
    
    		glTranslatef(0, 0, rarm.length + rhand.joint_radius / 2);
            apply_state(mode.rightHandMovement(t));
    		lhand.draw(t);
    	    //decoloring for debug
            glColor3f(0.435294f, 0.258824f, 0.258824f);
    	glPopMatrix();
    
    	// Left lower part
    	glPushMatrix();
    		glTranslatef(trunk.width * 0.3 + lthigh.joint_radius / 2, -trunk.height / 2 - lthigh.joint_radius / 2, lthigh.width / 2);
            apply_state(mode.leftThighMovement(t));
    		lthigh.draw(t);
    
    		glTranslatef(0, -lthigh.length / 2 - lleg.radius / 2, lleg.radius);
            apply_state(mode.leftLegMovement(t));
    		lleg.draw(t);
    
    		glTranslatef(0, -lleg.length - lfoot.joint_radius / 2, 0);
            apply_state(mode.leftFootMovement(t));
    		lfoot.draw(t);
    	glPopMatrix();
    
    	// Right lower part
    	glPushMatrix();
    		glTranslatef(-trunk.width * 0.3 + rthigh.joint_radius / 2, -trunk.height / 2 - rthigh.joint_radius / 2, rthigh.width / 2);
            apply_state(mode.rightThighMovement(t));
    		lthigh.draw(t);
    
    		glTranslatef(0, -rthigh.length / 2 - rleg.radius / 2, rleg.radius);
            apply_state(mode.rightLegMovement(t));
    		lleg.draw(t);
    
    		glTranslatef(0, -rleg.length - rfoot.joint_radius / 2, 0);
            apply_state(mode.rightFootMovement(t));
    		lfoot.draw(t);
    	glPopMatrix();

    glPopMatrix();
}

void Trunk::draw(int t) {
	draw_box(width, height, depth);
}

void Head::draw(int t) {
	draw_box(width, height, depth);
}

void Hand::draw(int t){
	// Draw wrist joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0, 0, joint_radius / 2);

	// Draw hand
	draw_box(2,1,3);
}

void Forearm::draw(int t) {
	// Draw elbow joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, 0.0f, joint_radius / 2);

	// Draw forearm
	auto quad = gluNewQuadric();
	gluCylinder(quad, radius, radius * 0.8, length, 30, 30);
	gluDeleteQuadric(quad);
}

void Arm::draw(int t) {
	// Draw shoulder joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, 0.0f, joint_radius / 2);

	// Draw arm
	auto quad = gluNewQuadric();
	gluCylinder(quad, radius, radius, length, 30, 30);
	gluDeleteQuadric(quad);
}

void Thigh::draw(int t) {
	// Draw joint
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0.0f, -length / 2, -width / 2);

	// Draw thigh
	draw_box(width, length, width);
}

void Leg::draw(int t) {
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

void Foot::draw(int t) {
	// Draw ankle
	glutSolidSphere(joint_radius, 30, 30);
	glTranslatef(0, 0, joint_radius / 2);

	// Draw foot
	draw_box(2, 1, 3);
}
