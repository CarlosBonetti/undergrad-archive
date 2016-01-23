// EPOS Memory Allocation Utility Test Program

#include <utility/ostream.h>
#include <utility/string.h>
#include <utility/malloc.h>
#include <system.h>

using namespace EPOS;

int main()
{
    OStream cout;

    cout << "Memory allocation test" << endl;

    char * cp = new char('A');
    cout << "new char('A')\t\t=> {p=" << (void *)cp << ",v=" << *cp << "}" << endl;
    int * ip = new int(1);
    cout << "new int(1)\t\t=> {p=" << (void *)ip << ",v=" << *ip << "}" << endl;
    long int * lp = new long int(1);
    cout << "new long int(1)\t\t=> {p=" << (void *)lp << ",v=" << *lp << "}" << endl;
    char * sp = new char[1024];
    strcpy(sp, "string");
    cout << "new char[1024]\t\t=> {p=" << (void *)sp << ",v=" << sp << "}" << endl;

    char * z = new (SYSTEM) char[64];
    int * addr = reinterpret_cast<int *>(z);
    unsigned int bytes = *(--addr);
    EPOS::Heap * heap = reinterpret_cast<EPOS::Heap *>(*(--addr));
    cout << "new char[64] SYSTEM\t\t=> {p=" << (void *)z << ",z=" << *z << "}"
         << "\t\t=> {bytes=" << bytes << ", heap_ptr=" << heap << "}" << endl;

    char * x = new (SYSTEM) char('X');
    cout << "new char('X')\t\t=> {p=" << (void *)x << ",v=" << *x << "}" << endl;

    char * u = new (UNCACHED) char[64];
    addr = reinterpret_cast<int *>(u);
    bytes = *(--addr);
    heap = reinterpret_cast<EPOS::Heap *>(*(--addr));
    cout << "new char[64] UNCACHED\t\t=> {p=" << (void *)u << ",u=" << *u << "}"
         << "\t\t=> {bytes=" << bytes << ", heap_ptr=" << heap << "}" << endl;

    char * y = new (UNCACHED) char('Y');
    cout << "new char('Y')\t\t=> {p=" << (void *)y << ",v=" << *y << "}" << endl;

    cout << "deleting everything!" << endl;

    delete cp;
    delete ip;
    delete lp;
    delete sp;
    delete x;
    delete z;
    delete u;
    delete y;

    cout << "and doing it all again!" << endl; 
    cp = new char('A');
    cout << "new char('A')\t\t=> {p=" << (void *)cp << ",v=" << *cp << "}" << endl;
    ip = new int(1);
    cout << "new int(1)\t\t=> {p=" << (void *)ip << ",v=" << *ip << "}" << endl;
    lp = new long int(1);
    cout << "new long int(1)\t\t=> {p=" << (void *)lp << ",v=" << *lp << "}" << endl;
    sp = new char[1024];
    strcpy(sp, "string");
    cout << "new char[1024]\t\t=> {p=" << (void *)sp << ",v=" << sp << "}" << endl;

    z = new (SYSTEM) char[64];
    addr = reinterpret_cast<int *>(z);
    bytes = *(--addr);
    heap = reinterpret_cast<EPOS::Heap *>(*(--addr));
    cout << "new char[64]\t\t=> {p=" << (void *)z << ",z=" << *z << "}"
         << "\t\t=> {bytes=" << bytes << ", heap_ptr=" << heap << "}" << endl;

    x = new (SYSTEM) char('X');
    cout << "new char('X')\t\t=> {p=" << (void *)x << ",v=" << *x << "}" << endl;
    
    u = new (UNCACHED) char[64];
    addr = reinterpret_cast<int *>(u);
    bytes = *(--addr);
    heap = reinterpret_cast<EPOS::Heap *>(*(--addr));
    cout << "new char[64] UNCACHED\t\t=> {p=" << (void *)u << ",u=" << *u << "}"
         << "\t\t=> {bytes=" << bytes << ", heap_ptr=" << heap << "}" << endl;

    y = new (UNCACHED) char('Y');
    cout << "new char('Y')\t\t=> {p=" << (void *)y << ",v=" << *y << "}" << endl;

    cout << "deleting everything!" << endl;

    return 0;
}
