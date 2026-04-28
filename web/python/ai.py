import sys
import json
import os
import subprocess

def convert_ai_svg(src, dest):
    try:
        subprocess.run(['inkscape', '-d', '1000', '-o', dest, src], check=True)
        pos = dest.rindex("/")
        print('{"svg":"'+dest[pos+1:]+'"}')
    except Exception as e:
        print('发生错误：', str(e))

src_file = '/Users/a1/kylin/imgs/docs/53679b8b-1e2a-43ce-b8f6-08157dab67f8.ai'
dest_file = '/Users/a1/kylin/222.svg'
obj = {}
if len(sys.argv) > 1:
    s = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(s)
    src_file = obj['src']
    pos = src_file.rindex(".")
    dest_file = src_file[0:pos]
    pos = dest_file.rindex("/")
    dest_file = dest_file + '/' + dest_file[pos+1:] + '.svg'
    convert_ai_svg(src_file, dest_file)
else:
    print("没有参数")
    convert_ai_svg(src_file, dest_file)
