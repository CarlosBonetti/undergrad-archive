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
    
    template<movement& _rightArmMovement, movement& _rightForearmMovement, movement& _rightHandMovement>
    class BodyMovement{
        public:
            const movement& rightArmMovement = _rightArmMovement;
            const movement& rightForearmMovement = _rightForearmMovement;
            const movement& rightHandMovement = _rightHandMovement;
    };
    
    
}
#endif
