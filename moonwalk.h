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

    movement mw_right_thigh([](int t) -> state{
            t = t % mw_length;
            int firstHalf = (mw_length)/2;
            
            double theta = 35;

            if(t <= firstHalf){
               double firstQuarter = firstHalf / 2;
               if(t <= firstQuarter){
                   theta = 35;
               }
               else{
                   theta = linearFit(35, 0,firstQuarter, firstHalf, t);
               }
            }
            else{
               int remaining = mw_length - firstHalf;
               int firstThird = firstHalf + remaining / 3;

               if(t <= firstThird){
                   theta = linearFit(0, -15, firstHalf, firstThird, t);
               }
               else{
                   theta = linearFit(-15, 35, firstThird, mw_length, t);
               }
            }

            return state(-theta,0,0,0,0,0);
    });
    
    movement mw_left_thigh([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
            return mw_right_thigh(t);
    });

    movement mw_right_leg([](int t) -> state{
            t = t % mw_length;
            int firstHalf = (mw_length)/2;
            
            double theta = -72;

            if(t <= firstHalf){
               double firstQuarter = firstHalf / 2;
               if(t <= firstQuarter){
                   theta = linearFit(-72, 0, 0, firstQuarter, t);
               }
               else{
                   theta = 0;
               }
            }
            else{
               int remaining = mw_length - firstHalf;
               int firstThird = firstHalf + remaining / 3;

               if(t <= firstThird){
                   theta = linearFit(0, -72, firstHalf, firstThird, t);
               }
               else{
                   theta = linearFit(-72, -72, firstThird, mw_length, t);
               }
            }

            return state(-theta,0,0,0,0,0);
    });
    
    movement mw_left_leg([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
            return mw_right_leg(t);
    });

    movement mw_right_foot([](int t) -> state{
            t = t % mw_length;
            int firstHalf = (mw_length)/2;
            
            double theta = -35;

            if(t <= firstHalf){
               double firstQuarter = firstHalf / 2;
               if(t <= 18){
                   theta = linearFit(-35, -12.5, 0, 18,t);
                   std::cout << t << ", " << theta << endl;
               }
               else if(t <= firstQuarter){
                   theta = linearFit(-12.5, -30,18, firstQuarter,t);
                   std::cout << t << ",2, " << theta << endl;
               }
               else{
                   theta = linearFit(-30, 0, firstQuarter, firstHalf, t);
                   cout << "ja deveria estar com pe no chao\n";
               }
            }
            else{
                cout << "passei da metade" << endl;
                theta = 0;
            }
           
            return state(-theta,0,0,0,0,0);
    });
    movement mw_left_foot([](int t) -> state{
            t = (t + mw_length/2) % mw_length;
            return noMovement(t);
//            return mw_right_foot(t);
    });



    typedef BodyMovement<noMovement,
            mw_right_arm, mw_right_forearm, mw_right_hand, 
			noMovement, noMovement, noMovement,
            mw_right_thigh, mw_right_leg, mw_right_foot,
            mw_left_thigh, mw_left_leg, mw_left_foot> moonwalk_t;
    moonwalk_t moonwalk;
}
#endif
