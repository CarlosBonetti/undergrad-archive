var assert = require("assert");

import { MeanVariable, TimedWeightedMeanVariable } from '../src/stats';
import { Time } from '../src/time';

describe('MeanVariable', function() {
  it('should be alright', function() {
    let m = new MeanVariable();

    m.include(2);
    assert.equal(m.actual(), 2);
    assert.equal(m.total(), 2);
    assert.equal(m.mean(), 2);
    assert.equal(m.max(), 2);
    assert.equal(m.min(), 2);

    m.include(8);
    assert.equal(m.actual(), 8);
    assert.equal(m.total(), 10);
    assert.equal(m.mean(), 5);
    assert.equal(m.max(), 8);
    assert.equal(m.min(), 2);

    m.include(-4);
    assert.equal(m.actual(), -4);
    assert.equal(m.total(), 6);
    assert.equal(m.mean(), 2);
    assert.equal(m.max(), 8);
    assert.equal(m.min(), -4);
  });
});

describe('TimedWeightedMeanVariable', function() {
  it('should be alright', function() {
    let m = new TimedWeightedMeanVariable();

    m.include(2, Time.seconds(5));
    assert.equal(m.actual(), 2);
    assert.equal(m.mean(Time.seconds(10)), 1);
    assert.equal(m.max(), 2);
    assert.equal(m.min(), 0);

    m.include(3, Time.seconds(8));
    assert.equal(m.actual(), 3);
    assert.equal(m.mean(Time.seconds(20)), 2.1);
    assert.equal(m.max(), 3);
    assert.equal(m.min(), 0);

    m.include(10, Time.seconds(10));
    assert.ok(m.mean(Time.seconds(100000000)) - 10 < 0.00001);
  });
});
