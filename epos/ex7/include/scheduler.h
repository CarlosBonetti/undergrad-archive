// EPOS Scheduler Declaration

#ifndef __scheduler_h
#define __scheduler_h

#include <system/config.h>
#include <utility/list.h>

__BEGIN_SYS

/**
 * Typename T must implement T::link() and have a T::Criterion type wich implements de operator int()
 */
template<typename T>
class Scheduler
{
protected:
    Scheduling_List<T> _list;

public:
    typedef typename Scheduling_List<T>::Element Element;

public:
    Scheduler() {
        db<Scheduler>(TRC) << "Scheduler()" << endl;
    }

    void insert(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::insert(" << obj << ")" << endl;
        _list.insert(obj->link());
    }

    T * remove(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::remove(" << obj << ")" << endl;
        return _list.remove(obj->link()) ? obj : 0;
    }

    T * suspend(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::suspend(" << obj << ")" << endl;
        return _list.remove(obj->link()) ? obj : 0;
    }

    void resume(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::resume(" << obj << ")" << endl;
        _list.insert(obj->link());
    }

    T  * chosen() {
        return _list.chosen()->object();
    }

    T * choose() {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << ",queue size=" << _list.size()
            << "]::choose() ..." << endl;

        T * chosen = _list.choose()->object();

        db<Scheduler>(TRC) << "    Scheduler new chosen => " << chosen << endl;

        return chosen;
    }

    T * choose(T * obj) {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << ",queue size=" << _list.size()
            << "]::choose(obj=" << obj << ") => ";

        T * chosen = _list.choose(obj->link())->object();

        db<Scheduler>(TRC) << "    Scheduler new chosen => " << chosen << endl;

        return chosen;
    }

    T * choose_another() {
        db<Scheduler>(TRC) << "Scheduler[chosen=" << chosen() << "]::choose_another() => ";

        T * chosen = _list.choose_another()->object();

        db<Scheduler>(TRC) << "    Scheduler new chosen => " << chosen << endl;

        return chosen;
    }
};

__END_SYS

#endif
