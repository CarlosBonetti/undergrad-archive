//=================COPYRIGHT ISSUES===============================================================
//This model is Let-me-know-ware, meaning that :
//You can use it without prior asking, but you MUST tell me
//that you used it (if you publish online, offline,...).
//You must also mention my name somewhere in the sources...
//Author: MOSEN Fabien  (MichelMosen@compuserve.com) 
//=================================================================================
//      A HEAVY WOODEN TABLE
//=================================================================================
//Frank Lloyd-looking table, 69 cm high, 70 cm wide, 160 cm long
//centered on X and Z axis, standing on y,0 floor
                          
#include "colors.inc"
#include "woods.inc" // #include "mestex.inc"
                          
#declare TableWood=texture {T_Wood23}
//allows you to chose any wooden texture you like.
//However, scaling and rotations will still apply to it
//the veins are carefully aligned in this model

#declare PiedTable =
  union {
         sphere {<0,2.5,0>,2.5 texture {TableWood scale 10 rotate <45,45,45>}}
         difference {
                intersection {
                        cylinder {<0,15.875,-1.5>,<0,15.875,1.5>,10.875}
                        box {<-7.5,5,-1.6>,<7.5,15,1.6>}
                        }
                box {<-.5,10,2>,<.5,5,-2>}
                }
        box {<2.5,8,-1.5>,<5,65,-10>}
        box {<-2.5,8,-1.5>,<-5,65,-10>}

        texture {TableWood scale 10 rotate x*90}
        }

#declare PiedTable2 =
        union {
                object {PiedTable translate z*23}
                object {PiedTable scale <1,1,-1> translate z*-23}
                box {<5,8,21.5>,<7.5,22,-21.5> texture {TableWood scale 10}}
                box {<-5,8,21.5>,<-7.5,22,-21.5> texture {TableWood scale 10}}
                box {<2.5,15,23.5>,<-2.5,27,-23.5> texture {TableWood scale 10}}
                box {<2.5,47,30>,<-2.5,52,-30> texture {TableWood scale 10}}
                box {<2.5,60,30>,<-2.5,65,-30> texture {TableWood scale 10}}

                texture {TableWood scale 10 rotate x*90}
}

#declare PoutreTable =
   difference {
        intersection {
                cylinder {<0,235.5,1.5>,<0,235.5,-1.5>,235.5}
                box {<60,0,-1.6>,<-60,15,1.6>}
                }
        cylinder {<0,7,2>,<0,7,-2>,3}

        texture {TableWood scale 10 rotate y*90}
 }

#declare TableCompl =
        union {
                box {<80,69,35>,<-80,65,-35> texture {TableWood scale 10 rotate y*90}}
                object {PiedTable2 translate x*50}
                object {PiedTable2 translate x*-50}
                object {PoutreTable translate <0,51,13>}
                object {PoutreTable translate <0,51,-13>}

                finish {reflection .6}
                }