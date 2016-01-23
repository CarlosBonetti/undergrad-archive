var assert = require("assert");

import { Time } from '../src/time';

describe('TestUnit', function() {
  describe('static seconds()', function() {
    it('should return the right amount of time', function() {
      let t = Time.seconds(23);
      assert.equal(t._seconds, 23);

      let t2 = Time.seconds(2.67);
      assert.equal(t2._seconds, 2.67);
    });
  });

  describe('static minutes()', function() {
    it('should return the right amount of time', function() {
      let t = Time.minutes(3);
      assert.equal(t._seconds, 180);

      let t2 = Time.minutes(2.5);
      assert.equal(t2._seconds, 150);
    });
  });

  describe('#toSeconds()', function() {
    it('should convert internal time to seconds', function() {
      let t = Time.minutes(2);
      assert.equal(t.toSeconds(), 120);
    });
  });

  describe('#toSeconds()', function() {
    it('should convert internal time to minutes', function() {
      assert.equal(Time.minutes(2).toMinutes(), 2);
      assert.equal(Time.seconds(30).toMinutes(), 0.5);
      assert.equal(Time.seconds(630).toMinutes(), 10.5);
    });
  });

  describe('#compare()', function() {
    it('should compare times', function() {
      let t1 = Time.seconds(1);
      let t2 = Time.seconds(2);
      assert.ok(t1.compare(t2) < 0);
      assert.ok(t2.compare(t1) > 0);
      assert.ok(t1.compare(t1) == 0);
      assert.ok(t2.compare(t2) == 0);
    });
  });

  describe('#add()', function() {
    it('should add two times', function() {
      let t1 = Time.seconds(3);
      let t2 = Time.minutes(2);
      assert.equal(t1.add(t2).toSeconds(), 123);
      assert.equal(t2.add(t1).toSeconds(), 123);
    });
  });

  describe('#toTimeString()', function() {
    it('should return the time formatted', function() {
      assert.equal(Time.seconds(3).toTimeString(), '00:00:03');
      assert.equal(Time.seconds(39).toTimeString(), '00:00:39');

      assert.equal(Time.seconds(61).toTimeString(), '00:01:01');
      assert.equal(Time.seconds(145).toTimeString(), '00:02:25');
      assert.equal(Time.minutes(10.5).toTimeString(), '00:10:30');

      assert.equal(Time.minutes(61).toTimeString(), '01:01:00');
      assert.equal(Time.minutes(600.05).toTimeString(), '10:00:03');
    });
  });
});
