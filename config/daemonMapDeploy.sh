#!/bin/bash
#@autor Guillemain
chemin=`pwd`
clear
echo "\t_____________________________________"
echo "\t|                                   |"
echo "\t| Lacement des Daemons de Hidoo V.0 |"
echo "\t|___________________________________|"
echo ""
echo " Deploiement pour les machines suivantes : "

for i in $*;
do echo 
echo "|\t" $i;
done
#Lancement
for i in $*;
do
ssh $i "cd $chemin && java ordon/DaemonImpl "& 
echo ""
done
echo ""