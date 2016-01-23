// EPOS Thread Abstraction Declarations

#ifndef __thread_h
#define __thread_h

#include <utility/queue.h>
#include <utility/handler.h>
#include <cpu.h>
#include <machine.h>
#include <system.h>
#include <scheduler.h>

extern "C" { void __exit(); }

__BEGIN_SYS

class Thread
{
    friend class Init_First;
    friend class System;
    friend class Scheduler<Thread>;
    friend class Synchronizer_Common;
    friend class Alarm;
    friend class IA32;

protected:
    static const bool smp = Traits<Thread>::smp;
    static const bool preemptive = Traits<Thread>::Criterion::preemptive;
    static const bool partitioned = Traits<Thread>::Criterion::partitioned;
    static const bool reboot = Traits<System>::reboot;
    static const unsigned int ATTEMPTS_BEFORE_MIGRATE = 15;

    static const unsigned int QUANTUM = Traits<Thread>::QUANTUM;
    static const unsigned int STACK_SIZE = Traits<Application>::STACK_SIZE;

    typedef CPU::Log_Addr Log_Addr;
    typedef CPU::Context Context;

public:
    // Thread State
    enum State {
        RUNNING,
        READY,
        SUSPENDED,
        WAITING,
        FINISHING
    };

    // Thread Priority
    typedef Scheduling_Criteria::Priority Priority;

    // Thread Scheduling Criterion
    typedef Traits<Thread>::Criterion Criterion;
    enum {
        HIGH    = Criterion::HIGH,
        NORMAL  = Criterion::NORMAL,
        LOW     = Criterion::LOW,
        MAIN    = Criterion::MAIN,
        IDLE    = Criterion::IDLE
    };

    // Thread Configuration
    struct Configuration {
        Configuration(const State & s = READY, const Criterion & c = NORMAL, unsigned int ss = STACK_SIZE)
        : state(s), criterion(c), stack_size(ss) {}

        State state;
        Criterion criterion;
        unsigned int stack_size;
    };

    // Thread Queue
    typedef Ordered_Queue<Thread, Criterion, Scheduler<Thread>::Element> Queue;

    class Stats {
    public:
        typedef TSC::Time_Stamp Time_Stamp;
        typedef RTC::Microsecond Microsecond;

        enum State {
            RUNNING, WAITING
        };

    public:
        Stats() : _last_deactivation(now()),
                  _last_activation(0),
                  _total_running_time{0},
                  _waiting_mean(0),
                  _running_mean(0),
                  _state(WAITING),
                  _activations(0) {}

        static Microsecond now() {
            return IA32_TSC::time_stamp() * 1000000 / CPU::clock();
        }

        void first_init() {
            _last_deactivation = now();
            _last_activation = _last_deactivation;
            _state = RUNNING;
        }

        void waiting() {
            assert(_state == RUNNING);
            _state = WAITING;
            _last_deactivation = now();
            _running_mean = (_running_mean + running_time()) / 2;
            _total_running_time[Machine::cpu_id()] += _last_deactivation - _last_activation;
        }

        void running() {
            assert(_state == WAITING);
            _state = RUNNING;
            _last_activation = now();
            _waiting_mean = (_waiting_mean + waiting_time()) / 2;
            _activations++;
        }

        static Microsecond ideal_waiting_time() {
            if (Thread::count() == 0)
                return 0;

            float run_proportion = Machine::n_cpus() / Thread::count();
            float wait_proportion = 1 - run_proportion;

            return wait_proportion * QUANTUM;
        }

        unsigned int activations() { return _activations; }

        void reset_activations() { _activations = 0; }

        Microsecond waiting_mean() const { return _waiting_mean; }

        Microsecond running_mean() const { return _running_mean; }

        Microsecond waiting_time() const {
            return _last_activation - _last_deactivation;
        }

        Microsecond running_time() const {
            return now() - _last_activation;
        }

        Microsecond * total_running_time() {
            return _total_running_time;
        }

        Microsecond total_running_time_all() const {
            Microsecond total_time = 0;
            for (unsigned int i = 0; i < Traits<Build>::CPUS; i++) {
                total_time += _total_running_time[i];
            }
            return total_time;
        }

    protected:
        Microsecond _last_deactivation; // last time thread has left the CPU
        Microsecond _last_activation;   // last time thread has obtained the CPU
        Microsecond _total_running_time[Traits<Build>::CPUS];
        Microsecond _waiting_mean;
        Microsecond _running_mean;
        State _state;
        unsigned int _activations;
    };

public:
    template<typename ... Tn>
    Thread(int (* entry)(Tn ...), Tn ... an);
    template<typename ... Tn>
    Thread(const Configuration & conf, int (* entry)(Tn ...), Tn ... an);
    ~Thread();

    const volatile State & state() const { return _state; }

    const volatile Priority & priority() const { return _link.rank(); }
    void priority(const Priority & p);

    Stats::Microsecond * total_running_time() { return _stats.total_running_time(); }
    Stats::Microsecond total_running_time_all() const { return _stats.total_running_time_all(); }
    static unsigned int less_used_cpu();
    void update_priority();
    void migrate();

    int join();
    void pass();
    void suspend() { suspend(false); }
    void resume();

    static unsigned int count() { return _thread_count - Machine::n_cpus(); }

    static Thread * volatile self() { return running(); }
    static void yield();
    static void exit(int status = 0);


    static unsigned int choose_queue() {
        return _scheduler.underloaded_queue();
    }

protected:
    void constructor_prolog(unsigned int stack_size);
    void constructor_epilog(const Log_Addr & entry, unsigned int stack_size);

    static Thread * volatile running() { return _scheduler.chosen(); }

    Queue::Element * link() { return &_link; }

    Criterion & criterion() { return const_cast<Criterion &>(_link.rank()); }

    unsigned int cpu_owner() { return _link.rank().queue(); }

    static void lock(bool disable_int = true) {
        if(disable_int)
            CPU::int_disable();
        if(smp)
            _lock.acquire();
    }

    static void unlock(bool enable_int = true) {
        if(smp)
            _lock.release();
        if(enable_int)
            CPU::int_enable();
    }
    
    static bool locked() { return CPU::int_disabled(); }

    void suspend(bool locked);

    static void sleep(Queue * q);
    static void wakeup(Queue * q);
    static void wakeup_all(Queue * q);

    static void reschedule();
    static void reschedule(unsigned int target_cpu);
    static void time_slicer(const IC::Interrupt_Id & interrupt);

    static void dispatch(Thread * prev, Thread * next, bool charge = true);

    static int idle();

private:
    static void init();

protected:
    char * _stack;
    Context * volatile _context;
    volatile State _state;
    Queue * _waiting;
    Thread * volatile _joining;
    Queue::Element _link;
    Stats _stats;

    static volatile unsigned int _thread_count;
    static Scheduler_Timer * _timer;
    static Scheduler<Thread> _scheduler;
    static Spin _lock;

    static Thread * _idles[Traits<Build>::CPUS];
};


template<typename ... Tn>
inline Thread::Thread(int (* entry)(Tn ...), Tn ... an)
: _state(READY), _waiting(0), _joining(0), _link(this, NORMAL)
{
    constructor_prolog(STACK_SIZE);
    _context = CPU::init_stack(_stack + STACK_SIZE, &__exit, entry, an ...);
    constructor_epilog(entry, STACK_SIZE);
}

template<typename ... Tn>
inline Thread::Thread(const Configuration & conf, int (* entry)(Tn ...), Tn ... an)
: _state(conf.state), _waiting(0), _joining(0), _link(this, conf.criterion)
{
    constructor_prolog(conf.stack_size);
    _context = CPU::init_stack(_stack + conf.stack_size, &__exit, entry, an ...);
    constructor_epilog(entry, conf.stack_size);
}


// An event handler that triggers a thread (see handler.h)
class Thread_Handler : public Handler
{
public:
    Thread_Handler(Thread * h) : _handler(h) {}
    ~Thread_Handler() {}

    void operator()() { _handler->resume(); }

private:
    Thread * _handler;

};

__END_SYS

#endif
