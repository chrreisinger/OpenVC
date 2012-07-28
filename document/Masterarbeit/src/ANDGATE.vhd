--library IEEE;
--use IEEE.std_logic_1164.all;

entity ANDGATE is
  port (
    I1 : in bit;
    I2 : in bit;
    O  : out bit);
end entity ANDGATE;

architecture RTL of ANDGATE is
--  signal I1, I2, O : bit := '0';
begin
  O <= I1 and I2;
end architecture RTL;