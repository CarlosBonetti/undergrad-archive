// EPOS Heap Utility Declarations

#ifndef __heap_h
#define __heap_h

#include <utility/debug.h>
#include <utility/list.h>
#include <utility/spin.h>
#include <segment.h>
#include <address_space.h>

// Delete cannot be declared inline due to virtual destructors
void operator delete(void * ptr);
void operator delete[](void * ptr);

__BEGIN_UTIL

// Heap
class Heap: private Grouping_List<char>, public Segment
{
public:
    using Grouping_List<char>::empty;
    using Grouping_List<char>::size;

    Heap(void * addr, unsigned int bytes, Flags flags = Flags::APP) : Segment(bytes, flags) {
        db<Init, Heaps>(TRC) << "Heap(addr=" << addr << ",bytes=" << bytes << ") => " << this << endl;

        Address_Space(MMU::current()).attach(this);

        free(addr, bytes);
    }

    void * alloc(unsigned int bytes) {
        db<Heaps>(TRC) << "Heap::alloc(this=" << this << ",bytes=" << bytes;

        if(!bytes)
            return 0;

        if(!Traits<CPU>::unaligned_memory_access)
            while((bytes % sizeof(void *)))
                ++bytes;

        // add room for heap pointer
        bytes += sizeof(Heap *);

        bytes += sizeof(int);         // add room for size
        if(bytes < sizeof(Element))
            bytes = sizeof(Element);

        Element * e = search_decrementing(bytes);
        if(!e) {
            out_of_memory();
            return 0;
        }

        int * addr = reinterpret_cast<int *>(e->object() + e->size());

        *addr++ = reinterpret_cast<int>(this); // add heap pointer

        *addr++ = bytes;

        db<Heaps>(TRC) << ") => " << reinterpret_cast<void *>(addr) << endl;

        return addr;
    }

    void free(void * ptr, unsigned int bytes) {
        db<Heaps>(TRC) << "Heap::free(this=" << this << ",ptr=" << ptr << ",bytes=" << bytes << ")" << endl;

        if(ptr && (bytes >= sizeof(Element))) {
            Element * e = new (ptr) Element(reinterpret_cast<char *>(ptr), bytes);
            Element * m1, * m2;
            insert_merging(e, &m1, &m2);
        }
    }

    void free(void * ptr) {
        int * addr = reinterpret_cast<int *>(ptr);
        unsigned int bytes = *--addr;
        --addr; // Subtract Heap pointer size
        free(addr, bytes);
    }

private:
    void out_of_memory();
};

__END_UTIL

#endif
