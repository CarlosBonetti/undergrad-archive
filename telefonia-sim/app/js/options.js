import * as Distribution from '../../src/distributions';
import { Time } from '../../src/time';

export class Options extends React.Component {
  constructor(props) {
    super(props);
    this.model = this.props.model;
  }

  render() {
    return (
      <div>
        <div className="row">
          <div className="col-md-6">
            <CellOptionPanel model={this.model} cell={1} />
          </div>
          <div className="col-md-6">
            <CellOptionPanel model={this.model} cell={2} />
          </div>
        </div>
        <div className="row">
          <div className="form-group form-group-sm col-sm-4">
            <label className="control-label">Tempo de simulação (em minutos)</label>
            <input defaultValue={this.model.values.ts.toMinutes()} onChange={this.changeTS.bind(this)} name="ts" type="number" min="0" max="99999" className="form-control" />
          </div>
        </div>
      </div>
    );
  }

  changeTS(e) {
    this.model.values[e.target.name] = Time.minutes(parseFloat(e.target.value));
    this.model.update();
  }
}

export class CallTypeOption extends React.Component {
  render() {
    return (
      <div className="form-group form-group-sm col-sm-4">
        <label className="control-label">{this.props.type} (%)</label>
        <input name={this.props.type} type="number" min="0" max="100" className="form-control" {...this.props} />
      </div>
    );
  }
}

export class CellOptionPanel extends React.Component {
  constructor(props) {
    super(props);
    this.model = this.props.model;
    this.cellType = 'C' + this.props.cell;
    this.dist = this.model.values[this.cellType].tec.constructor.name;
    this.value = this.model.values[this.cellType].tec.value || this.model.values[this.cellType].tec.mean;
  }

  render() {
    return (
      <div className="panel panel-default">
        <div className="panel-heading">
          <h3 className="panel-title">Configurações <strong>Célula {this.props.cell}</strong></h3>
        </div>
        <div className="panel-body">
          <div className="row">
            <CallTypeOption
              onChange={this.change.bind(this)}
              defaultValue={this.model.values[this.cellType][this.cellType + "C1"]}
              type={this.cellType + "C1"} />
            <CallTypeOption
              onChange={this.change.bind(this)}
              defaultValue={this.model.values[this.cellType][this.cellType + "C2"]}
              type={this.cellType + "C2"} />
            <CallTypeOption
              onChange={this.change.bind(this)}
              defaultValue={this.model.values[this.cellType][this.cellType + "FA"]}
              type={this.cellType + "FA"} />
            <p className="col-sm-12 help-block call-type">Distribuição de frequência para cada tipo de chamada com origem nesta célula</p>
          </div>

          <div className="row">
            <div className="form-group form-group-sm col-md-4">
              <label className="control-label">Número de canais</label>
              <input
                defaultValue={this.model.values[this.cellType].channels}
                onChange={this.change.bind(this)}
                name="channels" type="number" min="0" max="9999999" className="form-control" />
            </div>

            <div className="form-group form-group-sm col-md-8">
              <label className="control-label">Tempo entre chamadas (segundos)</label>
              <div className="row">
                <div className="col-sm-6">
                  <select onChange={this.changeTecDist.bind(this)} name="dist" className="form-control" defaultValue={this.dist}>
                    <option value="Exponential">Exponencial</option>
                    <option value="Constant">Constante</option>
                  </select>
                </div>

                <div className="col-sm-6">
                  <input onChange={this.changeTecValue.bind(this)} name="value" defaultValue={this.value} placeholder="Média / Valor" type="number" min="0" max="9999999" className="form-control" />
                </div>
              </div>
            </div>
          </div>

          <div className="form-group form-group-sm">
            <label className="control-label">Duração da chamada (segundos)</label>
            <DistributionOption defaultDistribution={this.model.values[this.cellType].duration} onSet={this.changeDuration.bind(this)} />
          </div>
        </div>
      </div>
    );
  }

  change(e) {
    this.model.values[this.cellType][e.target.name] = parseFloat(e.target.value);
    this.model.update();
  }

  changeTecDist(e) {
    this.dist = e.target.value;
    this.updateTec();
  }

  changeTecValue(e) {
    this.value = parseFloat(e.target.value);
    this.updateTec();
  }

  updateTec() {
    this.model.values[this.cellType].tec = new Distribution[this.dist](this.value);
    this.model.update();
  }

  changeDuration(d) {
    this.model.values[this.cellType].duration = d;
    this.model.update();
  }
}

export class DistributionOption extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      selected: this.props.defaultDistribution.constructor.name
    };

    this.values = {
      c1: this.props.defaultDistribution.value,
      u1: this.props.defaultDistribution.min,
      u2: this.props.defaultDistribution.max,
      t1: this.props.defaultDistribution.min,
      t2: this.props.defaultDistribution.mode,
      t3: this.props.defaultDistribution.max,
      e1: this.props.defaultDistribution.mean,
      n1: this.props.defaultDistribution.mean,
      n2: this.props.defaultDistribution.std,
    };
  }

  render() {
     return (
       <div className="row">
           <div className="col-sm-3">
             <select className="form-control" value={this.state.selected} onChange={this.change.bind(this)}>
               <option value="Constant">Constante</option>
               <option value="Uniform">Uniforme</option>
               <option value="Triangular">Triangular</option>
               <option value="Exponential">Exponencial</option>
               <option value="Normal">Normal</option>
             </select>
          </div>

          <div ref="inputs">
            {this.state.selected == 'Constant' && this.renderConstant()}
            {this.state.selected == 'Uniform' && this.renderUniform()}
            {this.state.selected == 'Triangular' && this.renderTriangular()}
            {this.state.selected == 'Exponential' && this.renderExponential()}
            {this.state.selected == 'Normal' && this.renderNormal()}
          </div>
      </div>
    );
   }

   change(e) {
     this.setState({
       "selected": e.target.value
     });
   }

   changeValues(e) {
     this.values[e.target.name] = e.target.value;
     this.update();
   }

   update() {
     var d;

     switch(this.state.selected) {
       case 'Constant':
        d = new Distribution.Constant(parseFloat(this.values.c1));
        break;
       case 'Uniform':
        d = new Distribution.Uniform(parseFloat(this.values.u1), parseFloat(this.values.u2));
        break;
       case 'Triangular':
        d = new Distribution.Triangular(parseFloat(this.values.t1), parseFloat(this.values.t2), parseFloat(this.values.t3));
        break;
       case 'Exponential':
        d = new Distribution.Exponential(parseFloat(this.values.e1));
        break;
       case 'Normal':
        d = new Distribution.Normal(parseFloat(this.values.n1), parseFloat(this.values.n2));
        break;
     }

     if (this.props.onSet) {
       this.props.onSet(d);
     }
   }

   renderConstant() {
     return (
       <div className="col-sm-3">
         <input defaultValue={this.values.c1} name="c1" onChange={this.changeValues.bind(this)} placeholder="Valor" type="number" min="0" max="99999999" className="form-control" />
       </div>
     );
   }

   renderUniform() {
     return (
       <div>
         <div className="col-sm-3">
           <input name="u1" defaultValue={this.values.u1} onChange={this.changeValues.bind(this)} placeholder="Mínimo" type="number" min="0" max="99999999" className="form-control" />
         </div>
         <div className="col-sm-3">
           <input name="u2" defaultValue={this.values.u2} onChange={this.changeValues.bind(this)} placeholder="Máximo" type="number" min="0" max="99999999" className="form-control" />
         </div>
       </div>
     );
   }

   renderTriangular() {
     return (
       <div>
         <div className="col-sm-3">
           <input name="t1" defaultValue={this.values.t1} onChange={this.changeValues.bind(this)} placeholder="Mínimo" type="number" min="0" max="99999999" className="form-control" />
         </div>
         <div className="col-sm-3">
           <input name="t2" defaultValue={this.values.t2} onChange={this.changeValues.bind(this)} placeholder="Moda" type="number" min="0" max="99999999" className="form-control" />
         </div>
         <div className="col-sm-3">
           <input name="t3" defaultValue={this.values.t3} onChange={this.changeValues.bind(this)} placeholder="Máximo" type="number" min="0" max="99999999" className="form-control" />
         </div>
       </div>
     );
   }

   renderExponential() {
     return (
       <div className="col-sm-3">
         <input name="e1" defaultValue={this.values.e1} onChange={this.changeValues.bind(this)} placeholder="Média" type="number" min="0" max="99999999" className="form-control" />
       </div>
     );
   }

   renderNormal() {
     return (
       <div>
         <div className="col-sm-3">
           <input name="n1" defaultValue={this.values.n1} onChange={this.changeValues.bind(this)} placeholder="Média" type="number" min="0" max="99999999" className="form-control" />
         </div>
         <div className="col-sm-3">
           <input name="n2" defaultValue={this.values.n2} onChange={this.changeValues.bind(this)} placeholder="Desvio Padrão" type="number" min="0" max="99999999" className="form-control" />
         </div>
       </div>
     );
   }
}
