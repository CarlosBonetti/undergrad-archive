/* @flow */

/**
 * Unidade de tempo intercambiável a segundos, minutos etc
 * Internamente guarda o tempo como segundos
 */
export class Time {
  /**
   * Retorna uma nova unidade de tempo representando os segundos informados
   */
  static seconds(seconds: number): Time {
    return new Time(seconds);
  }

  /**
   * Retorna uma nova unidade de tempo representando os minutos informados
   */
  static minutes(minutes: number): Time {
    return new Time(minutes * 60);
  }

  /**
   * Unidade de tempo interna do Time
   */
  _seconds: number;

  constructor(seconds: number) {
    this._seconds = seconds;
  }

  /**
   * Compara o tempo atual com o parâmetro
   * Retorna um valor negativo se o tempo atual for menor do que o parâmetro,
   * 0 se forem iguais e um valor positivo caso o tempo atual for maior
   * do que o parâmetro
   */
  compare(t2: Time): number {
    return this._seconds - t2._seconds;
  }

  /**
   * Soma dois tempos, retornando um novo objeto tempo
   */
  add(t2: Time): Time {
    return Time.seconds(this.toSeconds() + t2.toSeconds());
  }

  /**
   * Retorna o tempo convertido para segundos
   */
  toSeconds(): number {
    return this._seconds;
  }

  toMinutes(): number {
    return this._seconds / 60;
  }

  /**
   * Retorna a unidade de tempo no formato hh:mm:ss
   */
  toTimeString(): string {
    var r = this._seconds;

    var hours = Math.floor(r / 3600);
    r -= hours * 3600;

    var minutes = Math.floor(r / 60);
    r -= minutes * 60;

    var seconds = r;

    var h = hours > 9 ? hours : '0' + hours;
    var m = minutes > 9 ? minutes : '0' + minutes;
    var s = seconds > 9 ? seconds : '0' + seconds;

    var str = h + ':' + m + ':' + s;
    return str.substr(0, 12);
  }
}
