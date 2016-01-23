var assert = require("assert");

import * as Event from '../src/events';
import { Calendar, Simulation } from '../src/simulation';
import { Time } from '../src/time';
import * as Distribution from '../src/distributions';

describe ('TestCalendar', function(){
  describe('insert()', function(){
    it('should insert in order of event.time', function() {
      let c = new Calendar();
      let e1 = new Event.EventSim(Time.seconds(25));
      let e2 = new Event.EventSim(Time.seconds(67));
      let e3 = new Event.EventSim(Time.seconds(50));
      let e4 = new Event.EventSim(Time.seconds(30));
      let e5 = new Event.EventSim(Time.seconds(100));

      c.insert(e1);
      assert.deepEqual(c._array, [e1]);
      c.insert(e2);
      assert.deepEqual(c._array, [e1, e2]);
      c.insert(e3);
      assert.deepEqual(c._array, [e1, e3, e2]);
      c.insert(e4);
      assert.deepEqual(c._array, [e1, e4, e3, e2]);
      c.insert(e5);
      assert.deepEqual(c._array, [e1, e4, e3, e2, e5]);

      c.next();
      assert.throws(function() {
        c.insert(new Event.EventSim(Time.seconds(1)));
      });
    });
  });
});
//
//   describe('next()', function(){
//     it('should return next event from queue', function(){
//       let c = new Calendar();
//       let e1 = new Event.EventSim(Time.seconds(2));
//       let e2 = new Event.EventSim(Time.seconds(1));
//       c.insert(e1);
//       c.insert(e2);
//       assert.equal(c.size(), 2);
//       assert.equal(c.next(), e2);
//       assert.equal(c.size(), 2);
//     });
//   })
// })
//
// describe('TestSimuation', function() {
//   describe('start()', function() {
//     it('should insert a Init event at calendar', function(){
//       let s = new Simulation(Time.minutes(3));
//       assert.ok(s.calendar.size() == 0);
//       s.start();
//       assert.ok(s.calendar._array[0] instanceof Event.Init);
//       assert.equal(0, s.calendar._array[0].time.toSeconds());
//     });
//
//     it('should insert a End event at calendar', function(){
//       let s = new Simulation(Time.minutes(4));
//       assert.ok(s.calendar.size() == 0);
//       s.start();
//       assert.ok(s.calendar._array[1] instanceof Event.End);
//       assert.equal(240, s.calendar._array[1].time.toSeconds());
//     });
//   })
//
//   describe('step()', function() {
//       let s = new Simulation(Time.seconds(1));
//
//     it('should process one event from the calendar', function() {
//       s.start();
//       var countEvents = s.calendar.size();
//       s.step(); // Process Init, Generate one call
//       assert.equal(countEvents + 1, s.calendar.size());
//     });
//
//     it('should update actualTime', function() {
//       s.start();
//       s.step();
//       s.step();
//       assert.equal(s.actualTime.toSeconds(), 1);
//     });
//   });
//
//   describe('finish()', function() {
//     let s = new Simulation(Time.minutes(23));
//
//     it('should finish simulation', function(){
//       s.start();
//       s.step();
//       assert.equal(s.finished(), false);
//       s.finish();
//       assert.equal(s.finished(), true);
//     });
//   });
//
//   describe('finished()', function() {
//     let s = new Simulation(Time.seconds(1));
//
//     it('should return state of simulation', function() {
//       s.start();
//       assert.equal(s.finished(), false);
//       s.step();
//       assert.equal(s.finished(), false);
//       s.step();
//       assert.equal(s.finished(), true);
//     });
//   });
//
//   describe('insertEvent()', function(){
//     it('should insert event on Calendar', function() {
//       let s = new Simulation(Time.seconds(30));
//       assert.equal(s.calendar.size(), 0);
//       s.insertEvent(new Event.EventSim(Time.seconds(30)));
//       assert.equal(s.calendar.size(), 1);
//     });
//   });
// });
//
// describe('Simulation integration test', function() {
//   let c1 = {
//     channels: 15,
//     'C1C1': 50,
//     'C1C2': 30,
//     'C1FA': 20,
//     'tec': new Distribution.Constant(5),
//     'duration': new Distribution.Constant(3)
//   };
//
//   let c2 = {
//     channels: 30,
//     'C2C1': 30,
//     'C2C2': 50,
//     'C2FA': 20,
//     'tec': new Distribution.Normal(5, 1),
//     'duration': new Distribution.Exponential(5)
//   };
//
//   let sim = new Simulation(Time.minutes(10), c1, c2);
//   assert.equal(sim.cells[0]._channels, c1.channels);
//   assert.equal(sim.cells[1]._channels, c2.channels);
// });
