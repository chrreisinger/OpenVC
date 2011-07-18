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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EntityTests extends GenericTest {
  compileAndLoad("compile a empty entity") {
    """
      entity TestBench is
      end TestBench ;
    """
  }

  compileAndLoad("compile a entity with generic and ports") {
    """
      entity AndGate is
      generic
            (N: Natural := 2);
      port
            (Inputs: in Bit_Vector (1 to N);
             Result: out Bit) ;
      end entity AndGate ;
    """
  }

  compileAndLoad("compile a entity with a port") {
    """
      entity Full_Adder is
        port (X, Y, Cin: in Bit; Cout, Sum: out Bit) ;
      end Full_Adder ;
    """
  }

  compileAndLoad("compile a entity with declarative items") {
    """
    entity ROM is
      port ( Addr: in Word;
      Data: out Word;
      Sel: in Bit);
      type Instruction is array (1 to 5) of Natural;
      type Program is array (Natural range <>) of Instruction;
      use Work.OpCodes.all, Work.RegisterNames.all;
      constant ROM_Code: Program :=
             (
      (STM, R14, R12, 12, R13) ,
      (LD, R7, 32, 0, R1  ) ,
      (BAL, R14, 0, 0, R7  ) ,
             ) ;
      end ROM;
    """
  }
  compileAndLoad("compile a entity with a concurrent statement") {
    """
    entity Latch is
      port ( Din: in Word;
        Dout: out Word;
        Load: in Bit;
        Clk: in Bit );

      constant Setup: Time := 12 ns;
      constant PulseWidth: Time := 50 ns;
      --use Work.TimingMonitors.all;
      begin
        assert Clk='1' or Clk'Delayed'Stable (PulseWidth);
        --CheckTiming (Setup, Din, Load, Clk);
      end ;
    """
  }
  compileAndLoad("compile a entity with generics") {
    """
    entity E is
      generic (G1, G2, G3, G4: INTEGER);
      port (P1, P2: STRING (G1 to G2));
      procedure X (Y3: INTEGER range G3 to G4);
    end E;
    """
  }

}