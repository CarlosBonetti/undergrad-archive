// EPOS Scheduler Abstraction Declarations

#ifndef __scheduler_h
#define __scheduler_h

#include <utility/list.h>
#include <cpu.h>
#include <machine.h>

__BEGIN_SYS

// All scheduling criteria, or disciplines, must define operator int() with
// the semantics of returning the desired order of a given object within the
// scheduling list
namespace Scheduling_Criteria
{
    // Priority (static and dynamic)
    class Priority
    {
    public:
        enum {
            MAIN   = 0,
            HIGH   = 1,
            NORMAL = (unsigned(1) << (sizeof(int) * 8 - 1)) - 3,
            LOW    = (unsigned(1) << (sizeof(int) * 8 - 1)) - 2,
            IDLE   = (unsigned(1) << (sizeof(int) * 8 - 1)) - 1
        };

        static const bool timed = false;
        static const bool dynamic = false;
        static const bool preemptive = true;
        static const bool partitioned = false;

    public:
        Priority(int p = NORMAL): _priority(p) {}

        operator const volatile int() const volatile { return _priority; }

        void update() {}

    protected:
        volatile int _priority;
    };

    // Round-Robin
    class RR: public Priority
    {
    public:
        enum {
            MAIN   = 0,
            NORMAL = 1,
            IDLE   = (unsigned(1) << (sizeof(int) * 8 - 1)) - 1
        };

        static const bool timed = true;
        static const bool dynamic = false;
        static const bool preemptive = true;
        static const bool partitioned = false;

    public:
        RR(int p = NORMAL): Priority(p) {}
    };

    // First-Come, First-Served (FIFO)
    class FCFS: public Priority
    {
    public:
        enum {
            MAIN   = 0,
            NORMAL = 1,
            IDLE   = (unsigned(1) << (sizeof(int) * 8 - 1)) - 1
        };

        static const bool timed = false;
        static const bool dynamic = false;
        static const bool preemptive = false;
        static const bool partitioned = false;

    public:
        FCFS(int p = NORMAL); // Defined at Alarm
    };
    
    // Global Round-Robin
    class GRR: public RR
    {
    public:
        static const unsigned int HEADS = Traits<Machine>::CPUS;

    public:
        GRR(int p = NORMAL): RR(p) {}

        static unsigned int current_head() { return Machine::cpu_id(); }
    };

    // Partitioned Round-Robin
    // The typename T must have a class method choose_queue() to
    // designate the queue id for new criterion objects
    template<typename T>
    class PRR: public RR
    {
    public:
        static const unsigned int QUEUES = Traits<Machine>::CPUS;
        static const bool partitioned = true;

    public:
        PRR(int p = NORMAL): RR(p) {
            if (_priority == IDLE || _priority == MAIN)
                _queue = Machine::cpu_id();
            else
                _queue = T::choose_queue();
        }

        static unsigned int current_queue() { return Machine::cpu_id(); }
        unsigned int queue() const { return _queue; }

    protected:
        unsigned int _queue;
    };

    // Completely Fair Scheduler
    // The typename T must have a class method choose_queue() to
    // designate the queue id for new criterion objects
    template<typename T>
    class CFS: public RR
    {
    public:
        enum {
            IDLE   = (unsigned(1) << (sizeof(int) * 8 - 1)) - 1,
            MAIN   = IDLE / 2 - 1,
            HIGH   = IDLE / 2,
            NORMAL = IDLE / 2,
            LOW    = IDLE / 2
        };

        static const unsigned int QUEUES = Traits<Machine>::CPUS;
        static const bool partitioned = true;

        static unsigned int current_queue() { return Machine::cpu_id(); }
        unsigned int queue() const { return _queue; }

    public:
        CFS(int p = NORMAL): RR(p), _changes(0) {
            if (_priority == IDLE || _priority == MAIN)
                _queue = Machine::cpu_id();
            else
                _queue = T::choose_queue();
        }

        CFS(int p, unsigned int target_queue): RR(p), _changes(0) {
            _queue = target_queue;
        }

    protected:
        unsigned int _queue;
        unsigned int _changes;
    };
}


// Scheduling_Queue
template<typename T, typename R = typename T::Criterion>
class Scheduling_Queue: public Scheduling_List<T> {};

template<typename T>
class Scheduling_Queue<T, Scheduling_Criteria::GRR>:
public Multihead_Scheduling_List<T> {};

template<typename T>
class Scheduling_Queue<T, Scheduling_Criteria::PRR<T>>:
public Scheduling_Multilist<T> {};

template<typename T>
class Scheduling_Queue<T, Scheduling_Criteria::CFS<T>>:
public Scheduling_Multilist<T> {};

// Scheduler
// Objects subject to scheduling by Scheduler must declare a type "Criterion"
// that will be used as the scheduling queue sorting criterion (viz, through
// operators <, >, and ==) and must also define a method "link" to export the
// list element pointing to the object being handled.
template<typename T>
class Scheduler: public Scheduling_Queue<T>
{
private:
    typedef Scheduling_Queue<T> Base;

public:
    typedef typename T::Criterion Criterion;
    typedef Scheduling_List<T, Criterion> Queue;
    typedef typename Queue::Element Element;

public:
    Scheduler() {}

    unsigned int schedulables() { return Base::size(); }

    T * volatile chosen() {
    	return const_cast<T * volatile>((Base::chosen()) ? Base::chosen()->object() : 0);
    }

    void insert(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::insert(" << obj << ")" << endl;

        Base::insert(obj->link());
    }

    T * remove(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::remove(" << obj << ")" << endl;

        return Base::remove(obj->link()) ? obj : 0;
    }

    void suspend(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::suspend(" << obj << ")" << endl;

        Base::remove(obj->link());
    }

    void resume(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::resume(" << obj << ")" << endl;

        Base::insert(obj->link());
    }

    T * choose() {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::choose() => ";

        T * obj = Base::chosen() ? Base::choose()->object() : 0;

        db<Scheduler>(TRC) << obj << endl;

        return obj;
    }

    T * choose_another() {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::choose_another() => ";

        T * obj = Base::choose_another()->object();

        db<Scheduler>(TRC) << obj << endl;

        return obj;
    }

    T * choose(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::choose(" << obj;

        if(!Base::choose(obj->link()))
            obj = 0;

        db<Scheduler>(TRC) << obj << endl;

        return obj;
    }
};

__END_SYS

#endif
