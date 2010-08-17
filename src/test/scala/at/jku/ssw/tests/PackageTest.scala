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

final class PackageTest extends GenericTest {
  compileAndLoad("compile a package with extended identifiers"){
    """
    package dummy is
    end dummy;
    package body dummy is
      procedure test is
        variable \BUS\, \bus\  : integer;
        variable \a\\b\ : integer;
        variable VHDL, \VHDL\, \vhdl\ : integer;
      begin
        null;
      end;
    end package body dummy;
    """
  }
  compileAndLoad("compile a package without a body") {
    """
      package TimeConstants is    
        constant tPLH : Time := 10 ns;
        constant tPHL : Time := 12 ns;
        constant tPLZ : Time := 7 ns;
        constant tPZL : Time := 8 ns;
        constant tPHZ : Time := 8 ns;
        constant tPZH : Time := 9 ns;
      end TimeConstants ;
    """
  }
  compileAndLoad("compile package header and body") {
    """
      package TriState is
        type Tri is ('0', '1', 'Z', 'E');
        function BitVal (Value: Tri) return Bit ;
        function TriVal (Value: Bit) return Tri;
        type TriVector is array (Natural range <>) of Tri ;
        function Resolve (Sources: TriVector) return Tri ;
      end package TriState ;

      package body TriState is
        function BitVal (Value: Tri) return Bit is
          constant Bits : Bit_Vector := "0100";
        begin
          return Bits(Tri'Pos(Value));
        end;

        function TriVal (Value: Bit) return Tri is
        begin
          return Tri'Val(Bit'Pos(Value));
        end;

        function Resolve (Sources: TriVector) return Tri is
          variable V: Tri := 'Z';
        begin
          for i in Sources'Range loop
                if Sources(i) /= 'Z' then
                      if V = 'Z' then
                          V := Sources(i);
                      else
                          return 'E';
                      end if;
                end if;
          end loop;
          return V;
        end;
      end package body TriState ;
    """
  }
}