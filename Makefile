all:
	(cd CircuitRouter-SeqSolver; make $(MFLAGS) all)
	(cd CircuitRouter-SimpleShell; make $(MFLAGS) all)

clean:
	(cd CircuitRouter-SeqSolver; make $(MFLAGS) clean)
	(cd CircuitRouter-SimpleShell; make $(MFLAGS) clean)