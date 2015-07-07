#ifndef FLOOR_H_
#define FLOOR_H_

class Floor {
public:
	void draw();

	float tile_size = 10.0f;
	int max_tiles = 10;
};

void Floor::draw() {
	for (int i = 0; i < max_tiles; i++) {
		for (int j = 0; j < max_tiles; j++) {
			glBegin(GL_QUADS);
				glColor3f(0.1 * j, 0.1 * i, 0.1 * i);
				glVertex3f(0, 0, 0);
				glVertex3f(0, 0, tile_size);
				glVertex3f(tile_size, 0.0f, tile_size);
				glVertex3f(tile_size, 0.0f, 0);
			glEnd();
			glTranslatef(0, 0, tile_size);
		}
		glTranslatef(tile_size, 0, -tile_size * max_tiles);
	}
}

#endif
