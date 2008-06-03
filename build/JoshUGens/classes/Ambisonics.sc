// A series of BFormat UGens for SC3. UGens by Josh Parmenter 4-3-2004
// UGen plug-ins are based on James McCartney's Pan2B and DecodeB2 UGens.
// added interior localization, z-signal manipulation, and rotate, tilt
// and tumble transformations.
// B2Ster equations from 
// http://www.cyber.rdg.ac.uk/P.Sharkey/WWW/icdvrat/WWW96/Papers/keating.htm

BFPanner : Panner {
	*categories {^#["UGens>Multichannel>Ambisonics"]}
	}

BFDecoder : UGen {
	*categories {^#["UGens>Multichannel>Ambisonics"]}
	}
	
BFEncode1 : BFPanner {
	
	*ar { arg in, azimuth=0, elevation=0, rho = 1, gain=1, wComp = 1;
		^this.multiNew('audio', in, azimuth, elevation, rho, gain, wComp)
	}
	
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
}

BFEncodeSter : BFPanner {

	*ar { arg l, r, azimuth=0, width = 0.5pi, elevation=0, rho = 1, gain=1, wComp = 1;
		^this.multiNew('audio', l, r, azimuth, width, elevation, rho, gain, wComp )
	}
	
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
}

BFEncode2 : BFPanner {
	
	*ar { arg in, point_x = 1, point_y = 1, elevation=0, gain=1, wComp = 1;
		^this.multiNew('audio', in, point_x, point_y, elevation, gain, wComp )
	}

	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
} 

// second order encoder
FMHEncode0 : BFPanner {
	
	*ar { arg in, azimuth=0, elevation=0, gain=1;
		^this.multiNew('audio', in, azimuth, elevation, gain )
	}
	
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3),
					OutputProxy(\audio,this,4), OutputProxy(\audio,this,5),
					OutputProxy(\audio,this,6), OutputProxy(\audio,this,7),
					OutputProxy(\audio,this,8) ];
		^channels
	}
}

FMHEncode1 : BFPanner {
	
	*ar { arg in, azimuth=0, elevation=0, rho = 1, gain=1, wComp = 1;
		^this.multiNew('audio', in, azimuth, elevation, rho, gain, wComp )
	}
	
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3),
					OutputProxy(\audio,this,4), OutputProxy(\audio,this,5),
					OutputProxy(\audio,this,6), OutputProxy(\audio,this,7),
					OutputProxy(\audio,this,8) ];
		^channels
	}
}

FMHEncode2 : BFPanner {
	
	*ar { arg in, point_x = 0, point_y = 0, elevation=0, gain=1, wComp = 1;
		^this.multiNew('audio', in, point_x, point_y, elevation, gain, wComp)
	}
	
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3),
					OutputProxy(\audio,this,4), OutputProxy(\audio,this,5),
					OutputProxy(\audio,this,6), OutputProxy(\audio,this,7),
					OutputProxy(\audio,this,8) ];
		^channels
	}
}

BFDecode1 : BFDecoder {
	
	*ar { arg w, x, y, z, azimuth = 0, elevation = 0, mul = 1, add = 0;
		^this.multiNew('audio', w, x, y, z, azimuth, elevation ).madd(mul, add);
	}
	
	*ar1 {arg w, x, y, z, azimuth = 0, elevation = 0, maxDist = 10, distance = 10, mul = 1, 
			add = 0, scaleflag = 1;
		var dist, scaler;
		dist = ((maxDist - distance) / 345);
		scaler = if((scaleflag == 1), 1/((distance/maxDist)**1.5), 1);
		^DelayN.ar(this.multiNew('audio', w, x, y, z, azimuth, elevation ), dist, dist, 			scaler.reciprocal).madd(mul, add);
	}
	

 	checkInputs {
 		inputs[0..3].do({arg input, i; 	
 			if (rate !== input.rate) { 
 				^("input " + i + "is not" + rate + "rate: " + input + input.rate);
 			};
 			})
 		^this.checkValidInputs 
 	}
	
}

/* follows Furse / Malham conventions with some tweaking (W is scaled according to x, y, z, r, s, t, u, and v. s, t, u and v are scaled by 2/3.sqrt */

FMHDecode1 : BFDecoder {
	*ar {arg w, x, y, z, r, s, t, u, v, azimuth = 0, elevation = 0, mul = 1, add = 0;
		^this.multiNew('audio', w, x, y, z, r, s, t, u, v, azimuth, elevation).madd(mul, add);
		}
	
	*ar1 {arg w, x, y, z, r, s, t, u, v, azimuth = 0, elevation = 0, maxDist = 10, distance = 10, 
			mul = 1, add = 0, scaleflag = 1;
		var dist, scaler;
		dist = ((maxDist - distance) / 345);
		scaler = if((scaleflag == 1), 1/((distance/maxDist)**1.5), 1);
		^DelayN.ar(this.multiNew('audio', w, x, y, z, r, s, t, u, v, azimuth, elevation ), 
			dist, dist, scaler.reciprocal).madd(mul, add);
	}
	/* 
	* some common speaker configs, with the appropriate components zeroed out
	* see http://www.muse.demon.co.uk/ref/speakers.html
	* for more information
	*/
	
	*stereo {arg w, y, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, zero, y, zero, zero, zero, zero, zero, zero, [0.25pi, -0.25pi], 0, mul, add)
		}
	
	// stereo pairs
	*square {arg w, x, y, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, zero, v, [0.25pi, -0.25pi, 0.75pi, -0.75pi], 
			0, mul, add)
		}

	// quad clockwise
	*quad {arg w, x, y, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, zero, v, Array.series(4, 0.25pi, -0.5pi), 
			0, mul, add)
		}
	
	// point front first
	*pentagon {arg w, x, y, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, u, v, Array.series(5, 0, -0.4pi),
			0, mul, add)
		}
		
	*hexagon {arg w, x, y, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, u, v, 
			Array.series(6, 0.16666pi, -0.333333pi),
			0, mul, add)
		}

	// front bisects a side
	*octagon1 {arg w, x, y, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, u, v, 
			Array.series(8, 0.125pi, -0.25pi), 
			0, mul, add)
		}
	
	// front is a vertex
	*octagon2 {arg w, x, y, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, zero, zero, zero, zero, u, v, 
			Array.series(8, 0, -0.25pi), 
			0, mul, add)
		}
		
	*cube {arg w, x, y, z, s, t, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, z, zero, s, t, zero, v, 
			Array.series(8, 0.25pi, -0.5pi), 
			-0.25pi.dup(4) ++ 0.25pi.dup(4), mul, add)
		}	
	
	// top, then bottom
	*doubleHex {arg w, x, y, z, s, t, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, z, zero, s, t, u, v, 
			Array.series(12, 0.16666pi, -0.33333pi), 
			0.16666pi.dup(6) ++ -0.166666pi.dup(6), mul, add)
		}											
	// top, pentagonup, pentagondown, bottom
	*dodecahedron {arg w, x, y, z, r, s, t, u, v, mul = 1, add = 0;
		var zero;
		zero = K2A.ar(0);
		^this.ar(w, x, y, z, r, s, t, u, v, 
			[0] ++ Array.series(10, 0.2, -0.4) ++ [0],
			[0.5pi] ++ 0.16666pi.dup(5) ++ -0.16666pi.dup(5) ++ [-0.5pi],
			mul, add)
		}	
				
 	checkInputs {
 		inputs[0..8].do({arg input, i; 	
 			if (rate !== input.rate) { 
 				^("input " + i + "is not" + rate + "rate: " + input + input.rate);
 			};
 			})
 		^this.checkValidInputs 
 	}
		
	}
		
BFManipulate : BFPanner {
	
	*ar { arg w, x, y, z, rotate = 0, tilt = 0, tumble = 0;
		^this.multiNew('audio', w, x, y, z, rotate, tilt, tumble);
	}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }

} 

// Rotate tilt and tumble classes, built from Rotate2.  Allows w, x, y and z to be passed in, and 
// returns the new w, x, y, and z
Rotate : BFPanner {
	*ar {arg w, x, y, z, rotate;
		var xout, yout;
		#xout, yout = Rotate2.ar(x, y, rotate * -0.31830988618379);
		^[w, xout, yout, z];
		}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }
}	 

Tilt : BFPanner {
	*ar {arg w, x, y, z, tilt;
		var xout, zout;
		#xout, zout = Rotate2.ar(x, z, tilt * -0.31830988618379);
		^[w, xout, y, zout];
		}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }
}	

Tumble : BFPanner {
	*ar {arg w, x, y, z, tilt;
		var yout, zout;
		#yout, zout = Rotate2.ar(y, z, tilt * -0.31830988618379);
		^[w, x, yout, zout];
		}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }
}	

A2B : BFPanner {
	
	*ar { arg a, b, c, d;
		^this.multiNew('audio', a, b, c, d);
	}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }

}

B2A : BFPanner {
	
	*ar { arg w, x, y, z;
		^this.multiNew('audio', w, x, y, z);
	}
		
	init { arg ... theInputs;
		inputs = theInputs;		
		channels = [ OutputProxy(\audio,this,0), OutputProxy(\audio,this,1),
					OutputProxy(\audio,this,2), OutputProxy(\audio,this,3) ];
		^channels
	}
	
 	checkInputs { ^this.checkNInputs(4) }

}

B2Ster : BFPanner {
	*ar {arg w, x, y, mul = 1, add = 0;
		^this.multiNew('audio', w, x, y).madd(mul, add);
		}
		
	init {arg ... theInputs;
		inputs = theInputs;
		channels = [ OutputProxy(\audio, this, 0), OutputProxy(\audio, this, 1)];		^channels;
		}

 	checkInputs { ^this.checkNInputs(3) }

	}

// takes w, x, and y from a BF sig, returns a 2 channel UHJ file

B2UHJ : BFPanner {
	*ar {arg w, x, y;
		^this.multiNew('audio', w, x, y);
		}
		
	init {arg ... theInputs;
		inputs = theInputs;
		channels = [ OutputProxy(\audio, this, 0), OutputProxy(\audio, this, 1)];		^channels;
		}

 	checkInputs { ^this.checkNInputs(3) }

	}

// takes the left signal (ls) and right signal (rs) of a UHJ signal, 
// and returns w, x, and y of a BF signal

UHJ2B : BFPanner {
	*ar {arg ls, rs;
		^this.multiNew('audio', ls, rs);
		}
		
	init {arg ... theInputs;
		inputs = theInputs;
		^channels = [ OutputProxy(\audio, this, 0), OutputProxy(\audio, this, 1),
			OutputProxy(\audio, this, 2)]
		}

 	checkInputs { ^this.checkNInputs(2) }

	}
		
//////////////////////////////////////////////////////////////////////////////////////////////
// Classes from SuperCollider 2 are in the file AmbisonicsSC2
//////////////////////////////////////////////////////////////////////////////////////////////



