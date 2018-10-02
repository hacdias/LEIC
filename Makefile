all:
	cd CircuitRouter-SeqSolver && $(MAKE)
	cd CircuitRouter-SimpleShell && $(MAKE)

clean:
	cd CircuitRouter-SeqSolver && $(MAKE) clean
	cd CircuitRouter-SimpleShell && $(MAKE) clean
