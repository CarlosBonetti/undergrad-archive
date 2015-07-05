#ifndef MOONWALK_H_
#define MOONWALK_H_
#include "movement.h"
#include <iostream>
using namespace std;

namespace movement{

    /****
     * MOONWALK BOYZ
    */
    
    //number of "ticks" a cycle of the movement will have
    constexpr int mw_length = 160;
   
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

    constexpr int phase1 = mw_length * 2 / 16;
    constexpr int phase2 = mw_length * 4 / 16;
    constexpr int phase3 = mw_length * 8 / 16;
    constexpr int phase4 = mw_length * 12 / 16;

    movement mw_right_thigh([](int t) -> state{
            t = t % mw_length;
            double theta = 0;

            if (t <= phase1){
                theta = linearFit(0, -35, 0, phase1, t);
            }
            else if (t <= phase2){
                theta = -35;
            }
            else if (t <= phase3){
                theta = linearFit(-35, 35, phase2, phase3, t);
            }
            else if (t <= phase4){
                theta = linearFit(35, 20, phase3, phase4, t);
            }
            else{
                theta = linearFit(20, 0, phase4, mw_length, t);
            }
            return state(-theta,0,0,0,0,0);
    });

    movement mw_right_leg([](int t) -> state{
            t = t % mw_length;
            double theta = 0;
            if (t <= phase1 - 0.5*phase1){
                theta = 0;
            }
            else if (t <= phase2){
                theta = linearFit(0, -35, phase1-0.5*phase1, phase2, t);
            }
            else if (t <= phase3){
                theta = linearFit(-35, -70, phase2, phase3, t);
            }
            else if (t <= phase4){
                theta = linearFit(-70, -10, phase3, phase4, t);
            }
            else{
                theta = linearFit(-10, 0, phase4, mw_length, t);
            }
            return state(-theta,0,0,0,0,0);
    });
    
    movement mw_right_foot([](int t) -> state{
            t = t % mw_length;
            double theta = 0;

            if (t <= phase1){
                theta = linearFit(0, 35, 0, phase1, t);
            }
            else if (t <= phase2){
                theta = linearFit(35, -5, phase1, phase2, t);
            }
            else if (t <= phase3){
                theta = linearFit(-5, -25, phase2, phase3, t);
            }
            else if (t <= phase4){
                theta = linearFit(-25, -10, phase3, phase4, t);
            }
            else{
                theta = linearFit(-10, 0, phase4, mw_length, t);
            }
            return state(-theta,0,0,0,0,0);
    });
    
    movement mw_left_thigh([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
          //  return noMovement(t);
            return mw_right_thigh(t);
    });
    movement mw_left_leg([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
           // return noMovement(t);
            return mw_right_leg(t);
    });
    movement mw_left_foot([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
        //    return noMovement(t);
            return mw_right_foot(t);
    });

    movement mw_body([](int t) -> state{
           double speed = .11;
           double dx = speed * t;

            //length is divided by 2 because we want the up and down movement
            //to happen twice in each cycle, one for each leg
            t = t % (mw_length/2);

            constexpr int movement_duration = mw_length/2 * 1.0;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_dy = 1.0;
           constexpr double a = - max_dy / (phase_len * phase_len);
           constexpr double b = 2.0 * max_dy / phase_len;
           double dy = a * t * t + b * t;

           return state(0, 0, 0, 0, 0, -dx);
    });

    typedef BodyMovement< mw_body,
            mw_right_arm, mw_right_forearm, mw_right_hand, 
			noMovement, noMovement, noMovement,
            mw_right_thigh, mw_right_leg, mw_right_foot,
            mw_left_thigh, mw_left_leg, mw_left_foot> moonwalk_t;
    moonwalk_t moonwalk;
}
#endif
