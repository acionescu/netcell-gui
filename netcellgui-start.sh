#!/bin/bash
nohup ./start.sh >> netcellgui.out 2> netcellgui.err < /dev/null & jobPid=$!
echo $jobPid > netcellgui.pid
exit 0
