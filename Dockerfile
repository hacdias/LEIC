FROM opensuse/leap:15.1

# Thanks to @RageKnify for the examples.zip file.
ENV EXAMPLES_URL https://cdn.discordapp.com/attachments/672434875416182834/680575519267487748/examples.zip

# Compiled binaries published by the professor. More info on http://bit.ly/2vd3Tev
ENV REPO_URL https://download.opensuse.org/repositories/home:/d4vid:/co20/openSUSE_Leap_15.1/

RUN zypper ar $REPO_URL CO20 && \
  zypper --gpg-auto-import-keys refresh

RUN zypper install -y graphviz doxygen unzip vim \
  gcc-c++ gdb valgrind \
  yasm byacc wget lbzip2 \
  libcdk15-devel librts5-devel

RUN zypper in -y -t pattern devel_basis

WORKDIR /proj
VOLUME /proj/src
VOLUME /proj/xml

RUN wget $EXAMPLES_URL -O examples.zip && \
  unzip examples.zip && \
  rm examples.zip

COPY Makefile /proj/Makefile
COPY xmlall.sh /proj/xmlall.sh
