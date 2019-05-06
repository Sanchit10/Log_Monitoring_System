#!/bin/bash

javac RunSimulator.java

while (true)
do
echo -ne  ">"
read input

if [ "$input" != "EXIT" ]; then

arr=($input)
ipaddress={arr[1]}
cpuid={arr[2]}
startdate={arr[3]}
startime="{arr[4]}"
endate=arr[5]
endtime=arr[6]

if [ "${arr[0]}" != "Query" ]; then
echo "Query is misspelled. Please try again"



else
java RunSimulator $1 "${arr[1]}" "${arr[2]}" "${arr[3]}" "${arr[4]}" "${arr[5]}" "${arr[6]}"
#echo "${arr[1]}"
fi

else
echo "Closing simulator bye bye"
exit
fi
done







