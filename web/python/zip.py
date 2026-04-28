import zipfile
import sys
import json

obj = {}
if len(sys.argv) > 1:
    str = bytes.fromhex(sys.argv[1]).decode('utf-8')
    obj = json.loads(str)
else:
    print("没有参数")

with zipfile.ZipFile(obj["src"], 'r') as zip_ref:
    file_list = zip_ref.namelist()

new_names = []

for file in file_list:
    new_name = file.encode('cp437').decode('gbk')
    new_names.append(new_name)

# 打印文件列表
#for file_name in file_list:
#    print(file_name)

print(new_names)


def support_gbk(zip_file: ZipFile):
    name_to_info = zip_file.NameToInfo.copy()
    for name, info in name_to_info.items():
        real_name = name.encode('cp437').decode('gbk')
        if real_name != name:
            info.filename = real_name
            del zip_file.NameToInfo[name]
            zip_file.NameToInfo[real_name] = info
        return zip_file

#with support_gbk(ZipFile(obj['src'],'r')) as zip:
#    print(zip.namelist())
#    dest = obj["src"][0:len(obj["src"])-4]
#    zip.extractall(dest)

zip_ref.close()