library ieee;

package math_complex is
  type complex is record 
    re, im: real; 
  end record;
  type complex_vector is array (integer range <>) of complex;
  type complex_polar  is record mag: real; arg: real; end record;

  constant  cbase_1: complex := complex'(1.0, 0.0);
  constant  cbase_j: complex := complex'(0.0, 1.0);
  constant  czero: complex := complex'(0.0, 0.0);

  -- returns absolute value (magnitude) of z
  function cabs(z: in complex) return real;
  
  -- returns argument (angle) in radians of a complex number
  function carg(z: in complex) return real; 
  
  -- arithmetic operators
  function "+" (l: in complex;  r: in complex) return complex;
end  math_complex;
