// EPOS Heap Utility Implementation

#include <utility/heap.h>

extern "C" { void _panic(); }

__BEGIN_UTIL

// Methods
void Heap::out_of_memory()
{
    db<Heaps>(ERR) << "Heap::alloc(this=" << this << "): out of memory!" << endl;

    _panic();
}

__END_UTIL

void operator delete(void * ptr) {
    int * addr = reinterpret_cast<int *>(ptr);
    unsigned int bytes = *(--addr);
    EPOS::Heap * heap = reinterpret_cast<EPOS::Heap *>(*--addr);
    heap->free(addr, bytes);
}
void operator delete[](void * ptr) {
    int * addr = reinterpret_cast<int *>(ptr);
    unsigned int bytes = *(--addr);
    EPOS::Heap * heap = reinterpret_cast<EPOS::Heap *>(*--addr);
    heap->free(addr, bytes);
}
