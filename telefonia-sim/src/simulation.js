/* @flow */

import * as Event from './events';
import { Time } from './time';
import { Call, Cell } from './telephony';
import * as Distribution from './distributions';
import { MeanVariable, TimedWeightedMeanVariable } from './stats';

export class Simulation {
  /**
   * Calendário de eventos de simulação
   */
  calendar: Calendar;

  /**
   * Simulação terminada ou não
   */
  _finished: boolean;

  /**
   * Tempo total de simulação
   */
  simulationTime: Time;

  /**
   * Tempo atual de simulação
   */
  actualTime: Time;

  /**
   * Células desta simulação
   */
  cells: Cell[];

  generatedCalls: number;

  /**
  * Número de chamadas completadas da simulação
  */
  _completedCalls: number;

  /**
   * Estatísticas (min, max, média) sobre a duração das chamadas
   */
  _durationStat: MeanVariable;

  /**
   * Estatísticas do número de chamadas no sistema (atual, média, mínimo, máximo)
   */
  _callsStat: TimedWeightedMeanVariable;

  c1Opt: Object;
  c2Opt: Object;

  constructor(simulationTime: Time, c1Opt: any, c2Opt: any) {
    this.calendar = new Calendar();
    this._finished = false;
    this.simulationTime = simulationTime;
    this.actualTime = Time.seconds(0);
    this.generatedCalls = 0;
    this.c1Opt = c1Opt || {};
    this.c2Opt = c2Opt || {};
    this._completedCalls = 0;
    this._durationStat = new MeanVariable();
    this._callsStat = new TimedWeightedMeanVariable();

    this.cells = [];
    this.cells.push(new Cell(this.c1Opt.channels || 10, "C1"));
    this.cells.push(new Cell(this.c2Opt.channels || 10, "C2"));
    this.cells.push(new Cell(0, "FA"));
  }

  start() {
    this.calendar.insert(new Event.Init(Time.seconds(0)));
    this.calendar.insert(new Event.End(this.simulationTime));
  }

  /**
   * Processa o próxima evento da simulação
   */
  step() {
    if (this._finished) {
        throw new Error("Simulação já terminou");
    }

    if (this.actualTime === undefined) {
      throw new Error("Simulação não foi iniciada. Chame start()");
    }

    var ev = this.calendar.next();
    this.actualTime = ev.time;
    ev.process(this);
  }

  /**
   * Checa se a simulação já terminou
   */
  finished(): boolean {
    return this._finished;
  }

  /**
   * Encerra a simulação
   */
  finish(): void {
    this._finished = true;
  }

  /**
   * Incrementa número de chamadas completadas
   */
  completeCall(): void {
    this._completedCalls++;
  }

  /**
   * Insere um novo evento no calendário desta simulação
   */
  insertEvent(event: Event.EventSim): void {
    this.calendar.insert(event);
  }

  /**
   * Cria e retorna uma nova chamada, sorteando valores conforme distribuições
   * de frequência e probabilidade da simulação
   */
  generateCall(): Call {
    this.generatedCalls++;
    var origin = this.pickOriginCell();
    var next = this.pickNextCell(origin);
    var cells = origin == next ? [origin] : [origin, next];

    var duration = this.pickDuration(origin);
    var arrivalTime = this.actualTime.add(this.pickTec(origin));

    var call = new Call(cells, duration, arrivalTime);
    call.index = this.generatedCalls;
    return call;
  }

  incrementCalls() {
    this._callsStat.include(this._callsStat.actual() + 1, this.actualTime);
  }

  decrementCalls() {
    this._callsStat.include(this._callsStat.actual() - 1, this.actualTime);
  }

  pickOriginCell(): Cell {
    // TODO: tornar isso configurável?
    return Math.random() < 0.5 ? this.cells[0] : this.cells[1];
  }

  pickNextCell(origin: Cell): Cell {
    var r = Math.random() * 100;

    if (origin.name == "C1") {
      if (r < this.c1Opt.C1C1) return this.cells[0];
      if (r < this.c1Opt.C1C1 + this.c1Opt.C1C2) return this.cells[1];
      else return this.cells[2];
    } else {
      if (r < this.c2Opt.C2C2) return this.cells[1];
      if (r < this.c2Opt.C2C2 + this.c2Opt.C2C1) return this.cells[0];
      else return this.cells[2];
    }
  }

  pickTec(origin: Cell): Time {
    var dist;
    if (origin.name == "C1") {
      dist = this.c1Opt.tec || new Distribution.Constant(10);
    } else {
      dist = this.c2Opt.tec || new Distribution.Constant(10);
    }

    var sec = Math.abs(dist.get(Math.random()));
    return Time.seconds(sec);
  }

  pickDuration(origin: Cell): Time {
    var dist;
    if (origin.name == "C1") {
      dist = this.c1Opt.duration || new Distribution.Constant(2);
    } else {
      dist = this.c2Opt.duration || new Distribution.Constant(2);
    }

    var sec = Math.abs(dist.get(Math.random()));
    return Time.seconds(sec);
  }
}

/**
 * Calendário de eventos
 */
export class Calendar {
  _array: Event.EventSim[];
  _actualIndex: number; // Evento atual sendo processado

  constructor() {
    this._array = [];
    this._actualIndex = -1;
  }

  insert(event: Event.EventSim) {
    if (this._actualIndex !== -1 && event.time._seconds < this._array[this._actualIndex].time._seconds) {
      throw new Error("Evento lançado para ocorrer no passado!");
    }

    var init = this._actualIndex + 1;
    var pos = init;
    for (var i = init; i < this.size(); i++) {
      if (event.time._seconds < this._array[i].time._seconds) {
        break;
      }
      pos++;
    }
    this._array.splice(pos, 0, event);
  }

  /**
   * Retorna o próximo evento do calendário
   */
  next(): Event.EventSim {
    this._actualIndex++;
    return this._array[this._actualIndex];
  }

  /**
   * Retorna o tamanho (número de eventos) to calendário
   */
  size(): number {
    return this._array.length;
  }

}
