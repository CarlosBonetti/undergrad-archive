#declare FloorTexture = texture {
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