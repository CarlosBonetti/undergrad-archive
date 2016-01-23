// EPOS Handler Utility Declarations

#ifndef __handler_h
#define __handler_h

#include <system/config.h>

__BEGIN_UTIL

class Handler {
public:
    typedef void (Function)();

public:
    Handler() {}
    virtual ~Handler() {}

    virtual void operator()() = 0;
};

class Function_Handler : public Handler {
public:
    Function_Handler(Function * f) : _f(f) {}

    void operator()() {
        _f();
    }

protected:
    Function * _f;
};

__END_UTIL

#endif
