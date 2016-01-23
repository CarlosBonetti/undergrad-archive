var assert = require("assert");

import {
  Constant, Uniform, Triangular, Exponential, Normal
} from '../src/distributions';

/**
 * assert.equals for fractional values
 */
assert.similar = function(expected, actual, tolerance = 0.001) {
  let msg = "Expected value ("+expected+") not close enough to actual value ("+actual+")";

  if (expected > actual)
    assert(expected - actual <= tolerance, msg);
  else
    assert(actual - expected <= tolerance, msg);
};

describe('Constant', function() {
  let c1 = new Constant(3.14);
  let c2 = new Constant(2.02);

  it('should always return the constant value', function() {
    assert.equal(3.14, c1.get());
    assert.equal(3.14, c1.get());
    assert.equal(2.02, c2.get());
  });
});

describe('Uniform', function() {
  let u1 = new Uniform(10, 50);

  it('should return random values uniformely distributed between min and max', function() {
    var value;
    for(var i = 0; i < 100; i++) {
      // not a very good test but maybe good enough...
      value = u1.get(Math.random());
      // console.log(value);
      assert(value >= 10 && value <= 50);
    }
  });

  it('should return expected values', function() {
    assert.similar(47.28, u1.get(0.932));
    assert.similar(14.2, u1.get(0.105));
    assert.similar(37.48, u1.get(0.687));
  });
});

describe('Triangular', function() {
  let t1 = new Triangular(0, 1, 10);

  it('should return value between min and max', function() {
    var value;
    for(var i = 0; i < 100; i++) {
      // not a very good test but maybe good enough...
      value = t1.get(Math.random());
      // console.log(value);
      assert(value >= 0 && value <= 10);
    }
  });

  let t2 = new Triangular(0, 1, 2);
  it('should return expected values', function() {
    assert.similar(1.045, t2.get(0.544));
    assert.similar(1.288, t2.get(0.747));
    assert.similar(0.947, t2.get(0.449));
  });
});

describe('Exponential', function() {
  let e1 = new Exponential(1);

  it('should return expected values', function() {
    assert.similar(0.1399, e1.get(0.1306));
    assert.similar(0.0431, e1.get(0.0422));
    assert.similar(1.0779, e1.get(0.6597));
    assert.similar(1.5920, e1.get(0.7965));
    assert.similar(1.4679, e1.get(0.7696));
  });
});

describe('Normal', function() {
  let n1 = new Normal(10, 2);

  it('should return expected values', function() {
    assert.similar(12.22, n1.get(0.1758, 0.1489), 0.01);
    assert.similar(12.22, n1.get1(0.1758, 0.1489), 0.01);
    assert.similar(13.00, n1.get2(0.1758, 0.1489), 0.01);
  });

  it('should accept just one random sample', function() {
    assert(n1.get(0.452));
  });
});
