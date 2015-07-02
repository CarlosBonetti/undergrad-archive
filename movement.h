#ifndef MOVEMENT_H_
#define MOVEMENT_H_

#include <functional>
#include <tuple>

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
    
/*    typedef std::tuple<double, double, double,
                        double, double, double>
                        state;
*/
    
    typedef std::function<state(int)> movement;
    
    movement noMovement ([](int t) -> state {
            return state();
    });
    
    template<movement& _rightArmMovement>
    class BodyMovement{
        public:
            const movement& rightArmMovement = _rightArmMovement;
    };
    
    
    
    /****
     * MOONWALK BOYZ
    */
    
    //number of "ticks" a cycle of the movement will have
    constexpr int mk_length = 24;
    
    movement mw_right_arm([](int t) -> state{
            t = t % 24;
    
           //arm movement is like a pendulum: it rotates and then goes back
           constexpr int num_phases = 2;
           constexpr int phase_len = mk_length/2;
           constexpr double max_theta = 30; 
    
           int phase = t > 12? 1 : 2;
           switch(phase){
                //rotating forwards: 0 to 30 degress
                case 1: {
                   return state(t*max_theta/phase_len,0,0,0,0,0);
                }
                //rotating backwards: 30 to 0 degrees
                case 2:{
                    return state(max_theta - t*max_theta/phase_len,0,0,0,0,0);
                }
           }
     
    
    });
    
    
    BodyMovement<mw_right_arm> moonwalk;
}
#endif
