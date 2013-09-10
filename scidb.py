#!/usr/bin/python

import numpy as np
#from numpy.testing import assert_allclose, assert_equal, assert_
from scidbpy import interface, SciDBQueryError, SciDBArray
sdb = interface.SciDBShimInterface('http://localhost:8080')


def init_array():
    """Test export to a SciDB array"""
    X = np.random.random((10, 6))
    Xsdb = sdb.from_array(X)
    #def check_toarray(transfer_bytes):
    #    Xnp = Xsdb.toarray(transfer_bytes=transfer_bytes)
    #    # set ATOL high because we're translating text
    #    assert_allclose(Xnp, X, atol=1E-5)
    # 
    #for transfer_bytes in (True, False):
    #    yield check_toarray, transfer_bytes

if __name__ == '__main__':
    print("scidb.py is being run directly")
    init_array()






# import os
# import json
# import sys
# import time
# import subprocess      
# import Image
# import StringIO
# import urlparse
# import datetime
# import base64
# import csv

# sys.path.append('/var/www/wm/wsgi')
# import render
# import benchmark

# volume = dict()
# width = 0
# height = 0
# depth = 0
# iquery_path = "/opt/scidb/12.10/bin/iquery"

# def querySciDB(cmd):
#     """Execute the given SciDB command using iquery, returning the tabular result"""
#     #startT = benchmark.startTimer(cmd)

#     proc = subprocess.Popen([iquery_path, "-o", "csv+", "-a", "-q", cmd], stdout = subprocess.PIPE)
#     out,err = proc.communicate()

#     lines = out.split("\n")
#     # first line is header, last line is empty
#     header = lines[0].split(",")
#     rows = [line.split(",") for line in lines[1:-1]]
#     #benchmark.endTimer(cmd, startT)

#     return header, rows 

# def querySciDB2(cmd):
#     """Execute the given SciDB command using iquery, returning the tabular result"""
#     #startT = benchmark.startTimer(cmd)
#     proc = subprocess.Popen([iquery_path, "-o", "csv", "-a", "-q", cmd], stdout = subprocess.PIPE)
#     out,err = proc.communicate()

#     lines = out.split("\n")

#     header = lines[0] #.split(",")
#     rows = lines[1:-1]

#     #benchmark.endTimer(cmd, startT)
#     return header, rows 

# def queryList():
#     """Get a list of available arrays"""
#     #f = open("/var/log/scidbpy_log.txt","w+")
#     #f.write("starting queryList")

#     header, rows = querySciDB("list('arrays')")
#     names = [row[1].translate(None, "\"") for row in rows]

#     return names

# def queryDimensions(name):
#     """Determine the dimensions of the specified array"""
#     header, rows = querySciDB("dimensions(%s)" % name)

#     if len(rows) < 2:
#         return 0, 0
#     else:
#         return [int(row[3]) + 1 for row in rows] #bfichter comment: i don't know why the + 1 was put here as the data is not 0 based, not going to change it though because of the large amount of compensating -1's now in the code

# def queryDimensionNames(name):
#     """Determine the dimension names of the specified array"""

#     header, rows = querySciDB("dimensions(%s)" % name)
#     return [row[1].translate(None, "\"") for row in rows]

# def queryAttributeNames(name):
#     """Determine the attribute names of the specified array"""

#     header, rows = querySciDB("attributes(%s)" % name)
#     return [row[1].translate(None, "\"") for row in rows]

# def queryImage(name):
#     """Render an image of the entirety of the specified array, returning a
#     string encoding a PNG image"""

#     width, height = queryDimensions(name)
#     header, rows = querySciDB("scan(%s)" % name)

#     return render.renderPng(width, height, rows)

# def queryTopTile(brain,width,height,slicedepth,volume):
#     #startT = benchmark.startTimer("SciDB: queryTopTile")
#     header, rows = querySciDB2("subarray(%s,%d,%d,%d,%d,%d,%d,%d,%d)" % (brain, 0, 0, slicedepth, volume, width - 1, height - 1,slicedepth,volume))
#     #benchmark.endTimer("SciDB: queryTopTile", startT)
#     return render.renderPngTop(width-1, height-1, rows)
#     #return renderPngDummy()

# def queryFrontTile(brain, width, depth, slicedepth,volume):
#     #startT = benchmark.startTimer("SciDB: queryFrontTile")
#     header, rows = querySciDB2("subarray(%s,%d,%d,%d,%d,%d,%d,%d,%d)" % (brain, 0, slicedepth, 0, volume, depth-1, slicedepth, width - 1, volume))#maybe swap width-1 and height-1
#     #benchmark.endTimer("SciDB: queryFrontTile", startT)
#     return render.renderPngFrontSide(depth-1, width-1, rows)
#     #return renderPngDummy()

# def querySideTile(brain, depth, height, slicedepth,volume):
#     #startT = benchmark.startTimer("SciDB: querySideTile")
#     header, rows = querySciDB2("subarray(%s,%d,%d,%d,%d,%d,%d,%d,%d)" % (brain, slicedepth, 0, 0, volume, slicedepth, height - 1, depth - 1, volume))#maybe swap width-1 and height-1
#     #benchmark.endTimer("SciDB: querySideTile", startT)
#     return render.renderPngFrontSide(height-1, depth-1, rows)
#     #return renderPngDummy()   
    
# def removeArrays(pattern):
#     import re
    
#     for name in queryList():
#         if re.match(pattern, name):
#             querySciDB("remove(%s)" % name) 
