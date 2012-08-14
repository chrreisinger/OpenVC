PACKAGE std_logic_1164 IS
    -- logic state system  (unresolved)
    TYPE std_ulogic IS ( 'U',  -- Uninitialized
                         'X',  -- Forcing  Unknown
                         '0',  -- Forcing  0
                         '1',  -- Forcing  1
                         'Z',  -- High Impedance
                         'W',  -- Weak     Unknown
                         'L',  -- Weak     0
                         'H',  -- Weak     1
                         '-'   -- Don't care
                       );
    -- unconstrained array of std_ulogic for use with the resolution function
    TYPE std_ulogic_vector IS ARRAY ( NATURAL RANGE <> ) OF std_ulogic;

    -- resolution function
    FUNCTION resolved ( s : std_ulogic_vector ) RETURN std_ulogic;

    -- *** industry standard logic type ***
    SUBTYPE std_logic IS resolved std_ulogic;

    -- unconstrained array of std_logic for use in declaring signal arrays
    TYPE std_logic_vector IS ARRAY ( NATURAL RANGE <>) OF std_logic;

    -- overloaded logical operators
    FUNCTION "and"  ( l : std_ulogic; r : std_ulogic ) RETURN std_ulogic;
	
END std_logic_1164;