// EPOS Thread Abstraction Initialization

#include <system/kmalloc.h>
#include <system.h>
#include <thread.h>
#include <alarm.h>

__BEGIN_SYS

void Thread::init()
{
    // The installation of the scheduler timer handler must precede the
    // creation of threads, since the constructor can induce a reschedule
    // and this in turn can call timer->reset()
    // Letting reschedule() happen during thread creation is harmless, since
    // MAIN is created first and dispatch won't replace it nor by itself
    // neither by IDLE (which has a lower priority)

    db<Init, Thread>(TRC) << "Thread::init()" << endl;

    if(preemptive) {
        _timer = new (kmalloc(sizeof(Scheduler_Timer))) Scheduler_Timer(QUANTUM, time_slicer);
        _timer->disable();
    }
}


void Thread::init_idle()
{
    db<Init, Thread>(TRC) << "Thread::init_idle()" << endl;
    new (kmalloc(sizeof(Thread))) Thread(Configuration(READY, IDLE), idle);
}

__END_SYS
