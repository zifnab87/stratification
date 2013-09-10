#!/usr/bin/python

import json
import Image

def application(environ, start_response):
    
   
    start_response('200 OK', [('Content-Type', 'json/image')])
    im = Image.open('/var/www/html/stratification/image.jpeg')
    rgb_im = im.convert('RGB')
    width, height = im.size
    bitmap = [[x for x in xrange(3)] for x in xrange(width*height)]
    count = 0
    for i in range(height):
        for j in range(width):           
            r,g,b = rgb_im.getpixel((j,i))
            bitmap[count][0] = r
            bitmap[count][1] = g
            bitmap[count][2] = b
            count = count + 1
    return [json.dumps(bitmap)]
    
