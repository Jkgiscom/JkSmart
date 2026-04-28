import rarfile
import sys
import json

obj = {}
if len(sys.argv) > 1:
    str = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(str)
else:
    print("没有参数")

dest = obj["src"][0:len(obj["src"])-4]

f = rarfile.RarFile(obj["src"])
print(f.namelist())
#f.extractall(dest)
f.close()