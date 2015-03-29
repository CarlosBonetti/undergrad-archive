#include "cg/Viewport.h"

#include <cassert>

namespace CG {

 Coordinate Viewport::transformCoordinate(const Coordinate& c) const {
   int width = getWidth();
   int height = getHeight();
   double x = width * (c.x - _window.xmin()) / (_window.xmax() - _window.xmin());
   double y = height * (1 - ((c.y - _window.ymin()) / (_window.ymax() - _window.ymin())));
   return Coordinate(x, y);
 }

 GObject::Coordinates Viewport::transformCoordinates(const GObject::Coordinates& coords) const {
   CG::GObject::Coordinates vwCoordinates;
   for (const auto &c : coords)
     vwCoordinates.push_back(transformCoordinate(c));

   return vwCoordinates;
 }

	void Viewport::changeWindowZoom(double step){
		if(_window.zoom(step)){
			_window.updateMatrix();
			transformAndClipAll(_window.wo2wiMatrix());
			redraw();
		}
	}

	void Viewport::changeWindowPosition(double sx, double sy){
		_window.move(sx, sy);
		_window.updateMatrix();
		transformAndClipAll(_window.wo2wiMatrix());
		redraw();
	}

	void Viewport::rotateWindow(double theta){
		_window.rotate(Transformation::toRadians(theta));
		_window.updateMatrix();
		transformAndClipAll(_window.wo2wiMatrix());
		redraw();
	}

  void Viewport::onObjectCreation(const std::string& name, const GObject& object) {
    assert(!_windowObjects.exists(name));
    auto &windowObj = _windowObjects.add(name, object);
    transformAndClip(windowObj, _window.wo2wiMatrix());
    redraw();
  }

  void Viewport::onObjectChange(const std::string& name, const GObject& object) {
    auto itWindow = _windowObjects.findObject(name);
  	assert(_windowObjects.isValidIterator(itWindow));
  	auto &windowObj = itWindow->second;
    windowObj = object;
    transformAndClip(windowObj, _window.wo2wiMatrix());
    redraw();
  }

  void Viewport::onObjectRemoval(const std::string& name) {
    assert(_windowObjects.exists(name));
    _windowObjects.remove(name);
    redraw();
  }

  void Viewport::transformAndClip(GObject &obj, const Transformation &t){
	  obj.transform(t);
	  switch(obj.type()){
	  	  case GObject::Type::POINT:
	  		  _clippingStrategy.clip(static_cast<GPoint&> (obj)); break;
			case GObject::Type::LINE:
				_clippingStrategy.clip(static_cast<GLine&> (obj)); break;
			case GObject::Type::POLYGON:
				_clippingStrategy.clip(static_cast<GPolygon&> (obj)); break;
			case GObject::Type::OBJECT:
				break;
	  }
  }

	void Viewport::transformAndClipAll(const Transformation &t){
		_windowObjects.objects() = _world->getObjects();
		for(auto &obj : _windowObjects.objects())
			transformAndClip(obj.second, t);
	}

}
