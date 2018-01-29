// Gold Noise ©2017 dcerisano@standard3d.com
//  - based on the golden ratio, PI, and the square root of two
//  - faster one-line fractal noise generator function
//  - improved random distribution
//  - works with all chipsets (including low precision)
//  - gpu-optimized floating point operations (faster than integer)
//  - does not contain any slow division or unsupported bitwise operations

// Use mediump or highp for improved random distribution.
// This line can be removed for low precision chipsets and older GL versions.

// precision highp   float;
// precision mediump float;
   precision lowp    float;

// Irrationals with precision shifting 
//
float PHI = 1.61803398874989484820459 * 00000.1; // Golden Ratio   
float PI  = 3.14159265358979323846264 * 00000.1; // PI
float SRT = 1.41421356237309504880169 * 10000.0; // Square Root of Two


// Gold Noise function
//
float gold_noise(in vec2 coordinate, in float seed)
{
    return fract(sin(dot(coordinate*seed, vec2(PHI, PI)))*SRT);
}