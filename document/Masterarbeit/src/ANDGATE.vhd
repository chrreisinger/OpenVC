library IEEE;
use IEEE.std_logic_1164.all;

entity ANDGATE is
  port (
    I1 : in std_logic;
    I2 : in std_logic;
    O  : out std_logic);
end entity ANDGATE;

architecture RTL of ANDGATE is
begin
  O <= I1 and I2;
end architecture RTL;