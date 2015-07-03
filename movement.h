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
    
/*    typedef std::tuple<double, double, double,
                        double, double, double>
                        state;
*/
    
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
    
    
    
    /****
     * MOONWALK BOYZ
    */
    
    //number of "ticks" a cycle of the movement will have
    constexpr int mw_length = 120;
   
    //describes the movement of the right arm
    //code of the other body parts will be similar, thus they won't have detailed comments.
    movement mw_right_arm([](int t) -> state{
            //make sure time is between 0 and mk_length
            t = t % mw_length;

            //how much of the total duration of a moonwalk cycle is the arm moving?
            // 60%?
            constexpr int movement_duration = mw_length * 0.6;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           //arm movement is like a pendulum: it rotates to max_theta and then goes back to 0.
           //in this case we are simulating a parabola with points
           //(0,0)  (len, max_theta) (2*len, 0)
           constexpr int phase_len = movement_duration/2;
           constexpr double max_theta = -30.0;
           constexpr double a = - max_theta / (phase_len * phase_len);
           constexpr double b = 2.0 * max_theta / phase_len;
           double theta = a * t * t + b * t;
           return state(theta, 0, 0, 0, 0, 0);
    });
    
    movement mw_right_forearm([](int t) -> state{
            t = t % mw_length;

            constexpr int movement_duration = mw_length * 0.8;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_theta = -70.0;
           constexpr double a = - max_theta / (phase_len * phase_len);
           constexpr double b = 2.0 * max_theta / phase_len;
           double theta = a * t * t + b * t;
           return state(theta, 0, 0, 0, 0, 0);
    });

    movement mw_right_hand([](int t) -> state{
            t = t % mw_length;

            constexpr int movement_duration = mw_length * 0.9;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_theta = -80.0;
           constexpr double a = - max_theta / (phase_len * phase_len);
           constexpr double b = 2.0 * max_theta / phase_len;
           double theta = a * t * t + b * t;
           return state(theta, 0, 0, 0, 0, 0);
    });

    BodyMovement<mw_right_arm, mw_right_forearm, mw_right_hand> moonwalk;
}
#endif
