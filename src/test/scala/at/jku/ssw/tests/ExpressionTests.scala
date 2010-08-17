/*
 *     OpenVC, an open source VHDL compiler/simulator
 *     Copyright (C) 2010  Christian Reisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.jku.ssw.tests

final class ExpressionTests extends GenericTest {
  compileCodeInPackageAndLoad("compile constant declarations with value epxressions") {
    """
    subtype BYTE is BIT_VECTOR (7 downto 0);
    type MEMORY is array (Natural range <>) of BYTE;

    -- The following concatenation accepts two BIT_VECTORs and returns a BIT_VECTOR
    -- [case a)]:
    constant ZERO: BYTE := "0000" & "0000";
    -- The next two examples show that the same expression can represent either case a) or
    -- case c), depending on the context of the expression.
    -- The following concatenation accepts two BIT_VECTORS and returns a BIT_VECTOR
    -- [case a)]:
    constant C1: BIT_VECTOR := ZERO & ZERO;
    -- The following concatenation accepts two BIT_VECTORs and returns a MEMORY
    -- [case c)]:
    constant C2: MEMORY := ZERO & ZERO;
    -- The following concatenation accepts a BIT_VECTOR and a MEMORY, returning a
    -- MEMORY [case b)]:
    constant C3: MEMORY := ZERO & C2;
    -- The following concatenation accepts a MEMORY and a BIT_VECTOR, returning a
    -- MEMORY [case b)]:
    constant C4: MEMORY := C2 & ZERO;
    -- The following concatenation accepts two MEMORYs and returns a MEMORY [case a)]:
    constant C5: MEMORY := C2 & C3;
    type R1 is range 0 to 7;
    type R2 is range 7 downto 0;
    type T1 is array (R1 range <>) of Bit;
    type T2 is array (R2 range <>) of Bit;
    subtype S1 is T1(R1);
    subtype S2 is T2(R2);
    constant K1: S1 := (others => '0');
    constant K2: T1 := K1(1 to 3) & K1(3 to 4); -- K2'Left = 0 and K2'Right = 4
    constant K3: T1 := K1(5 to 7) & K1(1 to 2); -- K3'Left = 0 and K3'Right = 4
    constant K4: T1 := K1(2 to 1) & K1(1 to 2); -- K4'Left = 0 and K4'Right = 1
    constant K5: S2 := (others => '0');
    constant K6: T2 := K5(3 downto 1) & K5(4 downto 3); -- K6'Left = 7 and K6'Right = 3
    constant K7: T2 := K5(7 downto 5) & K5(2 downto 1); -- K7'Left = 7 and K7'Right = 3
    constant K8: T2 := K5(1 downto 2) & K5(2 downto 1); -- K8'Left = 7 and K8'Right = 6
    """
  }

  compileCodeInPackageAndRun("compile expressions with mod and rem") {
    """
      procedure main is
      begin  -- test
        assert 5 rem 3 = 2;
        assert 5 mod 3 = 2;
        assert (-5) rem 3 = -2;
        assert (-5) mod 3 = 1;
        assert (-5) rem (-3) = -2;
        assert (-5) mod (-3) = -2;
        assert 5 rem (-3) = 2;
        assert 5 mod (-3) = -1;
      end main;
    """
  }

    compileCodeInPackageAndRun("compile expressions with abs and pow") {
    """
      procedure main is
        variable x : integer;
        variable y : real;
        variable z : time;
      begin  -- test
        x:=abs x;
        y:=abs y;
        z:=abs z;
        x:=x**2;
        y:=y**2;
      end main;
    """
  }
}