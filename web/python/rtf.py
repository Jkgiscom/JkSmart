import sys
import json
import os

obj = {}
if len(sys.argv) > 1:
    str = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(str)
else:
    print("没有参数")

# with open(obj['src'], 'rb') as f1:
#     doc = Rtf15Reader.read(f1)
#     for elem in doc.content:
#         print(elem.content)

# f1.clsoe()

src_file = obj['src']
pos = src_file.rindex(".")
dest_file = src_file[0:pos]
# print(f'libreoffice --headless --convert-to pdf {src_file}')
os.system(f'libreoffice --headless --convert-to pdf {src_file} --outdir {dest_file}')

pos = dest_file.rindex("/")
print('{"pdf":"'+dest_file[pos+1:]+'"}')