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

final class ArchitectureTests extends GenericTest {
  compileAndLoad("compile a architecture with concurrent signal assignments") {
    """
      entity Full_Adder is
        port (X, Y, Cin: in Bit; Cout, Sum: out Bit) ;
      end Full_Adder ;
       architecture DataFlow of Full_Adder is
            signal A,B: Bit;
       begin
            A <= X xor Y;
            B <= A and Cin;
            Sum <= A xor Cin;
            Cout <= B or (X and Y) ;
       end architecture DataFlow ;
  """
  }

  compileAndLoad("compile a architecutre ") {
    """
      library Test;
      use Test.Components.all;
      architecture Structure of TestBench is
      component Full_Adder port (X, Y, Cin: Bit; Cout, Sum: out Bit);
      end component;
      signal A,B,C,D,E,F,G: Bit;
      signal OK: Boolean;
      begin
      UUT: Full_Adder port map (A,B,C,D,E);
      Generator: AdderTest port map (A,B,C,F,G);
      Comparator: AdderCheck port map (D,E,F,G,OK);
      end Structure;
    """
  }

  compileAndLoad("compile a architecture 2") {
    """
      entity AndGate is
      generic
            (N: Natural := 2);
      port
            (Inputs: in Bit_Vector (1 to N);
             Result: out Bit) ;
      end entitySymbol AndGate ;
    architecture Behavior of AndGate is
      begin
      process (Inputs)
            variable Temp: Bit;
      begin
            Temp := '1';
            for i in Inputs'Range loop
                  if Inputs(i) = '0' then
                    Temp := '0';
                    exit;
                  end if;
            end loop;
            Result <= Temp after 10 ns;
      end process;
    end Behavior;
    """
  }
}