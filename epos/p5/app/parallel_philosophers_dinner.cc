// EPOS Scheduler Test Program

#include <utility/ostream.h>
#include <machine.h>
#include <display.h>
#include <thread.h>
#include <semaphore.h>
#include <alarm.h>

using namespace EPOS;

const int iterations = 10;

Semaphore table;

Thread * phil[5];
Semaphore * chopstick[5];

OStream cout;

int philosopher(int n, int l, int c);
void think(unsigned long long n);
void eat(unsigned long long n);
unsigned long long busy_wait(unsigned long long n);

int main()
{
    table.p();
    Display::clear();
    Display::position(0, 0);
    cout << "The Philosopher's Dinner:" << endl;

    for(int i = 0; i < 5; i++)
        chopstick[i] = new Semaphore;

    phil[0] = new Thread(&philosopher, 0,  5, 30);
    phil[1] = new Thread(&philosopher, 1, 10, 44);
    phil[2] = new Thread(&philosopher, 2, 16, 39);
    phil[3] = new Thread(&philosopher, 3, 16, 21);
    phil[4] = new Thread(&philosopher, 4, 10, 17);

    cout << "Philosophers are alive and hungry!" << endl;

    Display::position(7, 44);
    cout << '/';
    Display::position(13, 44);
    cout << '\\';
    Display::position(16, 35);
    cout << '|';
    Display::position(13, 27);
    cout << '/';
    Display::position(7, 27);
    cout << '\\';
    Display::position(19, 0);

    cout << "The dinner is served ..." << endl;
    table.v();

    for(int i = 0; i < 5; i++) {
        int ret = phil[i]->join();
        table.p();
        Display::position(20 + i, 0);
        cout << "Philosopher " << i << " ate " << ret << " times " << endl;
        table.v();
    }

    cout << "\nPhilosopher\t" << "C_0\t\t" << "C_1\t\t" << "C_2\t\t" << "C_3" << endl;

    for(int i = 0; i < 5; i++) {
           int ret = phil[i]->join();
           Thread::Stats::Microsecond * a = phil[i]->total_running_time();
           table.p();
           Display::position(28 + i, 0);
           cout << i << endl;
           Display::position(28 + i, 15);
           cout << a[0] << endl;
           Display::position(28 + i, 31);
           cout << a[1] << endl;
           Display::position(28 + i, 47);
           cout << a[2] << endl;
           Display::position(28 + i, 65);
           cout << a[3]<< endl;
//           cout << " " << i << "\t" << a[0] << "\t"<< a[1]<< "\t"<< a[2]<< "\t"<< a[3]<< endl;
           table.v();
       }

	   for(int i = 0; i < 5; i++) {
		   int ret = phil[i]->join();
		   table.p();
			Display::position(34 + i, 0);
			cout << "Philosopher " << i << " total running time: "
				<< phil[i]->total_running_time_all() << "                    ";
		   table.v();
	   }

    for(int i = 0; i < 5; i++)
        delete chopstick[i];
    for(int i = 0; i < 5; i++)
        delete phil[i];

    cout << "The end!" << endl;

    return 0;
}

int philosopher(int n, int l, int c)
{
    int first = (n < 4)? n : 0;
    int second = (n < 4)? n + 1 : 4;

    for(int i = iterations; i > 0; i--) {

        table.p();

        Display::position(l, c);
        cout << "thinking[" << Machine::cpu_id() << "]";
        table.v();

        think(1000000);

        table.p();
        Display::position(l, c);
        cout << "  hungry[" << Machine::cpu_id() << "]";
        table.v();

        chopstick[first]->p();   // get first chopstick
        chopstick[second]->p();  // get second chopstick

        table.p();
        Display::position(l, c);
        cout << " eating[" << Machine::cpu_id() << "] ";
        table.v();

        eat(500000);

        table.p();
        Display::position(l, c);
        cout << "    sate[" << Machine::cpu_id() << "]";
        table.v();

        chopstick[first]->v();   // release first chopstick
        chopstick[second]->v();  // release second chopstick

        table.p();
        Display::position(20 + n, 0);
        cout << "Philosopher " << n << " total running time: "
            << Thread::self()->total_running_time_all() << "                    ";
        table.v();
    }

    table.p();
    Display::position(l, c);
    cout << "  done[" << Machine::cpu_id() << "]  ";
    table.v();

    return iterations;
}

void eat(unsigned long long n) {
    static unsigned long long v;
    v = busy_wait(n);
}

void think(unsigned long long n) {
    static unsigned long long v;
    v = busy_wait(n);
}

unsigned long long busy_wait(unsigned long long n)
{
    volatile unsigned long long v;
    for(long long int j = 0; j < 20 * n; j++)
        v &= 2 ^ j;
    return v;
}
