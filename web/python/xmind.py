import xmindparser
import json4tree
import sys
import json

obj = {}
if len(sys.argv) > 1:
    str = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(str)
else:
    print("没有参数")


workbook = xmindparser.xmind_to_dict(obj["src"])
tree = json4tree.handler(workbook)
print(tree.results.replace('\n','').replace('\r',''))
