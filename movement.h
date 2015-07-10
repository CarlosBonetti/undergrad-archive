#ifndef MOVEMENT_H_
#define MOVEMENT_H_

#include <functional>
#include <tuple>

constexpr int FPS = 120;

namespace movement{ 
    
    /* A rotation of a body part is a function like
     * double legRotation(int time);
     */
    
    
    struct state{
        state(){}
        state(double a, double b, double c, double d, double e, double f) : rx(a), ry(b), rz(c), tx(d), ty(e), tz(f) {}
        double rx=0, ry=0, rz=0;
        double tx=0, ty=0, tz=0;
    };
    
    typedef std::function<state(int)> movement;
    
    movement noMovement ([](int t) -> state {
            return state();
    });
    
    template<movement& _bodyMovement, 
        movement& _rightArmMovement, movement& _rightForearmMovement, movement& _rightHandMovement,
		movement& _leftArmMovement, movement& _leftForearmMovement, movement& _leftHandMovement,
        movement& _rightThighMovement, movement& _rightLegMovement, movement& _rightFootMovement,
        movement& _leftThighMovement, movement& _leftLegMovement, movement& _leftFootMovement>
    class BodyMovement{
        public:
            const movement& bodyMovement = _bodyMovement;

            const movement& rightArmMovement = _rightArmMovement;
            const movement& rightForearmMovement = _rightForearmMovement;
            const movement& rightHandMovement = _rightHandMovement;

            const movement& leftArmMovement = _leftArmMovement;
            const movement& leftForearmMovement = _leftForearmMovement;
            const movement& leftHandMovement = _leftHandMovement;

            const movement& rightThighMovement = _rightThighMovement;
            const movement& rightLegMovement = _rightLegMovement;
            const movement& rightFootMovement = _rightFootMovement;
            
            const movement& leftThighMovement = _leftThighMovement;
            const movement& leftLegMovement = _leftLegMovement;
            const movement& leftFootMovement = _leftFootMovement;
    };

    inline double linearFit(double y0, double yF, double x0, double xF, double t){
        return y0 + (t - x0) * (yF - y0) / (xF - x0);
    }

    //does not work
    inline double quadraticFit(double y0, double yF, double x0, double xF, double t, double g = -.2){
       double b = ((y0 - yF) - g * (x0*x0 - xF*xF)) / (x0 - xF);
       double c = y0 - g * x0*x0 - b * x0;
       return g * t*t + b * t + c;
    }
}
#endif
