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

final class TypeDefinitionTests extends GenericTest {
  compileCodeInPackageAndLoad("enumeration type delcaration") {
    """
      type MULTI_LEVEL_LOGIC is (LOW, HIGH, RISING, FALLING, AMBIGUOUS) ;
      type BIT is ('0','1') ;
      type SWITCH_LEVEL is ('0','1','X') ; -- Overloads '0' and '1'
    """
  }

  compileCodeInPackageAndLoad("integer type delcaration") {
    """
      type TWOS_COMPLEMENT_INTEGER is range -32768 to 32767;
      type BYTE_LENGTH_INTEGER is range 0 to 255;
      type WORD_INDEX is range 31 downto 0;
      subtype HIGH_BIT_LOW is BYTE_LENGTH_INTEGER range 0 to 127;
    """
  }

  compileCodeInPackageAndLoad("array type declaration") {
    """
      constant n         : integer := 4;
      type     MY_WORD is array (0 to 31) of bit;
      -- A memory word type with an ascending range.
      type     DATA_IN is array (7 downto 0) of boolean;
      -- An input port type with a descending range.
      -- Example of unconstrained array declarations :
      type     MEMORY is array (integer range <>) of MY_WORD;
      -- A memory array type.
      -- Examples of array object declarations :
      variable DATA_LINE : DATA_IN;
      -- Defines a data input line.
      variable MY_MEMORY : MEMORY (0 to 2**n-1);
      variable l, r      : bit_vector(0 to 10);
    """
  }

  compileCodeInPackageAndLoad("record type delcaration") {
    """
    type MONTH_NAME is range 1 to 12;
    type DATE is
      record
        DAY :   INTEGER range 1 to 31;
        MONTH :   MONTH_NAME;
        YEAR :   INTEGER range 0 to 4000;
      end record;
    """
  }

  compileCodeInPackageAndLoad("access type delcaration") {
    """
      subtype BYTE is bit_vector (7 downto 0);
      type    MEMORY is array (natural range <>) of BYTE;
      type    ADDRESS is access MEMORY;
      type    INTEGER_PTR is access integer;
    """
  }

  compileCodeInPackageAndLoad("access type delcaration and variable declarations") {
    """
      type Node is record
        x : integer;
        y : real;
      end record;
      type     nodePointer is access Node;
      type     intPointer is access integer;
      type     bitVectorPointer is access bit_vector;
      variable a : nodePointer      := new Node;
      variable b : nodePointer      := new Node'(100, 100.0);
      variable c : nodePointer      := new Node'(y => 100.0, x => 100);
      variable e : intPointer;
      variable f : intPointer       := null;
      variable g : intPointer       := new integer;
      variable h : bitVectorPointer := new bit_vector(1 to 10);
    """
  }

  compileCodeInPackageAndLoad("incomplete type delcaration") {
    """
      type CELL; -- An incomplete type declaration.
      type LINK is access CELL;
      type CELL is
        record
          VALUE : INTEGER;
          SUCC : LINK;
          PRED : LINK;
        end record CELL;
    """
  }

  compileCodeInPackageAndLoad("mutually dependent access types") {
    """
      type PART; -- Incomplete type declarations.
      type WIRE;
      type PART_PTR is access PART;
      type WIRE_PTR is access WIRE;
      type PART_LIST is array (POSITIVE range <>) of PART_PTR;
      type WIRE_LIST is array (POSITIVE range <>) of WIRE_PTR;
      type PART_LIST_PTR is access PART_LIST;
      type WIRE_LIST_PTR is access WIRE_LIST;
      type PART is
        record
          PART_NAME : STRING (1 to MAX_STRING_LEN);
          CONNECTIONS : WIRE_LIST_PTR;
        end record;
      type WIRE is
        record
          WIRE_NAME : STRING (1 to MAX_STRING_LEN);
          CONNECTS : PART_LIST_PTR;
        end record;
    """
  }

  compileCodeInPackageAndRun("physical type delcaration") {
    """
      type DURATION is range -1E18 to 1E18
        units
          fs; --  femtosecond
          ps      =   1000 fs; --  picosecond
          ns      =   1000 ps; --  nanosecond
          us      =   1000 ns; --  microsecond
          ms     =   1000 us; --  millisecond
          sec     =   1000 ms; --  second
          min    =   60 sec; --  minute
        end units;

      type DISTANCE is range 0 to 1E16
        units
          -- primary unit:
          A; --  angstrom
          -- metric lengths:
          nm     =   10 A; --  nanometer
          um     =   1000 nm; -- micrometer (or micron)
          mm    =   1000 um; -- millimeter
          cm     =   10 mm; -- centimeter
          m       =   1000 mm; -- meter
          km     =   1000 m; -- kilometer
          -- English lengths:
          mil     =   254000 A; --  mil
          inch   =   1000 mil; --  inch
          ft       =   12 inch; --  foot
          yd     =    3 ft; --  yard
          fm     =    6 ft; --  fathom
          mi     =    5280 ft; --  mile
          lg      =    3 mi; --  league
        end units DISTANCE;

        procedure main is
          variable x: distance; variable y: duration; variable z: integer;
        begin
          x := 5 A + 13 ft - 27 inch;
          y := 3 ns + 5 min;
          z := ns / ps;

          x := z * mi;
          y := y/10;
          z := 39.34 inch / m;
        end;
    """
  }

  compileAndLoad("protected type declaration") {
    """
    package dummy is
      type SharedCounter is protected
        procedure increment  (N: Integer := 1);
        procedure decrement (N: Integer := 1);
        impure function value return Integer;
      end protected SharedCounter;

      type ComplexNumber is protected
        procedure extract (variable r, i: out Real);
        procedure add (variable a, b: inout ComplexNumber);
      end protected ComplexNumber;

      type VariableSizedBitArray is protected
        procedure add_bit (index: Positive; value: Bit);
        impure function size return Natural;
      end protected VariableSizedBitArray;
    end package dummy;

    package body dummy is
      type SharedCounter is protected body
        variable counter: Integer := 0;

        procedure increment (N: Integer := 1) is
        begin
          counter := counter + N;
        end procedure increment;

        procedure decrement (N: Integer := 1) is
        begin
          counter := counter - N;
        end procedure decrement;

        impure function value return Integer is
        begin
          return counter;
        end function value;
      end protected body SharedCounter;

      type ComplexNumber is protected body
        variable re, im: Real;

        procedure extract (r, i: out Real) is
        begin
          r := re;
          i := im;
        end procedure extract;

        procedure add (variable a, b: inout ComplexNumber) is
          variable a_real, b_real: Real;
          variable a_imag, b_imag: Real;
        begin
          a.extract (a_real, a_imag);
          b.extract (b_real, b_imag);
          re  := a_real + b_real;
          im := a_imag + b_imag;
        end procedure add;
      end protected body ComplexNumber;

      type VariableSizeBitArray is protected body
        type bit_vector_access is access Bit_Vector;

        variable bit_array: bit_vector_access := null;
        variable bit_array_length: Natural := 0;

        procedure add_bit (index: Positive; value: Bit) is
          variable tmp: bit_vector_access;
        begin
          if index > bit_array_length then
            tmp := bit_array;
            bit_array := new bit_vector (1 to index);
            if tmp /= null then
              bit_array (1 to bit_array_length) := tmp.all;
              deallocate (tmp);
            end if;
            bit_array_length := index;
          end if;
          bit_array (index) := value;
        end procedure add_bit;

        impure function size return Natural is
        begin
          return bit_array_length;
        end function size;

        end protected body VariableSizeBitArray;
      end package body dummy;
      
        variable INDEX : INTEGER range 0 to 99 := 0 ;
        -- Initial value is determined by the initial value expression
        variable COUNT : POSITIVE ;
        -- Initial value is POSITIVE'LEFT; that is,1
        variable MEMORY : BIT_MATRIX (0 to 7, 0 to 1023) ;
        -- Initial value is the aggregate of the initial values of each element
        shared variable Counter: SharedCounter;
        -- See 3.5.1 and 3.5.2 for the definitions of SharedCounter
        shared variable addend, augend, result: ComplexNumber;
        -- See 3.5.1 and 3.5.2 for the definition of ComplexNumber
        variable bit_stack: VariableSizeBitArray;
        -- See 3.5.1 and 3.5.2 for the definition of VariableSizeBitArray;
    """
  }

}