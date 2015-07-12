#include "colors.inc"
#include "textures.inc"
#include "finish.inc"            

#include "chair.pov"      

global_settings{assumed_gamma 1.0 radiosity{recursion_limit 1}}

background{White}          

light_source{
    -z*6 color White
    area_light 1*x,1*z, 10,10 jitter adaptive 1 orient
    rotate x*45
    rotate -y*30
}

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

object{
    Chair 
    rotate -y * 45
    translate <0,0,0>
}
