#!/usr/bin/python

import json
import Image
#from scidbpy import interface, SciDBQueryError, SciDBArray
#sdb = interface.SciDBShimInterface('http://localhost:8080')


def application(environ, start_response):
    
   
    start_response('200 OK', [('Content-Type', 'json/image')])
    im = Image.open('/var/www/html/stratification/image.jpeg')
    rgb_im = im.convert('RGB')
    width, height = im.size
    width = width - 180
    height = height - 180
    bitmap = [[x for x in xrange(3)] for x in xrange(width*height)]
    count = 0
    f = open('/var/www/html/stratification/imagefile','w')
    f.write(str("i,j,r,g,b\n"))
    for i in range(height):
        for j in range(width):           
            r,g,b = rgb_im.getpixel((j,i))
            bitmap[count][0] = r
            bitmap[count][1] = g
            bitmap[count][2] = b
            count = count + 1
            f.write(str(i)+","+str(j)+","+str(r)+","+str(g)+","+str(b)+"\n")
    return [json.dumps(bitmap)]

