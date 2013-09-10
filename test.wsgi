#!/usr/bin/python

import json
import Image

def application(environ, start_response):
    
   
    start_response('200 OK', [('Content-Type', 'text/html')])
    im = Image.open('/var/www/html/stratification/image.gif')
    rgb_im = im.convert('RGB')
    r, g, b = rgb_im.getpixel((200, 215))
    content = {"r": r,"b" : b, "g" : g}
    return [json.dumps(content)]
    
