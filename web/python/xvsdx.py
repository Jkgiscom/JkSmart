import sys
import json
from vsdx import VisioFile
import os

file = '/Users/a1/kylin/imgs/docs/ad3042ed-20e2-41e8-b70c-4530104196a9.vsdx'
obj = {}
if len(sys.argv) > 1:
    s = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(s)
    file = obj['src']
else:
    print("没有参数")

# with VisioFile(file) as visio:
#     for page in visio.pages:
#         for shape in page.shapes:
#             print(shape)

# visio.close_vsdx()

src_file = file
pos = src_file.rindex(".")
dest_file = src_file[0:pos]
# print(f'libreoffice --headless --convert-to pdf {src_file}')
os.system(f'libreoffice --headless --convert-to svg {src_file} --outdir {dest_file}')
pos = dest_file.rindex("/")
print('{"pdf":"'+dest_file[pos+1:]+'"}')