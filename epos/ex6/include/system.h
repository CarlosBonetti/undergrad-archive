// EPOS Global System Abstraction Declarations

#ifndef __system_h
#define __system_h

#include <utility/heap.h>

// Specialized system allocators
__BEGIN_API
enum Heap_Type_System { SYSTEM };
enum Heap_Type_Uncached { UNCACHED };
__END_API

inline void * operator new(size_t bytes, const EPOS::Heap_Type_System &);
inline void * operator new[](size_t bytes, const EPOS::Heap_Type_System &);

inline void * operator new(size_t bytes, const EPOS::Heap_Type_Uncached &);
inline void * operator new[](size_t bytes, const EPOS::Heap_Type_Uncached &);

__BEGIN_SYS

class System
{
    friend class Init_System;
    friend class Init_Application;
    //friend void * kmalloc(size_t);
    //friend void kfree(void *);

    friend void * ::operator new(size_t bytes, const EPOS::Heap_Type_System &);
    friend void * ::operator new[](size_t bytes, const EPOS::Heap_Type_System &);
    friend void * ::operator new(size_t bytes, const EPOS::Heap_Type_Uncached &);
    friend void * ::operator new[](size_t bytes, const EPOS::Heap_Type_Uncached &);

public:
    static System_Info<Machine> * const info() { assert(_si); return _si; }

private:
    static void init();

private:
    static System_Info<Machine> * _si;
    static char _preheap[sizeof(Heap)];
    static Heap * _heap;

    static char _preheap_uncached[sizeof(Heap)];
    static Heap * _uncached_heap;
};

__END_SYS

// System heaps allocator definition

// SYSTEM heap allocator
inline void * operator new(size_t bytes, const EPOS::Heap_Type_System & heap) {
    return EPOS::System::_heap->alloc(bytes);
}
inline void * operator new[](size_t bytes, const EPOS::Heap_Type_System & heap) {
    return EPOS::System::_heap->alloc(bytes);
}

// UNCACHED heap allocator
inline void * operator new(size_t bytes, const EPOS::Heap_Type_Uncached & heap) {
    return EPOS::System::_uncached_heap->alloc(bytes);
}
inline void * operator new[](size_t bytes, const EPOS::Heap_Type_Uncached & heap) {
    return EPOS::System::_uncached_heap->alloc(bytes);
}

#endif
