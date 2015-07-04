#ifndef MOONWALK_H_
#define MOONWALK_H_
#include "movement.h"
namespace movement{

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

    typedef BodyMovement<mw_right_arm, mw_right_forearm, mw_right_hand> moonwalk_t;
    moonwalk_t moonwalk;
}
#endif
