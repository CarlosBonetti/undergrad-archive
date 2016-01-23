// EPOS Alarm Abstraction Implementation

#include <semaphore.h>
#include <alarm.h>
#include <display.h>

__BEGIN_SYS

// Class attributes
Alarm_Timer * Alarm::_timer;
volatile Alarm::Tick Alarm::_elapsed;
Alarm::Queue Alarm::_request;

// Methods
Alarm::Alarm(const Microsecond & time, Handler * handler, int times)
: _ticks(ticks(time)), _handler(handler), _times(times), _link(this, _ticks)
{
    lock();

    db<Alarm>(TRC) << "Alarm(t=" << time << ",tk=" << _ticks << ",h=" << reinterpret_cast<void *>(handler)
                   << ",x=" << times << ") => " << this << endl;

    if(_ticks) {
        _request.insert(&_link);
        unlock();
    } else {
        unlock();
        (*handler)();
    }
}


Alarm::~Alarm()
{
    lock();

    db<Alarm>(TRC) << "~Alarm(this=" << this << ")" << endl;

    _request.remove(this);

    unlock();
}


// Class methods
void Alarm::delay(const Microsecond & time)
{
    db<Alarm>(TRC) << "Alarm::delay(time=" << time << ")" << endl;

    //Tick t = _elapsed + ticks(time);

    //while(_elapsed < t);

    Semaphore s(0);
    Delay_Handler handler(&s);
    Alarm alarm(time, &handler);
    s.p();
}


void Alarm::handler(const IC::Interrupt_Id & i)
{
    //static Tick next_tick;
    //static Handler * next_handler;

    //lock();

    _elapsed++;

    if(Traits<Alarm>::visible) {
        Display display;
        int lin, col;
        display.position(&lin, &col);
        display.position(0, 79);
        display.putc(_elapsed);
        display.position(lin, col);
    }

    if (!_request.empty()) {
        _request.head()->promote();
        while (_request.head()->rank() <= 0) {
            Queue::Element * e = _request.remove();
            Alarm * alarm = e->object();
            if (alarm->_times != -1)
                alarm->_times--;
            if (alarm->_times) {
                e->rank(alarm->_ticks);
                _request.insert(e);
            }

            db<Alarm>(TRC) << "Alarm::handler(h=" << reinterpret_cast<void *>(alarm->_handler) << ")" << endl;
            (*alarm->_handler)();
        }
    }

    //unlock();
}

__END_SYS
