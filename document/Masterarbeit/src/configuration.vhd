configuration CFG_FULLADDER of FULLADDER is
    for STRUCT
        for MODULE2: HALFADDER
           use entity work.B(GATE);
           port map ( U => A,
                           V => B,
                           X => SUM,
                           Y => CARRY );
        end for;

        for  others : HALFADDER
           use entity work.A(RTL);
        end for;
    end for;
 end CFG_FULLADDER;