//=========================================
// Old chair
// -----------------------------------------
// Made for Persistence of vision 3.6
//==========================================  
// Copyright 2001-2004 Gilles Tran http://www.oyonale.com
// -----------------------------------------
// This work is licensed under the Creative Commons Attribution License. 
// To view a copy of this license, visit http://creativecommons.org/licenses/by/2.0/ 
// or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA.
// You are free:
// - to copy, distribute, display, and perform the work
// - to make derivative works
// - to make commercial use of the work
// Under the following conditions:
// - Attribution. You must give the original author credit.
// - For any reuse or distribution, you must make clear to others the license terms of this work.
// - Any of these conditions can be waived if you get permission from the copyright holder.
// Your fair use and other rights are in no way affected by the above. 
//==========================================  
// This chair was used in "Lilith". It is inspired from
// the chair featured in several paintings of Vermeer (minus the lions' heds)
//-----------------------------------------

//#version 3.1;
#include "colors.inc"                                        

#declare C_WoodChair=rgb<31,10,8>*1.4/255;
#declare C_CanvasChair=color rgb <0.1,0.491,0.598>;
#declare C_Nail=color Bronze;
#declare T_NailChair=texture{
        pigment{C_Nail}
        finish{metallic brilliance 5 reflection 0.1 ambient 0.01 diffuse 0.5 specular 0.4 roughness 0.01}
}           
#declare T_WoodChair=texture{
        pigment{
                wood
                turbulence 0.1
                color_map{
                        [0 C_WoodChair*0.2]
                        [0.11 C_WoodChair]
                        [0.6 C_WoodChair*1.5]
                        [1 C_WoodChair*0.4]
                }
        }   
        normal{bozo 0.01 scale 0.001}
        finish{ambient 0.00 diffuse 0.9 specular 0.5 roughness 0.03}
        rotate x*90
        rotate x*10
        rotate z*10
        scale 0.02
        translate 10
}                                   
#declare T_CanvasChair=texture{
        pigment{
                agate
                color_map{
                        [0 C_CanvasChair*0.2]
                        [0.11 C_CanvasChair*0.6]
                        [0.2 C_CanvasChair*1.5]
                        [1 C_CanvasChair*0.4]
                }
        }                         
        normal{quilted 0.5 turbulence 0.2 scale 0.1*<1,1,1>}
        finish{ambient 0.001 diffuse 0.5 specular 0.04 roughness 0.1}
        rotate z*45
        scale 0.4
}                                   

#declare T_CanvasPillow=texture{
        pigment{
                agate
                color_map{
                        [0 C_CanvasChair*0.2]
                        [0.11 C_CanvasChair*0.6]
                        [0.2 C_CanvasChair*1.5]
                        [1 C_CanvasChair*0.4]
                }
        }                         
        normal{quilted 1 turbulence 0.1 scale 0.1*<1,1,1>}
        finish{ambient 0.001 diffuse 0.5 specular 0.04 roughness 0.1}
        rotate y*45
        scale 0.5
}                                   
#declare LegChair1=lathe{
	cubic_spline
	26,
	<0.051,-0.005>,	<0.039,0.000>,	<0.041,0.012>,	<0.049,0.017>,	<0.051,0.026>,
	<0.055,0.042>,	<0.042,0.074>,	<0.057,0.099>,	<0.052,0.109>,	<0.062,0.114>,
	<0.067,0.123>,	<0.063,0.135>,	<0.071,0.141>,	<0.079,0.191>,	<0.072,0.226>,
	<0.063,0.264>,	<0.056,0.292>,	<0.045,0.320>,	<0.044,0.354>,	<0.043,0.376>,
	<0.046,0.390>,	<0.050,0.403>,	<0.060,0.419>,	<0.056,0.438>,	<0.055,0.454>,
	<0.055,0.458>
	scale 0.1/0.454
	scale <2,1,2>
}                                                      
#declare rLegChair=0.079*0.1/0.454;
#declare rLegChair=0.079*1.5*0.1/0.454;
#declare LegChair2=superellipsoid{<0.2,0.2> scale <1,0.5,1> translate y*0.5 scale <rLegChair,0.1,rLegChair>}
#declare LegChair3 = lathe{
	cubic_spline
	10,
	<0.031,-0.003>,	<0.032,0.000>,	<0.035,0.012>,	<0.038,0.020>,	<0.045,0.029>,
	<0.054,0.044>,	<0.049,0.064>,	<0.029,0.081>,	<0.026,0.085>,	<0.013,0.092>
	scale 0.05/0.085
	scale <0.8,1,0.8>
}
#declare yLegChair=0.45;
#declare LegChair=union{
        object{LegChair3}
        object{LegChair2 translate y*0.05}
        object{LegChair1 rotate x*180 translate y*0.25}
        object{LegChair2 translate y*0.25}
        object{LegChair1 translate y*0.35}
        scale yLegChair/0.45
}                        
#declare xChair=yLegChair*1.1;
#declare zChair=xChair;
#declare RodChair1=cylinder{0,xChair*x,rLegChair}
#declare RodChair2=cylinder{0,xChair*x,rLegChair}

#declare RodChair1 = union{
        cylinder{0,x*xChair,rLegChair*0.3 translate y*0.03 translate z*0.01}
        prism{
        	cubic_spline
        	0,
        	1,
        	26,
        	<0.050,0.>,	<0.,0.010>,	<0.,0.060>,	<0.050,0.080>,	<0.100,0.100>,
        	<0.070,0.050>,	<0.110,0.050>,	<0.150,0.070>,	<0.170,0.080>,	<0.190,0.090>,
        	<0.170,0.040>,	<0.230,0.080>,	<0.290,0.040>,	<0.270,0.090>,	<0.290,0.080>,
        	<0.310,0.070>,	<0.350,0.050>,	<0.390,0.050>,	<0.360,0.100>,	<0.410,0.080>,
        	<0.460,0.060>,	<0.460,0.010>,	<0.410,0.>,	<0.050,0.>,	<0.,0.010>,<0.,0.060>
        	translate <-0.23,-0.5,-0.05>
        	scale <1,1,-1>
        	scale <xChair*0.5/0.23,rLegChair*0.7,xChair*0.3/0.23>
        	rotate x*-90                               
        	translate <xChair*0.5,0,rLegChair*0.5>
	}
	
}
#declare RodChair2= prism{
	cubic_spline
	0,
	xChair,
	12,
	<0.005,0.006>,	<0.010,0.026>,	<0.020,0.033>,	<0.019,0.073>,	<0.007,0.082>,	<0.005,0.106>,
	<0.042,0.103>,	<0.031,0.046>,	<0.038,0.007>,	<0.005,0.006>,	<0.010,0.026>,	<0.020,0.033>
	translate <-0.021,0,-0.053>
	scale <1,1,-1>
	scale <0.7*rLegChair/0.021,1,0.3*rLegChair/0.021>
	rotate z*-90
	rotate -x*90
}
#declare yEl=0.02;
#declare elBack=superellipsoid{<0.4,0.4> scale <1,0.5,1> translate y*0.5 scale <rLegChair,0.1,rLegChair>
        clipped_by{box{<-rLegChair*1.2,0.029,-rLegChair*1.2>,<rLegChair*1.2,0.071,rLegChair*1.2>}}
        scale <1,0.1/0.04,1>
        translate -y*0.03        
        }                       
#declare elBackEnd=superellipsoid{<0.4,0.4> scale <1,0.5,1> translate y*0.5 scale <rLegChair,0.1,rLegChair>
        clipped_by{box{<-rLegChair*1.2,0.029,-rLegChair*1.2>,<rLegChair*1.2,0.2,rLegChair*1.2>}}
        scale <1,0.1/0.04,1>
        translate -y*0.03        
        }                       
#declare NailChair=sphere{0,rLegChair*0.5 scale <1,1,0.5>}
#declare RodBack=superellipsoid{<0.3,0.3> scale <xChair*0.5,rLegChair,rLegChair*0.8>}
#declare elPillowBack=difference{
        superellipsoid{<0.5,0.5>}
        plane{z,0 inverse}
        scale <xChair*0.5,rLegChair,rLegChair*1>
}

#declare nEl=25;
#declare yEl=0.03;
#declare BackChair=union{
        #declare i=0;          
        #declare posEl=<0,0,0>;
        #declare aEl=0;
        #while (i<nEl)                                           
                #if (mod(i,3)=0)
                        union{
                                object{NailChair rotate -y*90 translate -x*(xChair*0.5+rLegChair)}
                                object{NailChair rotate -y*90 translate x*(xChair*0.5+rLegChair)}
                                rotate x*aEl translate posEl 
                                texture{T_NailChair}
                        }
                #end
                #if (i<nEl-1)
                        union{
                                object{elBack scale <1,yEl/0.1,1> translate -x*xChair*0.5}
                                object{elBack scale <1,yEl/0.1,1> translate x*xChair*0.5}
                                rotate x*aEl translate posEl 
                        }
                #else
                        union{
                                object{elBackEnd scale <1,yEl/0.1,1> translate -x*xChair*0.5}
                                object{elBackEnd scale <1,yEl/0.1,1> translate x*xChair*0.5}
                                rotate x*aEl translate posEl 
                        }
                #end
                #if (i=11 | i=23)
                        object{RodBack rotate x*aEl translate posEl texture{T_WoodChair rotate z*100}}
                #end
                #declare posEl=posEl+<0,yEl*cos(radians(aEl)),yEl*sin(radians(aEl))>;
                #declare aEl=aEl+0.28;
                #declare i=i+1;
        #end

        #declare i=0;          
        #declare posEl=<0,0,0>;
        #declare aEl=0;
        union{
                #while (i<nEl)                                                       
                
                        #if (i>11 & i<23)
                                object{elPillowBack rotate x*aEl translate posEl}
                        #end
                        #declare posEl=posEl+<0,yEl*cos(radians(aEl)),yEl*sin(radians(aEl))>;
                        #declare aEl=aEl+0.25;
                        #declare i=i+1;
                #end
                texture{T_CanvasChair}        
        }

} 
                                  
#declare xNailsPillow=union{
                #declare i=0;
                #while (i<7)
                        object{NailChair translate x*xChair*i/6 translate -z*rLegChair} //*0.1*sin(pi*i/6)}
                #declare i=i+1;
                #end
                translate -x*xChair*0.5
       }                          

#declare zNailsPillow=union{
                #declare i=0;
                #while (i<6)
                        object{NailChair translate x*zChair*i/6 translate -z*rLegChair} //*0.1*sin(pi*i/6)}
                #declare i=i+1;
                #end
                translate -x*zChair*0.5
                
       }                          

#declare Pillow=union{    
        union{
                object{xNailsPillow translate -z*zChair*0.5}
                object{xNailsPillow scale <1,1,-1> translate z*zChair*0.5}
                object{zNailsPillow rotate y*-90 scale <-1,1,1> translate -x*xChair*0.5}
                object{zNailsPillow rotate y*-90 translate x*xChair*0.5}
                translate y*rLegChair*0.25
                texture{T_NailChair}
        }                             
        
        difference{
                difference{superellipsoid{<0.4,0.4>} plane{y,0} scale <xChair*0.5,rLegChair*2.5,zChair*0.5>*1.02}
                plane{y,0}
                texture{T_CanvasPillow}
                translate -y*0.01
        }
        
        superellipsoid{<0.3,0.3> scale rLegChair*1.1 translate <-xChair,0,-zChair>*0.5}
        superellipsoid{<0.3,0.3> scale rLegChair*1.1 translate <-xChair,0,zChair>*0.5}
        superellipsoid{<0.3,0.3> scale rLegChair*1.1 translate <xChair,0,-zChair>*0.5}
        superellipsoid{<0.3,0.3> scale rLegChair*1.1 translate <xChair,0,zChair>*0.5}

        superellipsoid{<0.3,0.3> scale <xChair*0.5,rLegChair,rLegChair> translate -z*zChair*0.5}
        superellipsoid{<0.3,0.3> scale <xChair*0.5,rLegChair,rLegChair> translate z*zChair*0.5}

        superellipsoid{<0.3,0.3> scale <zChair*0.5,rLegChair,rLegChair> rotate -y*90 translate -x*xChair*0.5}
        superellipsoid{<0.3,0.3> scale <zChair*0.5,rLegChair,rLegChair> rotate -y*90 translate x*xChair*0.5}

}
#declare Chair=union{                                                
        union{
                object{BackChair translate z*zChair*0.5}
                object{Pillow}
                translate y*(yLegChair+rLegChair*0.5)
                texture{T_WoodChair}
        }                            
        union{
                object{LegChair translate <xChair,0,zChair>*0.5}
                object{LegChair translate <xChair,0,-zChair>*0.5}
                object{LegChair translate <-xChair,0,zChair>*0.5}
                object{LegChair translate <-xChair,0,-zChair>*0.5}
                texture{T_WoodChair}
        }
        union{
                object{RodChair1 translate <-xChair*0.5,0.1,-zChair*0.5>}
                object{RodChair1 scale <1,1,-1> translate <-xChair*0.5,0.1,zChair*0.5>}
                object{RodChair2 translate <-xChair*0.5,0.3,-zChair*0.5>}
                object{RodChair2 scale <1,1,-1> translate <-xChair*0.5,0.3,zChair*0.5>}
                texture{T_WoodChair rotate x*90}
        }                
        union{
                
                object{RodChair1 scale <zChair/xChair,1,1> rotate y*-90 scale <-1,1,1> translate <-xChair*0.5,0.1,-zChair*0.5>}
                object{RodChair1 scale <zChair/xChair,1,1> rotate y*-90 translate <xChair*0.5,0.1,-xChair*0.5>}
                object{RodChair2 scale <zChair/xChair,1,1> rotate y*-90 scale <-1,1,1> translate <-xChair*0.5,0.3,-zChair*0.5>}
                object{RodChair2 scale <zChair/xChair,1,1> rotate y*-90 translate <xChair*0.5,0.3,-zChair*0.5>}
                texture{T_WoodChair rotate z*90}
                
        }
}
                                                                

