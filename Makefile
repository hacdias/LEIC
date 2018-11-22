all:
	(cd CircuitRouter-SeqSolver; make $(MFLAGS) all)
	(cd CircuitRouter-ParSolver; make $(MFLAGS) all)
	(cd CircuitRouter-SimpleShell; make $(MFLAGS) all)
	(cd CircuitRouter-AdvShell; make $(MFLAGS) all)

clean:
	(cd CircuitRouter-SeqSolver; make $(MFLAGS) clean)
	(cd CircuitRouter-ParSolver; make $(MFLAGS) clean)
	(cd CircuitRouter-SimpleShell; make $(MFLAGS) clean)
	(cd CircuitRouter-AdvShell; make $(MFLAGS) clean)
