/* global React, ReactDOM */

import { Options } from './js/options';
import { Simulation } from '../src/simulation';
import { Time } from '../src/time';
import * as Distribution from '../src/distributions';

class EventTable extends React.Component {

  render() {
    return (
      <table className="table">
        <thead>
          <tr>
            <th width="120">Tempo chegada (hh:mm:ss)</th>
            <th>Evento</th>
            <th>Chamada</th>
            <th>Duração<br /> chamada (hh:mm:ss)</th>
          </tr>
        </thead>
        <tbody>
          {this.props.calendar._array.map((event, index) => {
            var activeClass = index == this.props.calendar._actualIndex ? 'success' : '';
            return (
              <tr key={index} className={activeClass}>
                <td>{event.time.toTimeString()}</td>
                <td>{event.name}</td>
                <td>
                  {event.call && 'Chamada #' + event.call.index + ' (' + event.call.type() + ')'}
                </td>
                <td>
                  {event.call && event.call.duration.toTimeString()}
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    );
  }
}

class Stats extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div>
        <p><strong>Tempo atual da simulação: </strong> {this.props.stats.tempoAtual}</p>

        <h4 className="stat-title">Geral</h4>
        <div className="row">
          <p className="col-md-6">
            <strong>Número de chamadas no sistema</strong>
            <span className="sub-item">Atual: {this.props.stats.chamadas.actual}</span>
            <span className="sub-item">Média: {this.props.stats.chamadas.mean}</span>
            <span className="sub-item">Mínimo: {this.props.stats.chamadas.min}</span>
            <span className="sub-item">Máximo: {this.props.stats.chamadas.max}</span>
          </p>
          <p className="col-md-6">
            <strong>Duração das chamadas</strong>
            <span className="sub-item">Médio: {this.props.stats.callDuration.mean} segundos</span>
            <span className="sub-item">Mínimo: {this.props.stats.callDuration.min} segundos</span>
            <span className="sub-item">Máximo: {this.props.stats.callDuration.max} segundos</span>
            <span className="sub-item">Total: {this.props.stats.callDuration.total} (hh:mm:ss)</span>
          </p>
        </div>
        <p><strong>Chamadas completadas:</strong> {this.props.stats.completadas}</p>
        <p><strong>Chamadas perdidas por FA:</strong> {this.props.stats.lostFA}</p>

        <h4 className="stat-title">Célula 1</h4>
        <p><strong>Canais: </strong> {this.props.stats.c1.contCanais} / {this.props.stats.c1.maxCanais} (em uso / máximo)</p>
        <p><strong>Chamadas perdidas por falta de canal: </strong> {this.props.stats.c1.lost} </p>
        <p><strong>Taxa média de ocupação:</strong> {100 * this.props.stats.c1.mediaUso / this.props.stats.c1.maxCanais} %</p>

        <h4 className="stat-title">Célula 2</h4>
        <p><strong>Canais: </strong> {this.props.stats.c2.contCanais} / {this.props.stats.c2.maxCanais} (em uso / máximo)</p>
        <p><strong>Chamadas perdidas por falta de canal: </strong> {this.props.stats.c2.lost} </p>
        <p><strong>Taxa média de ocupação:</strong> {100 * this.props.stats.c2.mediaUso / this.props.stats.c2.maxCanais} %</p>
      </div>
    );
  }
}

class SimulationView extends React.Component {
  constructor(props) {
    super(props);
    this.reiniciar(false);
    this.state = {
      calendar: this.simulation.calendar,
      finished: false,
      stats: {
        actualTime: '-',
        callDuration: {},
        chamadas: {},
        c1: {
          lost: 0
        },
        c2: {
          lost: 0
        }
      }
    };
  }

  componentDidMount() {
    this.update();
  }

  render() {
    return (
      <div className="row">
        <div className="col-md-6">
          <div className="btn-group">
            <button
              className="btn btn-default"
              title="Processa o próximo evento do calendário"
              onClick={this.handleStep.bind(this)}
              disabled={this.state.finished}>Processar próximo evento</button>
            <button
              className="btn btn-default"
              onClick={this.handleProcessarTudo.bind(this)}
              disabled={this.state.finished}>Processar tudo</button>
            <button className="btn btn-default" onClick={this.reiniciar.bind(this)}>Reiniciar</button>
          </div>

          <p>Status: <span className="label label-primary">{this.state.finished ? 'Terminado!' :  'Simulando...'}</span></p>

          <h3>Estatísticas</h3>
          <Stats stats={this.state.stats} />
        </div>

        <h3>Calendário de eventos</h3>
        <div className="col-md-6 calendar">
          <EventTable calendar={this.state.calendar} />
        </div>
      </div>
    );
  }

  update() {
    this.setState({
      calendar: this.simulation.calendar,
      finished: this.simulation.finished(),
      stats: {
        tempoAtual: this.simulation.actualTime.toTimeString(),
        completadas: this.simulation._completedCalls,
        lostFA: this.simulation.cells[2]._missCalls,
        chamadas: {
          actual: this.simulation._callsStat.actual() || 0,
          mean: this.simulation._callsStat.mean(this.simulation.actualTime) || 0,
          min: this.simulation._callsStat.min() || 0,
          max: this.simulation._callsStat.max() || 0,
        },
        callDuration: {
          mean: this.simulation._durationStat.mean() || 0,
          min: this.simulation._durationStat.min() || 0,
          max: this.simulation._durationStat.max() || 0,
          total: Time.seconds(this.simulation._durationStat.total()).toTimeString() || 0
        },
        c1: {
          maxCanais: this.simulation.cells[0]._channels,
          contCanais: this.simulation.cells[0]._usedChannels,
          lost: this.simulation.cells[0]._missCalls,
          mediaUso: this.simulation.cells[0]._useStats.mean(this.simulation.actualTime),
        },
        c2: {
          maxCanais: this.simulation.cells[1]._channels,
          contCanais: this.simulation.cells[1]._usedChannels,
          lost: this.simulation.cells[1]._missCalls,
          mediaUso: this.simulation.cells[1]._useStats.mean(this.simulation.actualTime),
        }
      }
    });
  }

  reiniciar(update = true) {
    var config = this.props.config;
    this.simulation = new Simulation(config.ts, config.C1, config.C2);
    this.simulation.start();
    if (update)
      this.update();
  }

  handleStep() {
    this.simulation.step();
    this.update();
  }

  handleProcessarTudo() {
    while(!this.simulation.finished()) {
      this.simulation.step();
    }
    this.update();
  }
}

class App extends React.Component {
  constructor() {
    super();

    this.model = {
      values: {
        ts: Time.minutes(10),
        C1: {
          channels: 15,
          'C1C1': 50,
          'C1C2': 30,
          'C1FA': 20,
          'tec': new Distribution.Constant(4),
          'duration': new Distribution.Triangular(60, 120, 180)
        },
        C2: {
          channels: 30,
          'C2C1': 30,
          'C2C2': 50,
          'C2FA': 20,
          'tec': new Distribution.Exponential(5),
          'duration': new Distribution.Normal(90, 30)
        },
      },
      update: function() {
        // console.log(this);
      }
    };

    this.state = {
      optionsVisible: true,
      simulation: null
    }
  }

  render() {
    return (
      <div><br />
        {this.state.optionsVisible ?
          <div>
            <p>
              <strong>Alunos: </strong>
              Carlos Bonetti - 12100739 (<a target="_blank" href="mailto: carlosbonetti.mail@gmail.com">carlosbonetti.mail@gmail.com</a>)
              | Rodrigo Aguiar Costa - 12104064 (<a target="_blank" href="mailto: rodrigoacosta02@gmail.com">rodrigoacosta02@gmail.com</a>)
              <strong className="pull-right">INE5425 - Modelagem e Simulação, UFSC, 2015/2</strong>
            </p>
            <p>
              <strong>Instruções: </strong>Preencha os valores de entrada de simulação e pressione "Iniciar Simulação" para iniciar. Utilize '.' para casas decimais. Utilize 'segundos' como unidade de medida de tempo, a não ser onde informado o contrário. <a target="_blank" href="report.pdf">Download do relatório</a> | <a target="_blank" href="manual.pdf">Download do manual de uso</a> | <a target="_blank" href="https://github.com/CarlosBonetti/telefonia-sim">Código fonte desta aplicação</a>
            </p>
            <Options model={this.model} />
            <button onClick={this.init.bind(this)} className="btn btn-primary btn-block">Iniciar simulação</button>
          </div>
        :
          <div>
            <a href="#" onClick={this.showOptions.bind(this)}>Entrar com novos valores</a>
            <SimulationView config={this.state.config} />
          </div>
        }
      </div>
    );
  }

  init() {
    console.log(this.model.values);

    this.setState({
      config: this.model.values,
      optionsVisible: false
    });
  }

  showOptions() {
    this.setState({ optionsVisible: true });
  }
}

ReactDOM.render(<App />, document.getElementById('react-mount'));
