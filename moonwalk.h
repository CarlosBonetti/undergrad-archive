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
    constexpr int mw_length = 140;
   
    //describes the movement of the right arm
    //code of the other body parts will be similar, thus they won't have detailed comments.
    movement mw_right_arm([](int t) -> state{
    	t = t % mw_length;
		double x = static_cast<double>(t) / static_cast<double>(mw_length); // actual fraction of the movement

		double minTheta = 20;
		double maxTheta = -10;

		x *= 2;
		if (x > 1)
			x = 2 - x;

		double theta = minTheta + (maxTheta - minTheta) * x;
		return state(theta, 0, 0, 0, 0, 0);
    });
    
    movement mw_left_arm([](int t) -> state {
    	return mw_right_arm(t + mw_length / 2);
    });

    movement mw_right_forearm([](int t) -> state{
    	t = t % mw_length;
		double x = static_cast<double>(t) / static_cast<double>(mw_length); // actual fraction of the movement

		double minTheta = -10;
		double maxTheta = -60;

		x *= 2;
		if (x > 1)
			x = 2 - x;

		double theta = minTheta + (maxTheta - minTheta) * x;
		return state(theta, 0, 0, 0, 0, 0);
    });

    movement mw_left_forearm([](int t) -> state {
    	return mw_right_forearm(t + mw_length / 2);
    });

    movement mw_right_hand([](int t) -> state {
		t = t % mw_length;
		double x = static_cast<double>(t) / static_cast<double>(mw_length); // actual fraction of the movement

		double minTheta = 0;
		double maxTheta = -30;

		x *= 2;
		if (x > 1)
			x = 2 - x;

		double theta = minTheta + (maxTheta - minTheta) * x;
		return state(theta, 0, 0, 0, 0, 0);
    });

    movement mw_left_hand([](int t) -> state {
    	return mw_right_hand(t + mw_length / 2);
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
            return mw_right_thigh(t);
    });
    movement mw_left_leg([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
            return mw_right_leg(t);
    });
    movement mw_left_foot([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
            return mw_right_foot(t);
    });

    movement mw_body([](int t) -> state{
           double speed = .11;
           double dx = speed * t;

           return state(0, 0, 0, 0, 0, -dx);
    });

    typedef BodyMovement<   mw_body,
            mw_right_arm,   mw_right_forearm, mw_right_hand,
			mw_left_arm,    mw_left_forearm,  mw_left_hand,
            mw_right_thigh, mw_right_leg,     mw_right_foot,
            mw_left_thigh,  mw_left_leg,      mw_left_foot> moonwalk_t;
    moonwalk_t moonwalk;
}
#endif
