import json
import Image
import sys
sys.path.append("/var/www/html/stratification")
import scidb

#from scidbpy import interface, SciDBQueryError, SciDBArray
#sdb = interface.SciDBShimInterface('http://localhost:8080')


def application(environ, start_response):
    
   
    start_response('200 OK', [('Content-Type', 'text/html')])
    bitmap = scidb.querySciDB("SELECT * FROM subarray(Michalis,0,0,1024,1024)")
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
    return str(bitmap)

if __name__ == '__main__':
   #start_response('200 OK', [('Content-Type', 'text/html')])
   bitmap = scidb.querySciDB("SELECT * FROM subarray(Michalis,0,0,1024,1024)")
   print bitmap
