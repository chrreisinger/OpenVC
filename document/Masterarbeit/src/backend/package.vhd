package p is
  type intA is range 0 to 10;
  type intB is range 0 to 10;

  procedure proc(x: IntA := 1);
  procedure proc(x: IntB := 2);
end p;
