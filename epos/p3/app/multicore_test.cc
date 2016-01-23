// EPOS IA32 Test Program

#include <utility/ostream.h>
#include <cpu.h>

using namespace EPOS;

int main()
{
    OStream cout;
    cout << "Multicore test" << endl;

    for(int i = 0; i <= 10000000; i++) {
        if (i % 100000 == 0)
            cout << "Running multicore test... iteration=" << i << endl;
    }

    cout << "Multicore test end!" << endl;

    return 0;
}
