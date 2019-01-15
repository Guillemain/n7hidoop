#!/bin/bash
#@autor Guillemain
chemin=`pwd`
clear
echo "\t_______________________________________"
echo "\t|                                     |"
echo "\t| Lancement des Daemons de Hidoop V.0 |"
echo "\t|_____________________________________|"
echo ""
echo " Deploiement pour les machines suivantes : "

for i in $*;
do echo 
echo "|\t" $i;
done
#Lancement
for i in $*;
do
ssh $i "cd $chemin && java ordo/DaemonImpl 5510"& 
echo ""
done
echo ""