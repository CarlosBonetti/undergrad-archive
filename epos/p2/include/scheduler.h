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

    public:
        FCFS(int p = NORMAL); // Defined at Alarm
    };
}


// Scheduling_Queue
template<typename T, typename R = typename T::Criterion>
class Scheduling_Queue: public Scheduling_List<T> {};

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
    	// If called before insert(), chosen will dereference a null pointer!
    	// For threads, we this won't happen (see Thread::init()).
    	// But if you are unsure about your new use of the scheduler,
    	// please, pay the price of the extra "if" bellow.
//    	return const_cast<T * volatile>((Base::chosen()) ? Base::chosen()->object() : 0);
    	return const_cast<T * volatile>(Base::chosen()->object());
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

        T * obj = Base::choose()->object();

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

// Multiple Scheduling_Queue
template<typename T, unsigned int Size = Traits<Build>::CPUS, typename R = typename T::Criterion>
class Multiple_Scheduling_Queue: public Multiple_Scheduling_List<T, Size> {};

// Multiple Scheduler
// Objects subject to scheduling by Scheduler must declare a type "Criterion"
// that will be used as the scheduling queue sorting criterion (viz, through
// operators <, >, and ==) and must also define a method "link" to export the
// list element pointing to the object being handled.
template<typename T, unsigned int Size = Traits<Build>::CPUS>
class Multiple_Scheduler: public Multiple_Scheduling_Queue<T, Size>
{
private:
    typedef Multiple_Scheduling_Queue<T, Size> Base;

public:
    typedef typename T::Criterion Criterion;
    typedef Multiple_Scheduling_List<T, Size, Criterion> Queue;
    typedef typename Queue::Element Element;

public:
    Multiple_Scheduler() {}

    unsigned int schedulables() { return Base::size(); }

    T * volatile chosen(unsigned int consumer_id = Machine::cpu_id()) {
        // If called before insert(), chosen will dereference a null pointer!
        // For threads, we this won't happen (see Thread::init()).
        // But if you are unsure about your new use of the scheduler,
        // please, pay the price of the extra "if" bellow.
//      return const_cast<T * volatile>((Base::chosen()) ? Base::chosen()->object() : 0);
        return const_cast<T * volatile>(Base::chosen(consumer_id)->object());
    }

    void insert(T * obj, unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::insert(" << obj << ")" << endl;

        Base::insert(obj->link(), consumer_id);
    }

    T * remove(T * obj, unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::remove(" << obj << ")" << endl;

        return Base::remove(obj->link(), consumer_id) ? obj : 0;
    }

    void suspend(T * obj, unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::suspend(" << obj << ")" << endl;

        Base::remove(obj->link(), consumer_id);
    }

    void resume(T * obj, unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::resume(" << obj << ")" << endl;

        Base::insert(obj->link(), consumer_id);
    }

    T * choose(unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::choose() => ";

        T * obj = Base::choose(consumer_id)->object();

        db<Multiple_Scheduler>(TRC) << obj << endl;

        return obj;
    }

    T * choose_another(unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::choose_another() => ";

        T * obj = Base::choose_another(consumer_id)->object();

        db<Multiple_Scheduler>(TRC) << obj << endl;

        return obj;
    }

    T * choose(T * obj, unsigned int consumer_id = Machine::cpu_id()) {
        db<Multiple_Scheduler>(TRC) << "Multiple_Scheduler[chosen=" << chosen(consumer_id) << "]::choose(" << obj;

        if(!Base::choose(obj->link(), consumer_id))
            obj = 0;

        db<Multiple_Scheduler>(TRC) << obj << endl;

        return obj;
    }
};

__END_SYS

#endif
