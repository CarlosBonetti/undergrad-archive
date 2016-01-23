/* @flow */

import { Time } from './time';
import { TimedWeightedMeanVariable } from './stats';

/**
 * Célula de cobertura telefônica
 */
export class Cell {
  /**
   * Número de canais máximo desta célula
   */
  _channels: number;

  /**
  * Número de canais sendo usados na célula
  */
  _usedChannels: number;

  /**
   * Estatísticas do uso dos canais de chamadas do sistema (atual, média, mínimo, máximo)
   */
  _useStats: TimedWeightedMeanVariable;

  /**
   * Chamadas em uso da célula
   */
  _calls: Call[];

  _missCalls: number;

  /**
   * Nome desta célula
   * @type {[type]}
   */
  name: string;

  constructor(channels: number, name: string) {
    this._channels = channels;
    this._usedChannels = 0;
    this._calls = [];
    this._missCalls = 0;
    this.name = name;
    this._useStats = new TimedWeightedMeanVariable();
  }

  /**
   * Checa se a célula está disponível (ao menos um canal está disponível)
   */
  available(): boolean {
    return this._usedChannels < this._channels;
  }

  /**
   * aloca um canal quando disponível
   */
  allocateChannel(call: Call, time: Time) {
    if (this.available()) {
      this._usedChannels++;
      this._useStats.include(this._usedChannels, time);
      this._calls.push(call);
      call.actualCell = this;
    }
  }

  /**
   * desaloca um canal quando disponível
   */
  freeChannel(call: Call, time: Time) {
    if (this._usedChannels > 0) {
      this._usedChannels--;
      this._useStats.include(this._usedChannels, time);
      this._calls.splice(this._calls.indexOf(call), 1);
      call.actualCell = undefined;
    }
  }

  missCall(){
    this._missCalls++;
  }
}

/**
 * Chamada telefônica
 */
export class Call {
  /**
  * Trajetoria da Chamada cells[Célula início, Célula fim]
  */
  cells: Cell[];

  /**
   * Célula em que a chamada se encontra atualmente
   */
  actualCell: ?Cell;

  /**
  * Duração da Chamada
  */
  duration: Time;

  /**
  * Tempo que a chamada se iniciou
  */
  arrivalTime: Time;

  /**
   * Identificador numérico único da chamada
   */
  index: number;

  constructor(cells: Cell[], duration: Time, arrivalTime: Time){
    this.cells = cells;
    this.duration = duration;
    this.arrivalTime = arrivalTime;
  }

  /**
   * Checa se esta chamada é do tipo que migra (troca de célula)
   */
  migrates(): boolean {
    return this.cells[1] !== undefined;
  }

  /**
   * Tempo de finalização da chamada (arrivalTime + duration)
   */
  endTime(): Time {
    return Time.seconds(this.arrivalTime.toSeconds() + this.duration.toSeconds());
  }

  /**
   * Retorna o momento em que esta chamada migrou (trocou de célula) caso
   * esta chamada seja do tipo que migre
   * Lança um erro caso a chamada não seja do tipo que migre
   */
  migrationTime(): Time {
    if (!this.migrates()) {
      throw new Error("Esta chamada não é do tipo que migra");
    }

    return Time.seconds(this.arrivalTime.toSeconds() + this.duration.toSeconds() / 2);
  }

  /**
   * Retorna o nome do "tipo" da chamada, baseado nas células deste call
   */
  type(): string {
    var type = "";

    if (this.cells.length == 1) {
      return this.cells[0].name + this.cells[0].name;
    }

    for (var i = 0; i < this.cells.length; i++) {
      type += this.cells[i].name;
    }
    return type;
  }
}
