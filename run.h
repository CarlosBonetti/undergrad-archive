#ifndef RUN_H_
#define RUN_H_
#include "movement.h"
#include <iostream>

namespace movement
{
    constexpr int run_length = 100;

    movement run_body([](int t) -> state{
            //length is divided by 2 because we want the up and down movement
            //to happen twice in each cycle, one for each leg
            t = t % (run_length/2);

            constexpr int movement_duration = run_length/2 * 1.0;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_dy = 1.0;
           constexpr double a = - max_dy / (phase_len * phase_len);
           constexpr double b = 2.0 * max_dy / phase_len;
           double dy = a * t * t + b * t;

           return state(0, 0, 0, 0, dy, 0);
    });

    // ===========================================
    // Legs

    movement run_right_thigh([] (int t) -> state {
           t = t % run_length;

           double x = static_cast<double>(t) / static_cast<double>(run_length);
           double theta;
           if(x <= 2.0/6){
                theta = 30 - 180*x;
           }
           else if (x <= 5.0/6){
                theta = 180*x - 90;
           }
           else{
                theta = 210 - 180*x;
           }
           return state(-theta, 0, 0, 0, 0, 0);
    });

    movement run_right_leg([] (int t) -> state {
           t = t % run_length;

           double x = 100 * static_cast<double>(t) / static_cast<double>(run_length);
           double theta;
           if(x <= 35){
                theta = -x*x * 59.0/1001 + x * 1922.0/1001 + 30;
           }
           else if(x <= 95){
                theta = -x*x * 131.0/1800 + x * 422.0/45 - 15413.0/72;
           }
           else{
                theta = 2*x - 170;
           }
           
           return state(theta, 0, 0, 0, 0, 0);
    });

    //left thigh and leg are copies of the right ones but shifted 50% ahead in the cycle.
    movement run_left_thigh([] (int t) -> state {
           t = (t + run_length/2) % run_length;

           double x = static_cast<double>(t) / static_cast<double>(run_length);
           double theta;
           if(x <= 2.0/6){
                theta = 30 - 180*x;
           }
           else if (x <= 5.0/6){
                theta = 180*x - 90;
           }
           else{
                theta = 210 - 180*x;
           }
           return state(-theta, 0, 0, 0, 0, 0);
    });

    movement run_left_leg([] (int t) -> state {
           t = (t + run_length/2) % run_length;

           double x = 100 * static_cast<double>(t) / static_cast<double>(run_length);
           double theta;
           if(x <= 35){
                theta = -x*x * 59.0/1001 + x * 1922.0/1001 + 30;
           }
           else if(x <= 95){
                theta = -x*x * 131.0/1800 + x * 422.0/45 - 15413.0/72;
           }
           else{
                theta = 2*x - 170;
           }
           
           return state(theta, 0, 0, 0, 0, 0);
    });

    // ===========================================
    // Arms

    movement run_right_arm([] (int t) -> state {
    	t = t % run_length;
    	double x = static_cast<double>(t) / static_cast<double>(run_length); // actual fraction of the movement

    	double minTheta = 35;
    	double maxTheta = -20;

    	x *= 2;
    	if (x > 1)
    		x = 2 - x;

    	double theta = minTheta + (maxTheta - minTheta) * x;

    	return state(theta, 0, 0, 0, 0, 0);
    });

    movement run_left_arm([] (int t) -> state {
    	return run_right_arm(t + run_length/2);
    });

    movement run_right_forearm([] (int t) -> state {
    	t = t % run_length;
		double x = static_cast<double>(t) / static_cast<double>(run_length); // actual fraction of the movement

		double minTheta = -30;
		double maxTheta = -90;

    	x *= 2;
    	if (x > 1)
    		x = 2 - x;

    	double theta = minTheta + (maxTheta - minTheta) * x;

		return state(theta, 0, 0, 0, 0, 0);
    });

    movement run_left_forearm([] (int t) -> state {
    	return run_right_forearm(t + run_length/2);
    });

    typedef BodyMovement<run_body, 
    		run_right_arm, run_right_forearm, noMovement,
			run_left_arm, run_left_forearm, noMovement,
            run_right_thigh, run_right_leg, noMovement,
            run_left_thigh, run_left_leg, noMovement> run_t;
    run_t run;
}
#endif
