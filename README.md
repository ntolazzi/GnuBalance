# GnuBalance

This repository contains the server and client code for a **synchronized dept manager**
which is meant to be used in a cooperative manner.
My initial incentive for the work was that I was never satisfied
with the existing applications in the typical AppStores for two primary reasons. First, I dislike
advertisement in apps and the corresponding security risks. Second, I want to keep my data as private as possible
and therefore decided to go with a self hosted solution.

GnuBalance is mainly meant as a simple app that manages debts between two parties. It is part of the GnuTools app package
that can be found in my repositories on GitHub.

The client code is written in Java and distributed as an AndroidStudio project.

The server code is build using [Flask](http://flask.pocoo.org/) and implements an incomplete RESTful API.
