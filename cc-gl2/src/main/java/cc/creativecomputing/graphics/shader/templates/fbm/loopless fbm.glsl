

outtype fbm4( vec2 p ){
	outtype f = 0.0;
    f += 0.5000 * noise(p); p = p * 2.02;
    f += 0.2500 * noise(p); p = p * 2.03;
    f += 0.1250 * noise(p); p = p * 2.01;
    f += 0.0625 * noise(p);
    return f / 0.9375;
}

outtype fbm6( vec2 p ){
	outtype f = 0.0;
    f += 0.500000 * (0.5 +0.5 * noise(p)); p = p * 2.02;
    f += 0.250000 * (0.5 +0.5 * noise(p)); p = p * 2.03;
    f += 0.125000 * (0.5 +0.5 * noise(p)); p = p * 2.01;
    f += 0.062500 * (0.5 +0.5 * noise(p)); p = p * 2.04;
    f += 0.031250 * (0.5 +0.5 * noise(p)); p = p * 2.01;
    f += 0.015625 * (0.5 +0.5 * noise(p));
    return f / 0.96875;
}
