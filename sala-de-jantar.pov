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
    pigment { 
        wood color_map { [0 rgb <.9,.7,.3>] [1 rgb <.6,.3,.1>] }
        turbulence .5
        scale <1, 1, 20>*.2
    }
    finish { specular 1 }
    normal { 
        gradient x 1
        slope_map { 
            [0 <0, 1>] // 0 height, strong slope up
            [.05 <1, 0>] // maximum height, horizontal
            [.95 <1, 0>] // maximum height, horizontal
            [1 <0, -1>] // 0 height, strong slope down
        }
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
