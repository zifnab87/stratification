#!/usr/bin/python

import json

def application(environ, start_response):
    
    content = {"name": "Michael"}
    start_response('200 OK', [('Content-Type', 'text/html')])
    return [json.dumps(content)]
