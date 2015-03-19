#ifndef __VIEWPORT
#define __VIEWPORT

#include <gtkmm/drawingarea.h>
#include "Window.h"
#include "DisplayFile.h"
#include "GObject.h"

class Viewport : public Gtk::DrawingArea {
	private:
		virtual bool on_draw(const Cairo::RefPtr<Cairo::Context>& ctx);
		void drawObject(const CG::GObject &obj, const Cairo::RefPtr<Cairo::Context>& ctx);

		int transformX(int x);
		int transformY(int y);

		CG::Window* _window;
		CG::DisplayFile* _displayFile;

	public:
		Viewport(CG::Window* window, CG::DisplayFile* dfile);

		void redraw() { queue_draw(); }

		//Window methods
		void zoomIn()  { _window->zoom(1.0);  redraw(); }
		void zoomOut() { _window->zoom(-1.0); redraw(); }
		void left()    { _window->dx(-1); redraw(); }
		void right()   { _window->dx(1);  redraw(); }
		void up()      { _window->dy(1);  redraw(); }
		void down()    { _window->dy(-1); redraw(); }

		void addObject(std::string name, CG::GObject object) { _displayFile->add(name, object); }
};

#endif
