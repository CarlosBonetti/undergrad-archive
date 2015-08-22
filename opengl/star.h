#ifndef STAR_H_
#define STAR_H_

class Star {
public:
	Star();
	void draw();
	float x,y,z;
};

Star::Star() {
	x = rand() % 500 - 250;
	y = rand() % 500 - 250;
	z = rand() % 50 + 100;
}

void Star::draw() {
	glPushMatrix();
		glColor3f(.9,.9,.9);
		glTranslatef(x, y, z);
		glutSolidSphere(1, 7, 5);
	glPopMatrix();
}

#endif
