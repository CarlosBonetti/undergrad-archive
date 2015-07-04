#ifndef RUN_H_
#define RUN_H_
#include "movement.h"

namespace movement
{
    /* Running movemement definitions
     * See moonwalk.h for detailed comments
     * */
    constexpr int run_length = 120;

    movement run_body([](int t) -> state{
            t = t % run_length;

            constexpr int movement_duration = run_length * 1.0;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_dy = 1.0;
           constexpr double a = - max_dy / (phase_len * phase_len);
           constexpr double b = 2.0 * max_dy / phase_len;
           double dy = a * t * t + b * t;
           return state(0, 0, 0, 0, dy, 0);
    });

    movement run_right_tigh([] (int t) -> state {
            t = t % run_length;

            constexpr int movement_duration = run_length * 0.5;
            if(t > movement_duration) return state(0,0,0,0,0,0);
    
           constexpr int phase_len = movement_duration/2;
           constexpr double max_theta = 40.0;
           constexpr double a = - max_theta / (phase_len * phase_len);
           constexpr double b = 2.0 * max_theta / phase_len;
           constexpr double c = 0;
           double theta = a * t * t + b * t + c;
           return state(theta, 0, 0, 0, 0, 0);
    });

    typedef BodyMovement<run_body, noMovement, noMovement, noMovement, run_right_tigh> run_t;
    run_t run;
}
#endif
