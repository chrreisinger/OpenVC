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

final class DeclarationTests extends GenericTest {
  compileCodeInPackageAndLoad("compile a group declaration") {
    """
      group PIN2PIN is (signal, signal); -- Groups of this type consist of two signals.
      group RESOURCE is (label <>); -- Groups of this type consist of any number of labels.
      group DIFF_CYCLES is (group <>); -- A group of groups.
    """
  }
  compileCodeInPackageAndLoad("compile a file declaration") {
    """
      type IntegerFile is file of INTEGER;
      file F1: IntegerFile; -- No implicit FILE_OPEN is performed during elaboration.
      file F2: IntegerFile is "tests.dat"; -- At elaboration, an implicit call is performed: FILE_OPEN (F2, "tests.dat"); The OPEN_KIND parameter defaults to READ_MODE.
      file F3: IntegerFile open WRITE_MODE is "tests.dat"; --At elaboration, an implicit call is performed: FILE_OPEN (F3, "tests.dat", WRITE_MODE);
    """
  }

  compileCodeInPackageAndLoad("compile a file type declaration") {
    """
      type fileType1 is file of STRING; -- Defines a file type that can contain an indefinite number of strings of arbitrary length.
      type fileType2 is file of NATURAL; -- Defines a file type that can contain only nonnegative integer values.
    """
  }

  compileCodeInPackageAndLoad("compile a attribute declaration") {
    """
      type COORDINATE is record X,Y: INTEGER; end record;
      subtype POSITIVE is INTEGER range 1 to INTEGER'HIGH;
      attribute LOCATION: COORDINATE;
      attribute PIN_NO: POSITIVE;
    """
  }
  compileAndLoad("compile a architecture with a shared protected variable") {
    """
    architecture UseSharedVariables of SomeEntity is
      subtype ShortRange is INTEGER range -1 to 1;

      type ShortRangeProtected is protected
        procedure Set(V: ShortRange);
        procedure Get(V: out ShortRange);
      end protected;
      type ShortRangeProtected is protected body
          variable Local: ShortRange := 0;
        begin
          procedure Set(V: ShortRange) is
          begin
            Local := V;
          end procedure Set;
          procedure Get(V: out ShortRange) is
          begin
            V := Local;
          end procedure Get;
      end protected body;
      shared variable Counter: ShortRangeProtected ;

    begin
      PROC1: process
        variable V: ShortRange;
      begin
        Counter.Get( V );
        Counter.Set( V+1 );
        wait;
      end process PROC1;

      PROC2: process
        variable V: ShortRange;
      begin
        Counter.Get( V );
        Counter.Set( V-1);
        wait;
      end process PROC2;
    end architecture UseSharedVariables;
    """
  }
}