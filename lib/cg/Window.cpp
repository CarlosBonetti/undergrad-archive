#include "cg/Window.h"
#include <ctgmath>

#include <iostream>

namespace CG {

Window:: Window(double cx, double cy, double width, double height, double theta)
        : _center(cx, cy), _width(width), _height(height), _theta(theta) {
    updateMatrix();
}

bool Window::zoom(double step) {
    // Maximum zoom reached
    if (_width + step <= 0 ||  _height + step <= 0)
        return false;

    _width += step;
    _height += step;
    return true;
}

void Window::move(double dx, double dy) {
    Coordinate c(dx, dy);
    c *= Transformation::newRotationAroundOrigin(-_theta);
    _center.x -= c.x;
    _center.y -= c.y;
}

void Window::updateMatrix() {
    _wo2wiMatrix = Transformation();
    _wo2wiMatrix *= Transformation::newTranslation(-_center.x, -_center.y);
    _wo2wiMatrix *= Transformation::newRotationAroundOrigin(_theta);
    _wo2wiMatrix *= Transformation::newScaling(1.0/_width, 1.0/_height);
}

}
