var assert = require("assert");

import * as Event from '../src/events';
import { Time } from '../src/time';
import { Simulation } from '../src/simulation';
import { Call, Cell } from '../src/telephony';

let simulation1 = new Simulation(Time.minutes(30));

describe('Event', function() {
  let e = new Event.EventSim(Time.seconds(30));

  it('should fail when process is called', function() {
    assert.throws(function() {
      e.process(simulation1);
    });
  });

  it('should be initilizable with processing time', function() {
    assert.equal(30, e.time.toSeconds());
  });
});

describe('Init', function(){
  it('should create a NewCall event', function() {
    let s = new Simulation(Time.minutes(30));
    s.start();
    s.step(); // Process init
    assert.ok(s.calendar.next() instanceof Event.NewCall);
  });
});

describe('End', function() {
  it('should finish simulation', function() {
    let e = new Event.End(Time.seconds(2));
    e.process(simulation1);
    assert.equal(true, simulation1.finished());
  });
});

describe('NewCall', function(){
  it('should launch new call event', function(){
    let s = new Simulation(Time.minutes(30));
    s.start();
    s.step(); // Process init (New Call)
    s.step(); // Process new Call (and generates another)
    assert.ok(s.calendar.next() instanceof Event.NewCall || s.calendar.next() instanceof Event.NewCall);
  });

  it('should allocate cell channel', function() {
    let s = new Simulation(Time.minutes(30));
    let cell = new Cell(2);
    let call = new Call([cell], Time.seconds(2), Time.seconds(0));
    let e = new Event.NewCall(Time.seconds(0), call);
    assert.equal(cell._usedChannels, 0);
    e.process(s);
    assert.equal(cell._usedChannels, 1);
  });

  it('should occurrer missCall during new call event', function() {
    let s = new Simulation(Time.minutes(30));
    let cell = new Cell(0);
    let call = new Call([cell], Time.seconds(2), Time.seconds(0));
    let e = new Event.NewCall(Time.seconds(0), call);
    assert.equal(cell._usedChannels, 0);
    e.process(s);
    assert.equal(cell._usedChannels, 0);
    assert.equal(cell._missCalls, 1);
  });
});

describe('Migration', function(){
  it('should migrate current call to another cell', function(){
    let s = new Simulation(Time.minutes(30));
    let c1 = new Cell(2);
    let c2 = new Cell(10);
    let call = new Call([c1, c2], Time.seconds(2), Time.seconds(0));

    c1.allocateChannel(call);
    let e = new Event.Migration(Time.seconds(0), call);

    assert.equal(c1._usedChannels, 1);
    assert.equal(c2._usedChannels, 0);
    e.process(s);
    assert.equal(c1._usedChannels, 0);
    assert.equal(c2._usedChannels, 1);
  });
  it('should occurrer missCall during migration to another cell', function(){
    let s = new Simulation(Time.minutes(10));
    let c1 = new Cell(10);
    let c2 = new Cell(1);
    let call = new Call([c1, c2], Time.minutes(10), Time.seconds(0));
    let call2 = new Call([c1, c2], Time.seconds(30), Time.seconds(0));

    c1.allocateChannel(call);
    c2.allocateChannel(call2);
    let e = new Event.Migration(Time.seconds(0), call);

    assert.equal(c1._usedChannels, 1);
    assert.equal(c2._usedChannels, 1);
    e.process(s);
    assert.equal(c1._usedChannels, 0);
    assert.equal(c2._usedChannels, 1);

    assert.equal(c1._missCalls, 0);
    assert.equal(c2._missCalls, 1);

  });
});

describe('EndCall', function(){
  it('should free cell channel', function() {
    let s = new Simulation(Time.minutes(30));
    let cell = new Cell(2);
    let call = new Call([cell], Time.seconds(5), Time.seconds(1));
    cell.allocateChannel(call);
    let e = new Event.EndCall(call.endTime(), call);

    assert.equal(cell._usedChannels, 1);
    e.process(s);
    assert.equal(cell._usedChannels, 0);
    assert.equal(s._completedCalls, 1);
    assert.equal(s._durationStat.total(), 5);
  });
});
