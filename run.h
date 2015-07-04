#ifndef RUN_H_
#define RUN_H_
#include "movement.h"

namespace movement{

    typedef BodyMovement<noMovement, noMovement, noMovement> run_t;
    run_t run;
}
#endif
