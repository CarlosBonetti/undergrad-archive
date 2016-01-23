/* @flow */

/**
 * Distribuições de probabilidade
 * 	Normal, exponencial, constante etc
 *
 * Variáveis aleatórias de cada distribuição são calculadas utilizando o
 * método da transformação inversa
 *
 * Cada distribuição pode ser usada da seguinte maneira:
 *
 * 		var expo = Exponential(2.45); // 2.45 é a média da distribuição
 * 		expo.get(r); // Retorna um valor da distribuição calculado com base no
 * 							   // valor aleatório `r`, sendo que 0 <= r <= 1
 */

/**
 * Classe base para as distribuições de probabilidade
 */
export class Distribution {
  get(r: number) : number {
    throw new Error("get() must be defined");
  }
}

/**
 * Distribuição constante. Retorna sempre o mesmo valor
 */
export class Constant extends Distribution {
  value: number;

  constructor(value: number) {
    super();
    this.value = value;
  }

  get(r?: number): number {
    return this.value;
  }
}

/**
 * Distribuição uniforme. Retorna valores uniformemente distribuídos
 * entre os valores mínimo e máximo estipulados
 */
export class Uniform extends Distribution {
  /**
   * Valor mínimo da distribuição
   */
  min: number;

  /**
   * Valor máximo da distribuição
   */
  max: number;

  constructor(min: number, max: number) {
    super();
    this.min = min;
    this.max = max;
  }

  get(r: number): number {
    return this.min + (this.max - this.min) * r;
  }
}

/**
 * Distribuição triangular. Retorna valores distribuídos com moda e limites
 * definido a partir dos parâmetros
 */
export class Triangular extends Distribution {
  /**
   * Limite inferior da distribuição
   */
  min: number;

  /**
   * Limite superior da distribuição
   */
  max: number;

  /**
   * Moda da distribuição
   */
  mode: number;

  /**
   * Proporção da distribuição, usada para geração de um valor da distribuição
   */
  proportion: number;

  constructor(min: number, mode: number, max: number) {
    super();
    this.min = min;
    this.mode = mode;
    this.max = max;
    this.proportion = (this.mode - this.min) / (this.max - this.min);
  }

  get(r: number): number {
    if (r <= this.proportion)
      return this.min + Math.sqrt(r * (this.mode - this.min) * (this.max - this.min));
    else
      return this.max - Math.sqrt((1 - r) * (this.max - this.mode) * (this.max - this.min));
  }
}

/**
 * Distribuição exponencial (de Poisson)
 */
export class Exponential extends Distribution {
  /**
   * Média da distribuição. Representa o número de ocorrências esperado por
   * unidade de tempo
   */
  mean: number;

  constructor(mean : number) {
    super();
    this.mean = mean;
  }

  get(r: number) : number {
    return -this.mean * Math.log(1 - r);
  }
}

/**
 * Distribuição Normal
 */
export class Normal extends Distribution {
  /**
   * Média da distribuição normal
   */
  mean: number;

  /**
   * Desvio padrão da distribuição normal
   */
  std: number;

  constructor(mean: number, std: number) {
    super();
    this.mean = mean;
    this.std = std;
  }

  get(r1: number, r2?: number = Math.random()): number {
    return this.get1(r1, r2);
  }

  get1(r1: number, r2: number): number {
    return this.mean + this.std * this.z1(r1, r2);
  }

  get2(r1: number, r2: number): number {
    return this.mean + this.std * this.z2(r1, r2);
  }

  z1(r1: number, r2: number): number {
    return Math.sqrt(-2 * Math.log(r1)) * Math.cos(2 * Math.PI * r2);
  }

  z2(r1: number, r2: number): number {
    return Math.sqrt(-2 * Math.log(r1)) * Math.sin(2 * Math.PI * r2);
  }
}
