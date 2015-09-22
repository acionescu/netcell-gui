#!/bin/bash
java -Dlog4j.configuration=file:log4j.properties -cp .:target/*:target/dependency/* net.segoia.netcell.gui.NetcellGuiController
