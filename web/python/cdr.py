import sys
import json
import os
import subprocess

def convert_cdr_svg(src, dest):
    try:
        subprocess.run(['libreoffice', '--headless', '--convert-to', 'svg', src, '--outdir', dest])
        pos = dest.rindex("/")
        print('{"svg":"'+dest[pos+1:]+'"}')
    except Exception as e:
        print('发生错误：', str(e))

src_file = ''
dest_file = ''
obj = {}
if len(sys.argv) > 1:
    s = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(s)
    src_file = obj['src']
    pos = src_file.rindex(".")
    dest_file = src_file[0:pos]
    convert_cdr_svg(src_file, dest_file)
else:
    print("没有参数")