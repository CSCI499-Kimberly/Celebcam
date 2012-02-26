#!/bin/bash

path=celebcam_android_1.6/src/com/celebcam

echo "package com.celebcam;
public class LineCount{ 
 static String lineCount=Integer.toString(" > $path/LineCount.java

echo `wc -l  celebcam_android_1.6/src/com/celebcam/*.java celebcam_android_1.6/src/com/celebcam/celebcamapi/* | tail -n 1` > celebcam_line_count

awk '{print $1, ");}"}' celebcam_line_count  >> $path/LineCount.java

rm celebcam_line_count
 
