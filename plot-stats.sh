#!/bin/bash
date '+%Y%m%d-%H%M' >> lastrun.txt

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main nlesc https://research.software
echo "Plotting nlesc graphs"
gnuplot < users-nlesc.gnuplot
gnuplot < orgs-nlesc.gnuplot

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main helmholtz https://helmholtz.software
echo "Plotting helmholtz graphs"
gnuplot < users-helmholtz.gnuplot
gnuplot < orgs-helmholtz.gnuplot

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main nfdi https://nfdi.software
echo "Plotting nfdi graphs"
gnuplot < users-nfdi.gnuplot
gnuplot < orgs-nfdi.gnuplot

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main veda https://vedaresearch.nl
echo "Plotting veda graphs"
gnuplot < users-veda.gnuplot
gnuplot < orgs-veda.gnuplot

java -cp ./target/rsd-0.1-jar-with-dependencies.jar nl.esciencecenter.rsd.Main koeln https://research-software.cceh.uni-koeln.de/
echo "Plotting koeln graphs"
gnuplot < users-koeln.gnuplot
gnuplot < orgs-koeln.gnuplot

