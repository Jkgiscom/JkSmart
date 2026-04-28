from psd_tools import PSDImage
from PIL import Image
import sys
import json

obj = {}
if len(sys.argv) > 1:
    str = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(str)
else:
    print("没有参数")

src_file = obj['src']
pos = src_file.rindex(".")
dest_file = src_file[0:pos]
pos = dest_file.rindex("/")
key = dest_file[pos+1:]

psd = PSDImage.open(src_file)
try:
    psd.composite().save(dest_file+'/'+key+'.png')
    print('{"jpg":"'+key+'.png"}')
except:
    psd.composite().save(dest_file+'/'+key+'.jpg')
    img = Image.open(dest_file+'/'+key+'.jpg')
    if img.mode!='RGB': #CMYK':
        img = img.convert('RGB')
    img.save(dest_file+'/'+key+'.png', 'PNG')
    print('{"jpg":"'+key+'.png"}')

