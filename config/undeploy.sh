#!/bin/bash
#@autor Guillemain
chemin=`pwd`
clear
echo ""
echo " supression des daemons pour les machines : "
for i in $*;
do echo -n "|\t"
echo $i;
done
#Lancement
cmpt=5000
for i in $*;
do
ssh $i " killall java ;" 
cmpt=$((($cmpt)+1))
done
echo "fini"
echo ""
