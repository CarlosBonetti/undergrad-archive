/* @flow */

import { Time } from './time';

export class MeanVariable {
  _register: number[];
  _min: number;
  _max: number;

  constructor() {
    this._register = [];
  }

  include(item: number) {
    this._register.push(item);

    if (this._min == null || item < this._min)
      this._min = item;

    if (this._max == null || item > this._max)
      this._max = item;
  }

  size(): number {
    return this._register.length;
  }

  actual(): number {
    return this._register[this.size() - 1];
  }

  total(): number {
    var total = 0;
    for (var i = 0; i < this.size(); i++) {
      total += this._register[i];
    }
    return total;
  }

  mean(): number {
    return this.total() / this.size();
  }

  min(): number {
    return this._min;
  }

  max(): number {
    return this._max;
  }
}

export class TimedWeightedMeanVariable {
  _register: any[];
  _min: number;
  _max: number;

  constructor(initialState: number = 0) {
    this._register = [];
    this.include(initialState, Time.seconds(0));
  }

  include(state: number, time: Time) {
    this._register.push({
      state: state,
      time: time
    });

    if (this._min == null || state < this._min)
      this._min = state;

    if (this._max == null || state > this._max)
      this._max = state;
  }

  size(): number {
    return this._register.length;
  }

  actual(): number {
    return this._register[this.size() - 1].state;
  }

  mean(actualTime: Time): number {
    var total = 0;
    for (var i = 0; i < this.size(); i++) {
      var init = this._register[i].time;
      var end = i < this.size() - 1 ? this._register[i + 1].time : actualTime;
      var periodInSeconds = end.compare(init);
      total += this._register[i].state * periodInSeconds;
    }

    return total / actualTime.toSeconds();
  }

  min(): number {
    return this._min;
  }

  max(): number {
    return this._max;
  }
}
