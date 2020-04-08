# CO20 Docker

Helper image for 2020's Compilers course project.

Usage:

```bash
$ docker pull hacdias/co20:latest
$ docker run \
  -v /path/to/project/source:/proj/src \
  -v /path/to/xml/output:/proj/xml \
  -it hacdias/co20
```

Inside you can:

```bash
# Build the project (or make build)
$ make
# Cleanup the project
$ make clean
# Run the tests
$ make test
# Make XML from source files
$ make xml
```
