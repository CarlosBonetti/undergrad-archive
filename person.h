#ifndef PERSON_H_
#define PERSON_H_

#include <GL/glut.h>

class Trunk {
public:
	void draw(int t);

	double width = 7;
	double height = 12;
	double depth = 3;
};

class Head {
public:
	void draw(int t);

	double width = 4;
	double height = 4;
	double depth = 3;
};

/**
 * Hand + wrist joint
 */
class Hand {
public:
	void draw(int t);

	double joint_radius = 0.8;
};

/**
 * Forearm + elbow joint
 */
class Forearm {
public:
	void draw(int t);

	double length = 3.6;
	double joint_radius = 1;
	double radius = 1;
};

/**
 * Arm + shoulder joint
 */
class Arm {
public:
	void draw(int t);

	double length = 3.6;
	double joint_radius = 1.2;
	double radius = 1.2;
};

/**
 * Thigh + joint
 */
class Thigh {
public:
	void draw(int t);

	double length = 5;
	double width = 1.4;
	double joint_radius = .8;
};

/**
 * Lower leg = Knee
 */
class Leg {
public:
	void draw(int t);

	double length = 4;
	double radius = 1;
	double joint_radius = .6;
};

/**
 * Foot = ankle
 */
class Foot {
public:
	void draw(int t);

	double joint_radius = .5;
};

class vector {
public:
	GLfloat x = 0;
	GLfloat y = 0;
	GLfloat z = 0;
};

class Person {
public:
    enum walkingMode{MOONWALKING, RUNNING};

    Person();
    void setWalkingMode(walkingMode m) {currentMode = m;}
	void draw(int t);
	void update(float velocity);

	vector position;
	GLfloat rotation = 0;

private:
    walkingMode currentMode = RUNNING;
   
    template<typename T, T& mode> void draw_aux(int t);

	Trunk trunk;
	Head head;
	Hand lhand, rhand;
	Forearm lforearm, rforearm;
	Arm larm, rarm;
	Thigh lthigh, rthigh;
	Leg lleg, rleg;
	Foot lfoot, rfoot;
};

#endif
