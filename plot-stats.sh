#!/bin/bash
java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main nlesc https://research.software
echo "Plotting nlesc graphs"
gnuplot < users-nlesc.gnuplot
gnuplot < orgs-nlesc.gnuplot

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main helmholtz https://helmholtz.software
echo "Plotting helmholtz graphs"
gnuplot < users-helmholtz.gnuplot
gnuplot < orgs-helmholtz.gnuplot
