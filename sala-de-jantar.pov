#include "colors.inc"
#include "textures.inc"
#include "finish.inc"

// Camera (Window) posicionada em x=0, y=2, z=-5 voltada para
// direção apontada pelo vetor x=0, y=1, z=2.
camera {
    location <0, 2, -5>
    look_at  <0, 1,  2>
}

// Cor de fundo. Nao e objeto de cena.
background { color Blue }

// Fonte de Luz Branca posicionada em x=20, y=4 e z=-13
light_source { <20, 4, -13> color White }

// Chao com textura de madeira
plane { <0, 1, 0>, -1    
    texture { 
         Dark_Wood 
         finish { Shiny } 
         scale 0.1 
    }
}

// Parede extena
plane {
    <0, 0, 1>, 30
    pigment { Red }
}

// Esfera em x=0, y=1, z=2 com raio=2
sphere {
    <0, 1, 2>, 2
    texture {
      pigment {
        White_Marble   // predefinida em textures.inc
        scale 0.4        // fator de escala da textura
      }
      finish { Shiny } // predefinida em finish.inc
    }
  }

// Esfera ao fundo para mais tarde demonstrar efeitos de nevoa
sphere {
    <15, 1, 30>, 2
    texture {
      pigment {Red}
      finish { Shiny }
    }
  }

polygon {
    30,
    <-0.8, 0.0>, <-0.8, 1.0>,    // Letter "P"
    <-0.3, 1.0>, <-0.3, 0.5>,    // outer shape
    <-0.7, 0.5>, <-0.7, 0.0>,
    <-0.8, 0.0>,
    <-0.7, 0.6>, <-0.7, 0.9>,    // hole
    <-0.4, 0.9>, <-0.4, 0.6>,
    <-0.7, 0.6>
    <-0.25, 0.0>, <-0.25, 1.0>,  // Letter "O"
    < 0.25, 1.0>, < 0.25, 0.0>,  // outer shape
    <-0.25, 0.0>,
    <-0.15, 0.1>, <-0.15, 0.9>,  // hole
    < 0.15, 0.9>, < 0.15, 0.1>,
    <-0.15, 0.1>,
    <0.45, 0.0>, <0.30, 1.0>,    // Letter "V"
    <0.40, 1.0>, <0.55, 0.1>,
    <0.70, 1.0>, <0.80, 1.0>,
    <0.65, 0.0>,
    <0.45, 0.0>
    pigment { color rgb <1, 0.3, 0> }

    translate x*2
  }

intersection {
      sphere { <0, 0, 0>, 1
        translate -0.5*x
     }
      sphere { <0, 0, 0>, 1
        translate 0.5*x
      }
      pigment { Red }
      rotate -30*z     // Para vermos o disco meio de lado
      finish { Shiny }
}

// Exemplo de Geometria Construtiva
// Diferenca entre um cilindro e o resultado da intersecção de duas esferas.
/*difference {
    intersection {
      sphere { <0, 0, 0>, 1
        translate -0.5*x
     }
      sphere { <0, 0, 0>, 1
        translate 0.5*x
      }
      pigment { Red }
      rotate 90*y
      finish { Shiny }
     }
    cylinder { <0, 0, -1> <0, 0, 1>, .35
      pigment { Blue }
    }
    rotate -30*y
  } */
