// EPOS Semaphore Abstraction Test Program

#include <utility/ostream.h>
#include <thread.h>
#include <mutex.h>
#include <semaphore.h>
#include <alarm.h>
#include <display.h>
#include <machine.h>
#include <string.h>

using namespace EPOS;

const int iterations = 10;

Mutex table;

Thread * phil[5];
Semaphore * chopstick[5];

OStream cout;

int delay;

int philosopher(int n, int l, int c)
{
    int first = (n < 4)? n : 0;
    int second = (n < 4)? n + 1 : 4;

    for(int i = iterations; i > 0; i--) {

        table.lock();
        cout << "<" << Machine::cpu_id() << ">:\tthinking\tphilo=" << n << endl;
        table.unlock();

        int a = 1, b = 1;
        for ( a = 1 ; a <= 10000 ; a++ )
            for ( b = 1 ; b <= 2000 ; b++ )
            	delay= b/a;

        chopstick[first]->p();    // get first chopstick
        chopstick[second]->p();   // get second chopstick

        table.lock();
        cout << "<" << Machine::cpu_id() << ">:\teating\t\tphilo=" << n << endl;
        table.unlock();

        for ( a = 1 ; a <= 10000 ; a++ )
            for ( b = 1 ; b <= 2000 ; b++ )
            	delay= b/a;

        chopstick[first]->v();    // release first chopstick
        chopstick[second]->v();   // release second chopstick
    }

    table.lock();
    cout << "<" << Machine::cpu_id() << ">:\tdone\t\tphilo=" << n << endl;
    table.unlock();
    return iterations;
}


int main()
{
    table.lock();
    cout << "The Philosopher's Dinner:" << endl;
    cout << "\nCPU\tSTATE\t\tPHILOSOPHER" << endl;
    table.unlock();

    for(int i = 0; i < 5; i++)
        chopstick[i] = new Semaphore;

    phil[0] = new Thread(&philosopher, 0, 5, 5);
    phil[1] = new Thread(&philosopher, 1, 5, 20);
    phil[2] = new Thread(&philosopher, 2, 5, 35);
    phil[3] = new Thread(&philosopher, 3, 5, 50);
    phil[4] = new Thread(&philosopher, 4, 5, 65);


    for(int i = 0; i < 5; i++) {
        int ret = phil[i]->join();
        table.lock();
        cout << "Philosopher " << i << " ate " << ret << " times " << endl;
        table.unlock();
    }

    for(int i = 0; i < 5; i++)
        delete chopstick[i];
    for(int i = 0; i < 5; i++)
        delete phil[i];

    cout << "The end!" << endl;

    return 0;
}
