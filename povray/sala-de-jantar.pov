#include "colors.inc"
#include "textures.inc"
#include "finish.inc"            

#include "chair.pov"  
#include "table.pov"    
#include "FloorTexture.inc"

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
    location <3, 2, -7>
    look_at  <0, 1,  2>
}

// Cor de fundo. Nao e objeto de cena.
background { color Blue } 
                                                         
// Chao com textura de madeira
plane { <0, 1, 0>, -1    
    texture { FloorTexture }
}

// Parede extena
plane {
    <0, 0, 1>, 20
    pigment { Red }
} 

object { 
    TableCompl
    scale 0.028
    translate <0, -1, 0>  
    scale <2, 0, 1.5>
}                      

object {
    Chair 
    rotate y * 180
    translate <-1,-0.5,-0.8>
    scale <2,2,2>
}        