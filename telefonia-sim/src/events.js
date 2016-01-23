/* @flow */

import { Time } from './time';
import { Simulation } from './simulation';
import { Call, Cell } from './telephony';

/**
 * Classe base para os eventos da simulação
 */
export class EventSim {
  /**
   * Tempo de processamento deste evento
   */
  time: Time;

  name: string;

  constructor(time: Time) {
    this.time = time;
    this.name = "Evento";
  }

  process(simulation: Simulation): void {
    throw new Error("process() must be defined");
  }
}

/**
 * Início de simulação
 */
export class Init extends EventSim {
  constructor(time: Time) {
    super(time);
    this.name = "Início da simulação";
  }

  process(simulation: Simulation): void {
    NewCall.dispatchNewCall(simulation);
  }
}

/**
 * Fim da simulação
 */
export class End extends EventSim {
  constructor(time: Time) {
    super(time);
    this.name = "Fim da simulação";
  }

  process(simulation: Simulation): void {
    simulation.finish();
  }
}

/**
 * Evento de chegada de nova chamada
 */
export class NewCall extends EventSim {
  /**
   * Cria uma nova chamada e um evento correspondente à sua chegada
   */
  static dispatchNewCall(simulation: Simulation) {
    var call = simulation.generateCall();
    var newEvent = new NewCall(call.arrivalTime, call);
    simulation.insertEvent(newEvent);
  }

  call: Call;

  constructor(time: Time, call: Call) {
    super(time);
    this.call = call;
    this.name = "Nova chamada";
  }

  process(simulation: Simulation): void {
    NewCall.dispatchNewCall(simulation);

    var cell = this.call.cells[0];
    if (cell.available()) {             // Se célula estiver disponível
      cell.allocateChannel(this.call, this.call.arrivalTime);  // Aloca a célula
      simulation.incrementCalls();      // Incrementa o número de chamdas no sistema

      if (this.call.migrates()) {       // Se chamada for do tipo que migra
        // Lança evento de migração
        simulation.insertEvent(new Migration(this.call.migrationTime(), this.call));
      } else {
        // Lança evento de fim de chamada
        simulation.insertEvent(new EndCall(this.call.endTime(), this.call));
      }
    } else {
      cell.missCall();  //incrementador de chamadas perdidas da célula
    }
  }
}

/**
 * Evento de migração de chamada de uma célula para outra
 */
export class Migration extends EventSim {
  call: Call;

  constructor(time: Time, call: Call) {
    super(time);
    this.call = call;
    this.name = "Deslocamento chamada";
  }

  process(simulation: Simulation): void {
    var cell = this.call.actualCell;

    // Desaloca a célula atual
    if (cell)
      cell.freeChannel(this.call, this.call.migrationTime());
    else
      throw new Error("Migration: chamada processada não está em nehuma célula atualmente");

    // Pega a nova célula
    var newCell = this.call.cells[1];
    if (!newCell)
      throw new Error("Migration: chamada processada não é do tipo que migra");

    if (newCell.available()) {
      // Aloca a célula
      newCell.allocateChannel(this.call, this.call.arrivalTime);
      // Lança evento de fim de chamada
      simulation.insertEvent(new EndCall(this.call.endTime(), this.call));
    } else {
      // adiciona a duração de chamada até o momento da perda de sinal
      simulation._durationStat.include(simulation.actualTime.compare(this.call.arrivalTime));
      newCell.missCall();  //incrementador de chamadas perdidas da célula
      simulation.decrementCalls(); // decrementa o número de chamadas no sistema
    }
  }
}

/**
 * Evento de fim de chamada
 */
export class EndCall extends EventSim {
  call: Call;

  constructor(time: Time, call: Call) {
    super(time);
    this.call = call;
    this.name = "Fim de chamada";
  }

  process(simulation: Simulation): void {
    var cell = this.call.actualCell;

    if (cell){
      cell.freeChannel(this.call, this.call.endTime());
      simulation.completeCall();   //incrementador de chamadas completadas da célula
      // adiciona a duração de chamada até o momento da perda de sinal
      simulation._durationStat.include(this.call.duration.toSeconds());
      simulation.decrementCalls(); // decrementa o número de chamadas no sistema
    } else
      throw new Error("EndCall: chamada processada não está em nehuma célula atualmente");
  }
}
