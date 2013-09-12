#import json
#import Image
import sys
sys.path.append("/var/www/html/stratification")
#import scidb

#from scidbpy import interface, SciDBQueryError, SciDBArray
#sdb = interface.SciDBShimInterface('http://localhost:8080')

import os
import json
#import sys
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

    proc = subprocess.Popen(["/opt/scidb/13.3/bin/iquery","-o", "csv", "-q", cmd], stdout = subprocess.PIPE)
    out,err = proc.communicate()

    lines = out.split("\n")
    #first line is header, last line is empty
    #header = lines[0]
    rows = lines[1:-1]
    return rows




def application(environ, start_response):
     
    start_response('200 OK', [('Content-Type', 'text/html')])
    proc = subprocess.Popen(["python","scidbtile.wsgi"],stdout = subprocess.PIPE)
    out,err = proc.communicate()
    lines = out.split("\n")
    

    #first line is header, last line is empty
    #header = lines[0]
    rows = lines[1:-1]
    #bitmap = scidb.querySciDB("SELECT * FROM subarray(Michalis,0,0,1024,1024)")
    #bitmap = scidb.querySciDB("python scidbtile.wsgi")
    # im = Image.open('/var/www/html/stratification/image.gif')
    # rgb_im = im.convert('RGB')
    # width, height = im.size
    # width = width
    # height = height
    # bitmap = [[x for x in xrange(3)] for x in xrange(width*height)]
    # count = 0
    ## f = open('/var/www/html/stratification/test.csv','w')
    ## f.write(str("i,j,r,g,b\n"))
    # for i in range(height):
    #     for j in range(width):           
    #         r,g,b = rgb_im.getpixel((j,i))
    #         bitmap[count][0] = r
    #         bitmap[count][1] = g
    #         bitmap[count][2] = b
    #         count = count + 1
    #         #f.write(str(i)+","+str(j)+","+str(r)+","+str(g)+","+str(b)+"\n")
    #print bitmap 
    return str(rows)
    #return "ta"

if __name__ == '__main__':
   #start_response('200 OK', [('Content-Type', 'text/html')])
   bitmap = querySciDB("SELECT * FROM subarray(Michalis,0,0,1024,1024)")
   #bitmap = scidb.querySciDB("python scidbtile.wsgi")
   #bitmap = scidb.querySciDB("dummy")
   print bitmap
