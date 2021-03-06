// EPOS Thread Abstraction Implementation

#include <machine.h>
#include <system.h>
#include <thread.h>
#include <alarm.h> // for FCFS

// This_Thread class attributes
__BEGIN_UTIL
bool This_Thread::_not_booting;
__END_UTIL

__BEGIN_SYS

// Class attributes
volatile unsigned int Thread::_thread_count;
Scheduler_Timer * Thread::_timer;
Scheduler<Thread> Thread::_scheduler;
Spin Thread::_lock;
Thread * Thread::_idles[];

// Methods
void Thread::constructor_prolog(unsigned int stack_size)
{
    lock();

    _thread_count++;
    _scheduler.insert(this);
    if (_link.rank() == IDLE){
    	_idles[Machine::cpu_id()] = this;
    }
    _stack = new (SYSTEM) char[stack_size];
}


void Thread::constructor_epilog(const Log_Addr & entry, unsigned int stack_size)
{
    db<Thread>(TRC) << "Thread(target_cpu=" << cpu_owner()
                    << ",entry=" << entry
                    << ",state=" << _state
                    << ",priority=" << _link.rank()
                    << ",stack={b=" << reinterpret_cast<void *>(_stack)
                    << ",s=" << stack_size
                    << "},context={b=" << _context
                    << "," << *_context << "}) => " << this << endl;

    if((_state != READY) && (_state != RUNNING))
        _scheduler.suspend(this);

    if(preemptive && (_state == READY) && (_link.rank() != IDLE))
        reschedule(cpu_owner());
    else
        if((_state == RUNNING) || (_link.rank() != IDLE)) // Keep interrupts disabled during init_first()
            unlock(false);
        else
            unlock();
}


Thread::~Thread()
{
    lock();

    db<Thread>(TRC) << "~Thread(this=" << this
                    << ",state=" << _state
                    << ",priority=" << _link.rank()
                    << ",stack={b=" << reinterpret_cast<void *>(_stack)
                    << ",context={b=" << _context
                    << "," << *_context << "})" << endl;

    // The running thread cannot delete itself!
    assert(_state != RUNNING);

    switch(_state) {
    case RUNNING:  // For switch completion only: the running thread would have deleted itself! Stack wouldn't have been released!
        exit(-1);
        break;
    case READY:
        _scheduler.remove(this);
        _thread_count--;
        break;
    case SUSPENDED:
        _scheduler.resume(this);
        _scheduler.remove(this);
        _thread_count--;
        break;
    case WAITING:
        _waiting->remove(this);
        _scheduler.resume(this);
        _scheduler.remove(this);
        _thread_count--;
        break;
    case FINISHING: // Already called exit()
        break;
    }

    if(_joining)
        _joining->resume();

    unlock();

    delete _stack;
}


void Thread::priority(const Priority & c)
{
    lock();

    unsigned int prev_owner = cpu_owner();

    _link.rank(Criterion(c));

    if(_state != RUNNING) {
        _scheduler.remove(this);
        _scheduler.insert(this);
    }

    db<Thread>(TRC) << "Thread::priority(this=" << this
        << ",target_cpu=" << cpu_owner() << ")"
        << ",prio=" << c << ")" << endl;

    if(preemptive) {
        reschedule(prev_owner); // implicit unlock()
        if (prev_owner != cpu_owner()) {
            lock();
            reschedule(cpu_owner());
        }
    }
}


int Thread::join()
{
    lock();

    db<Thread>(TRC) << "Thread::join(this=" << this << ",state=" << _state << ")" << endl;

    // Precondition: no Thread::self()->join()
    assert(running() != this);

    // Precondition: a single joiner
    assert(!_joining);

    if(_state != FINISHING) {
        _joining = running();
        _joining->suspend(true);
    } else
        unlock();

    return *reinterpret_cast<int *>(_stack);
}


void Thread::pass()
{
    lock();

    db<Thread>(TRC) << "Thread::pass(this=" << this << ")" << endl;

    Thread * prev = running();
    Thread * next = _scheduler.choose(this);

    if(next)
        dispatch(prev, next, false);
    else {
        db<Thread>(WRN) << "Thread::pass => thread (" << this << ") not ready!" << endl;
        unlock();
    }
}


void Thread::suspend(bool locked)
{
    if(!locked)
        lock();

    db<Thread>(TRC) << "Thread::suspend(this=" << this << ")" << endl;

    Thread * prev = running();

    _scheduler.suspend(this);
    _state = SUSPENDED;

    Thread * next = running();

    dispatch(prev, next);
}


void Thread::resume()
{
    lock();

    db<Thread>(TRC) << "Thread::resume(this=" << this << ")" << endl;

    if(_state == SUSPENDED) {
        _state = READY;
        _scheduler.resume(this);

        if(preemptive)
            reschedule(cpu_owner());
    } else {
        db<Thread>(WRN) << "Resume called for unsuspended object!" << endl;

        unlock();
    }
}


// Class methods
void Thread::yield()
{
    lock();

    db<Thread>(TRC) << "Thread::yield(running=" << running() << ")" << endl;

    Thread * prev = running();
    Thread * next = _scheduler.choose_another();

    dispatch(prev, next);
}


void Thread::exit(int status)
{
    lock();

    db<Thread>(TRC) << "Thread::exit(status=" << status << ") [running=" << running() << "]" << endl;

    Thread * prev = running();
    _scheduler.remove(prev);
    *reinterpret_cast<int *>(prev->_stack) = status;
    prev->_state = FINISHING;

    _thread_count--;

    if(prev->_joining) {
        prev->_joining->_state = READY;
        _scheduler.resume(prev->_joining);
        prev->_joining = 0;
    }

    dispatch(prev, _scheduler.choose());
}


void Thread::sleep(Queue * q)
{
    db<Thread>(TRC) << "Thread::sleep(running=" << running() << ",q=" << q << ")" << endl;

    // lock() must be called before entering this method
    assert(locked());

    Thread * prev = running();
    _scheduler.suspend(prev);
    prev->_state = WAITING;
    q->insert(&prev->_link);
    prev->_waiting = q;

    dispatch(prev, _scheduler.chosen());
}


void Thread::wakeup(Queue * q)
{
    db<Thread>(TRC) << "Thread::wakeup(running=" << running() << ",q=" << q << ")" << endl;

    // lock() must be called before entering this method
    assert(locked());

    if(!q->empty()) {
        Thread * t = q->remove()->object();
        t->_state = READY;
        t->_waiting = 0;
        _scheduler.resume(t);

        if(preemptive)
            reschedule(t->cpu_owner());
    } else
        unlock();
}


void Thread::wakeup_all(Queue * q)
{
    db<Thread>(TRC) << "Thread::wakeup_all(running=" << running() << ",q=" << q << ")" << endl;

    // lock() must be called before entering this method
    assert(locked());

    if(!q->empty())
        while(!q->empty()) {
            Thread * t = q->remove()->object();
            t->_state = READY;
            t->_waiting = 0;
            _scheduler.resume(t);

            if(preemptive) {
                reschedule(t->cpu_owner());
                lock();
            }
         }
    else
        unlock();
}


void Thread::reschedule()
{
    db<Scheduler<Thread> >(TRC) << "Thread::reschedule()" << endl;

    // lock() must be called before entering this method
    assert(locked());

    Thread * prev = running();
    Thread * next = _scheduler.choose();

    dispatch(prev, next);
}


void Thread::reschedule(unsigned int target_cpu) {
    db<Thread>(TRC) << "Thread::reschedule(target_cpu=" << target_cpu << ")" << endl;

    if (target_cpu != Machine::cpu_id()) {
        IC::ipi_send(target_cpu, IC::INT_RESCHEDULER);
        unlock(); // Since reschedule() also has an implicit unlock() ...
    } else
        reschedule();
}


void Thread::time_slicer(const IC::Interrupt_Id & i)
{
    lock();

    reschedule();
}


void Thread::update_priority() {
    if (link()->rank() == IDLE)
        return;

    int new_priority = NORMAL + Stats::ideal_waiting_time() - _stats.waiting_mean();

    assert(new_priority < IDLE);
    assert(new_priority > 0);

    db<Thread>(TRC) << "Thread::update_priority[this=" << this
        << ",old_priority=" << link()->rank()
        << ",new_priority=" << new_priority
        << "]" << endl;

    link()->rank(Criterion(new_priority, cpu_owner()));
}


unsigned int Thread::less_used_cpu() {
    unsigned int cpu = 0;
    Stats::Microsecond max_mean = 0;

    db<Thread>(TRC) << "Thread::less_used_cpu()" << endl;

    for (unsigned int i = 0; i < Traits<Build>::CPUS; i++) {
        Stats::Microsecond mean = _idles[i]->_stats.running_mean();
        db<Thread>(TRC) << "    idle[" << i << "] | running_mean = " << mean << endl;
        if (mean >= max_mean) {
            cpu = i;
            max_mean = mean;
        }
    }

    db<Thread>(TRC) << "    less_used_cpu = [" << cpu << "] | running_mean = " << max_mean << endl;

    return cpu;
}


void Thread::migrate() {
    assert(link()->rank() != IDLE);

    db<Thread>(TRC) << "Thread::migrate(this=" << this
                    << ",prev_cpu=" << cpu_owner() << ")" << endl;

    if (_state == READY)
        _scheduler.remove(this);

    link()->rank(Criterion(link()->rank(), less_used_cpu()));

    if (_state == READY)
        _scheduler.insert(this);

    db<Thread>(TRC) << "Thread::migrate(this=" << this
                    << ",target_cpu=" << this->cpu_owner() << ")" << endl;

    _stats.reset_activations();
}


void Thread::dispatch(Thread * prev, Thread * next, bool charge)
{
    if(charge) {
        if(Criterion::timed) {
            _timer->reset();
        }
    }

    prev->_stats.waiting();
    next->_stats.running();

    prev->update_priority();

    if(prev != next) {
        if(prev->_state == RUNNING)
            prev->_state = READY;
        next->_state = RUNNING;

        next->update_priority();

        if (   prev->link()->rank() != IDLE
            && prev->_stats.activations() >= ATTEMPTS_BEFORE_MIGRATE)
        {
            prev->migrate();
        }

        db<Thread>(TRC) << "Thread::dispatch(prev=" << prev << ",next=" << next << ")" << endl;
        db<Thread>(INF) << "prev={" << prev << ",ctx=" << *prev->_context << "}" << endl;
        db<Thread>(INF) << "next={" << next << ",ctx=" << *next->_context << "}" << endl;

        if(smp)
            _lock.release(); // Note that releasing the lock here, even with interrupts disabled, allows for another CPU to select "prev".
                             // The analysis of whether it could get scheduled by another CPU while its context is being saved by CPU::switch_context()
                             // must focus on the time it takes to save a context and to reschedule a thread. If this gets stringent for a given architecture,
                             // then unlocking must be moved into the mediator. For x86 and ARM it doesn't seam to be the case.

        CPU::switch_context(&prev->_context, next->_context);
    } else {
        if(smp)
            _lock.release();
    }

    CPU::int_enable();
}


int Thread::idle()
{
    while(_thread_count > Machine::n_cpus()) { // someone else besides idles
        if(Traits<Thread>::trace_idle)
            db<Thread>(TRC) << "Thread::idle(CPU=" << Machine::cpu_id() << ",this=" << running() << ")" << endl;

        CPU::int_enable();
        CPU::halt();
    }
    CPU::int_disable();
    if(Machine::cpu_id() == 0) {
        db<Thread>(WRN) << "The last thread has exited!" << endl;
        if(reboot) {
            db<Thread>(WRN) << "Rebooting the machine ..." << endl;
            Machine::reboot();
        } else
            db<Thread>(WRN) << "Halting the machine ..." << endl;
    }
    CPU::halt();

    return 0;
}

__END_SYS

// Id forwarder to the spin lock
__BEGIN_UTIL
unsigned int This_Thread::id()
{
    return _not_booting ? reinterpret_cast<volatile unsigned int>(Thread::self()) : Machine::cpu_id() + 1;
}
__END_UTIL
