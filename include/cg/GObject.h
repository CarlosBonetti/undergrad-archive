#ifndef CG_GOBJECT_H_
#define CG_GOBJECT_H_

#include <vector>
#include <string>
#include <map>
#include "cg/Transformations.h"

namespace CG {
	class Transformation;
	class Coordinate {
		public:
			Coordinate(double dx, double dy) : x(dx), y(dy), w(1) {}
			double x, y, w;

			Coordinate& operator*=(const Transformation& t);
			friend Coordinate operator+(const Coordinate &c1, const Coordinate &c2);
			friend Coordinate operator-(const Coordinate &c1, const Coordinate &c2);
	};

	class Color {
		public:
			Color() : r(0), g(0), b(0) {}
			Color(double r, double g, double b) : r(r), g(g), b(b) {}
			double r, g, b;
	};

	class GObject {
		public:
			typedef std::vector<Coordinate> Coordinates;
			enum Type { OBJECT, POINT, LINE, POLYGON };

			static const std::string TypeNames[];

			GObject(Type t = Type::OBJECT) : _type(t) {}
			Type type() const { return _type; }

			const Coordinates& coordinates() const {return _coordinates;}
			int numPoints() const {return _coordinates.size();}
			Coordinate center() const;

			void transform(const Transformation& t);
			void clear() { _coordinates.clear(); }

			Color color;

		protected:
			Type _type;

			void addCoordinate(int x, int y) {_coordinates.emplace_back(x,y);}
			void addCoordinate(const Coordinate& p) {_coordinates.push_back(p);}
			void addCoordinate(Coordinate&& p) {_coordinates.push_back(p);}
			void addCoordinate(const Coordinates& coords){_coordinates.insert(_coordinates.end(), coords.begin(), coords.end());}
		private:
			Coordinates _coordinates;
	};

	class GPoint : public GObject {
		public:
			GPoint(int x, int y) : GObject(Type::POINT) {
				addCoordinate(x,y);
			}
			GPoint(const Coordinate& p) : GObject(Type::POINT) {
				addCoordinate(p);
			}
			GPoint(Coordinate&& p) : GObject(Type::POINT) {
				addCoordinate(p);
			}
	};

	class GLine : public GObject {
		public:
			GLine(int x1, int y1, int x2, int y2) : GObject(Type::LINE) {
				addCoordinate(x1,y1);
				addCoordinate(x2,y2);
			}
			GLine(const Coordinate& p1, const Coordinate& p2) : GObject(Type::LINE) {
				addCoordinate(p1);
				addCoordinate(p2);
			}
			GLine(Coordinate&& p1, Coordinate&& p2) : GObject(Type::LINE) {
				addCoordinate(p1);
				addCoordinate(p2);
			}
	};

	class GPolygon : public GObject {
		public:
			GPolygon(const Coordinates& coords) : GObject(Type::POLYGON) {
				addCoordinate(coords);
			}
	};

}

#endif
