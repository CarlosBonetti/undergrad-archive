var assert = require('assert');

import { Cell, Call } from '../src/telephony';
import { Time } from '../src/time';

describe('Cell', function() {
  describe('#constructor()', function() {
    it('should initialize', function() {
      let c1 = new Cell(15);
      assert.equal(c1._channels, 15);
    });
  });

  describe('available', function() {
    it('should return true for channels available', function() {
      let c1 = new Cell(0);
      assert.ok(!c1.available());
      c1 = new Cell(1);
      assert.ok(c1.available());
      c1 = new Cell(-1);
      assert.ok(!c1.available());
    });
  });

  describe('allocateChannel', function() {
    it('should allocate one cell channel', function() {
      let c1 = new Cell(3);
      let call1 = new Call([c1, c1], Time.minutes(1), Time.seconds(1));

      c1.allocateChannel(call1);

      assert.ok(c1.available());
      assert.equal(c1._usedChannels,1);
      assert.equal(c1._calls.length,1);

      let call2 = new Call([c1, c1], Time.minutes(1), Time.seconds(1));

      c1.allocateChannel(call2);

      assert.ok(c1.available());
      assert.equal(c1._usedChannels,2);

      let call3 = new Call([c1, c1], Time.minutes(1), Time.seconds(1));

      c1.allocateChannel(call3);

      assert.ok(!c1.available());
      assert.equal(c1._usedChannels,3);

      let call4 = new Call([c1, c1], Time.minutes(1), Time.seconds(1));

      c1.allocateChannel(call4);

      assert.ok(!c1.available());
      assert.equal(c1._usedChannels,3);
    });

    it('should define actualCell of call', function() {
      let call = new Call();
      let cell1 = new Cell(1);
      let cell2 = new Cell(2);
      assert.equal(call.actualCell, undefined);
      cell1.allocateChannel(call);
      assert.equal(call.actualCell, cell1);
      cell2.allocateChannel(call);
      assert.equal(call.actualCell, cell2);
    });
  });

  describe('freeChannel', function() {
    it('should free one cell channel', function() {
      let c1 = new Cell(3);
      let call1 = new Call([c1, c1], Time.minutes(1), Time.seconds(1));

      c1.allocateChannel(call1);
      c1.freeChannel(call1);
      assert.ok(c1.available());
      assert.equal(c1._usedChannels,0);
      assert.equal(c1._calls.length,0);
     });

     it('should erase actualCell of call', function() {
       let call = new Call();
       let cell1 = new Cell(1);
       cell1.allocateChannel(call);
       assert.equal(call.actualCell, cell1);
       cell1.freeChannel(call);
       assert.equal(call.actualCell, undefined);
     });
  });
});

describe('Call', function() {
  describe('migrates()', function() {
    it('should return whether the call migrates or not', function() {
      let c1 = new Call([]);
      assert.ok(!c1.migrates());
      let c2 = new Call([new Cell(4)]);
      assert.ok(!c2.migrates());
      let c3 = new Call([new Cell(2), new Cell(4)]);
      assert.ok(c3.migrates());
    });
  });

  describe('endTime()', function() {
    it('should return arrivalTime + duration', function() {
      let c1 = new Call([], Time.minutes(2), Time.seconds(23));
      assert.equal(c1.endTime().toSeconds(), 143);
    });
  });

  describe('migrationTime()', function() {
    it('should return arrivalTime + duration / 2', function() {
      let c1 = new Call([new Cell(1), new Cell(2)], Time.minutes(1), Time.seconds(4));
      assert.equal(c1.migrationTime().toSeconds(), 34);
    });
    it('should throw an error for non-migrating calls', function() {
      let c1 = new Call([], Time.minutes(1), Time.seconds(4));
      assert.throws(function() {
        c1.migrationTime().toSeconds();
      });
    });
  });

  describe('type()', function() {
    it('should return call type', function() {
      let c1 = new Cell(4, 'C1');
      let fa = new Cell(0, 'FA');
      let call = new Call([c1, fa]);
      assert.equal(call.type(), "C1FA");
      let call2 = new Call([c1]);
      assert.equal(call2.type(), "C1C1");
    });
  });
});
