import os
import json
import sys
import time
import subprocess      
import Image
import StringIO
import urlparse
import datetime
import base64
import csv

sys.path.append('/var/www/html/stratification')
#import render
#import benchmark


def querySciDB(cmd):
    """Execute the given SciDB command using iquery, returning the tabular result"""
    #startT = benchmark.startTimer(cmd)

    #proc = subprocess.Popen(["/opt/scidb/13.3/bin/iquery","-o", "csv", "-q", cmd], stdout = subprocess.PIPE)
    proc = subprocess.Popen(["python","scidbtile.wsgi"],stdout = subprocess.PIPE)
    out,err = proc.communicate()
    
    lines = out.split("\n")
    #first line is header, last line is empty
    #header = lines[0]
    rows = lines[1:-1]
    return rows
