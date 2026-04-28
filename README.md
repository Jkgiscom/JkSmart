# jksmart
多格式文档在线预览服务器
## JkSmart文件服务安装及配置
文件服务器将附件上传与预览独立开来，减少开发的工作量，也便于统一管理附件。
### 安装
```sh
设置jdk1.8+
复制文件夹到指定目录
```

### 配置

打开/Jksoft/smart/server/conf/smart.json文件
```json
{
    "port" : 6606,
    "static_home" : "/Jksoft/smart/web/",
    "data_home" : "/Jksoft/smart/imgs/",
    "python_home" : "/Jksoft/smart/python/",
    "MAX_SIZE" : 400,
    "DELAY" : 100,
    "WAIT_FOR" : 20
}
```

可以配置文件服务器端口、文件存储路径、python文件的职位
> JkSmart支持java和python的混合编程

| 参数 | 值 | 说明 |
| --- | --- | --- |
| port | 端口 | 默认6600 |
| static_home | 程序位置 | |
| data_home | 文件存储位置 | 不建议和程序放在同一目录 |
| python_home | python程序位置 | 支持java与python混合编程 |


`执行 /Jksoft/smart/smart.sh 文件即可运行`

### 使用方法

JkSmart不支持自动创建文件夹
要使用JkSmart首先要到`data_home`所在位置，手动创建目录，如**gt**目录。
然后使用表单上传附件到这个位置。

#### **下载附件**

[http://localhost:6606/gt/文件名](http://localhost:6606/gt/文件名)

#### **预览docx文件，使用如下路径：**

[http://localhost:6606/docx.html?url=gt/文件名](http://localhost:6606/docx.html?url=gt/文件名)

#### **预览xlsx文件，使用如下路径：**

[http://localhost:6606/xlsx.html?url=gt/文件名](http://localhost:6606/xlsx.html?url=gt/文件名)

#### **预览pdf**

[http://localhost:6606/gt/文件名](http://localhost:6606/gt/文件名)

#### **预览其他支持文件格式**
```
doc\xls\ppt\pptx\vsdx\wsp\rar\zip\7z\psd\ai\cdr\rtf\xmind..
```
[http://localhost:6606/gt/cgi?url=文件名](http://localhost:6606/gt/cgi?url=文件名)

### 支持格式
[JkSmart服务器](http://114.132.231.65/)支持的文档格式如下：
> office序列
doc \ docx \ xls \ xlsx \ ppt \ pptx \ vsdx \ rtf

> xmind

> 图形图像
psd \ ai \ cdr

> 压缩文件
zip \ rar \ 7z

> 其他
pdf \ wps


如只需要可执行程序，请点击[这里下载](http://img.qykpzx.cn/ohow/5191851a-d33c-471a-b2e8-b58843eccd83.rar)，请联系客服获得支持。
