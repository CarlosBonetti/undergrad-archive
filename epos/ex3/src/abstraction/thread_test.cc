// EPOS Thread Test Program

#include <utility/ostream.h>
#include <thread.h>
#include <alarm.h>

using namespace EPOS;

const int iterations = 3;
const int delay      = 10; // Original:  500000

int func_a(void);
int func_b(void);

Thread * a;
Thread * b;
Thread * m;

OStream cout;

int main()
{
    cout << "Thread test" << endl;

    m = Thread::self();

    cout << "I'm the first thread of the first task created in the system." << endl;
    cout << "I'll now create two threads and then wait for them to finish ..." << endl;

    a = new Thread(&func_a);
    b = new Thread(&func_b);

    int status_a = a->join();
    int status_b = b->join();

    cout << "Thread A exited with status " << status_a 
         << " and thread B exited with status " << status_b << "" << endl;

    delete b;
    delete a;
    delete m;
    
    cout << "It should not be shown on the display!" << endl;

    return 0;
}

int func_a(void)
{
    for(int i = iterations; i > 0; i--) {
        for(int i = 0; i < 79; i++)
            cout << "a";
        cout << "" << endl;
        Alarm::delay(delay);
    }

    return 'A';   
}

int func_b(void)
{
    for(int i = iterations; i > 0; i--) {
        for(int i = 0; i < 79; i++)
            cout << "b";
        cout << "" << endl;
        Alarm::delay(delay);
    }

    return 'B';   
}
