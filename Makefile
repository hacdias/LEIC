ROOT = /
MAKEOPTS =
MAKEOPTS += ROOT=$(ROOT)

LANGUAGE = og

.PHONY: all clean build examples test
all: $(LANGUAGE)

$(LANGUAGE): build
	cp src/$(LANGUAGE) .

build:
	$(MAKE) -C src $(MAKEOPTS) ast/all.h ast/visitor_decls.h # to work with multiple jobs
	$(MAKE) -C src $(MAKEOPTS) all

examples: build
	$(MAKE) -C examples $(MAKEOPTS) all

test: examples

clean:
	$(MAKE) -C src $(MAKEOPTS) clean
	$(MAKE) -C examples $(MAKEOPTS) clean
	rm -f $(LANGUAGE)
