<p align="center">
	<a href="https://whaleal.com/"><img src="https://docs.whaleal.com/images/logo1.png" width="45%"></a>
</p>
<p align="center">
	<a href="https://whaleal.com/"><img src="https://docs.whaleal.com/images/logo1.png" width="45%"></a>
</p>
<p align="center">
	<strong>🍬A set of tools that keep Java sweet.</strong>
</p>
<p align="center">
	👉 <a href="https://whaleal.com">https://whaleal.com/</a> 👈
</p>

<p align="center">
	<a target="_blank" href="https://search.maven.org/artifact/com.whaleal.icefrog/icefrog-all">
		<img src="https://img.shields.io/maven-central/v/com.whaleal.icefrog/icefrog-all.svg?label=Maven%20Central" />
	</a>
	<a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
	</a>
	<a href="https://codecov.io/gh/whaleal/icefrog">
		<img src="https://codecov.io/gh/whaleal/icefrog/branch/v1-main/graph/badge.svg" />
	</a>
	<a target="_blank" href='https://github.com/whaleal/icefrog'>
		<img src="https://img.shields.io/github/stars/whaleal/icefrog.svg?style=social" alt="github star"/>
	</a>
</p>



-------------------------------------------------------------------------------

[**🌎English Documentation**](README-EN.md)

-------------------------------------------------------------------------------

## 📚简介
icefrog是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以“甜甜的”。

icefrog中的工具方法来自每个用户的精雕细琢，它涵盖了Java开发底层代码中的方方面面，它既是大型项目开发中解决小问题的利器，也是小型项目中的效率担当；

icefrog是项目中“util”包友好的替代，它节省了开发人员对项目中公用类和公用工具方法的封装时间，使开发专注于业务，同时可以最大限度的避免封装不完善带来的bug。

### 🎁icefrog名称的由来

icefrog ，是上海锦木信息技术有限公司项目底层代码剥离后的开源库，基于其游戏氛围的企业文化，纪念War3 冰蛙大神,起名为 icefrog。

### 🍺icefrog如何改变我们的coding方式

icefrog的目标是使用一个工具方法代替一段复杂代码，从而最大限度的避免“复制粘贴”代码的问题，彻底改变我们写代码的方式。

以计算MD5为例：

- 👴【以前】打开搜索引擎 -> 搜“Java MD5加密” -> 打开某篇博客-> 复制粘贴 -> 改改好用
- 👦【现在】引入icefrog  -> SecureUtil.md5()

icefrog的存在就是为了减少代码搜索成本，避免网络上参差不齐的代码出现导致的bug,同时避免重复造轮子，。

-------------------------------------------------------------------------------

## 🛠️包含组件
一个Java基础工具类，对文件、流、加密解密、转码、正则、线程、XML等JDK方法进行封装，组成各种Util工具类，同时提供以下组件：

| 模块                |     介绍                                                                          |
| -------------------|---------------------------------------------------------------------------------- |
| icefrog-aop         |     JDK动态代理封装，提供非IOC下的切面支持                                              |
| icefrog-bloomFilter |     布隆过滤，提供一些Hash算法的布隆过滤                                                |
| icefrog-cache       |     简单缓存实现                                                                     |
| icefrog-core        |     核心，包括Bean操作、日期、各种Util等                                               |
| icefrog-cron        |     定时任务模块，提供类Crontab表达式的定时任务                                          |
| icefrog-crypto      |     加密解密模块，提供对称、非对称和摘要算法封装                                          |
| icefrog-db          |     JDBC封装后的数据操作，基于ActiveRecord思想                                         |
| icefrog-dfa         |     基于DFA模型的多关键字查找                                                         |
| icefrog-extra       |     扩展模块，对第三方封装（模板引擎、邮件、Servlet、二维码、Emoji、FTP、分词等）            |
| icefrog-http        |     基于HttpUrlConnection的Http客户端封装                                            |
| icefrog-log         |     自动识别日志实现的日志门面                                                         |
| icefrog-script      |     脚本执行封装，例如Javascript                                                      |
| icefrog-setting     |     功能更强大的Setting配置文件和Properties封装                                        |
| icefrog-system      |     系统参数调用封装（JVM信息等）                                                      |
| icefrog-json        |     JSON实现                                                                       |
| icefrog-captcha     |     图片验证码实现                                                                   |
| icefrog-poi         |     针对POI中Excel和Word的封装                                                       |
| icefrog-socket      |     基于Java的NIO和AIO的Socket封装                                                   |
| icefrog-jwt         |     JSON Web Token (JWT)封装实现                                                    |

可以根据需求对每个模块单独引入，也可以通过引入`icefrog-all`方式引入所有模块。

-------------------------------------------------------------------------------

## 📝文档 


-------------------------------------------------------------------------------

## 📦安装

### 🍊Maven
在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>com.whaleal.icefrog</groupId>
    <artifactId>icefrog-all</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 🍐Gradle
```
implementation 'com.whaleal.icefrog:icefrog-all:1.0.0'
```

### 📥下载jar

点击以下链接，下载`icefrog-all-X.X.X.jar`即可：

- [Maven中央库](https://repo1.maven.org/maven2/cn/icefrog/icefrog-all/1.0.0/)

> 🔔️注意
> icefrog 1.x支持JDK8+，对Android平台没有测试，不能保证所有工具类或工具方法可用。
> 所有版本编译起始均为JDK8+

### 🚽编译安装

访问icefrog的github主页：[https://github.com/whaleal/icefrog](https://github.com/whaleal/icefrog) 下载整个项目源码（v1-main或v1-dev分支都可）然后进入icefrog项目目录执行：

```sh
./IceFrog.sh install
```

然后就可以使用Maven引入了。

-------------------------------------------------------------------------------

## 🏗️添砖加瓦

### 🎋分支说明

icefrog的源码分为两个分支，功能如下：

| 分支       | 作用                                                          |
|-----------|---------------------------------------------------------------|
| v1-main | 主分支，release版本使用的分支，与中央库提交的jar一致，不接收任何pr或修改 |
| v1-dev    | 开发分支，默认为下个版本的SNAPSHOT版本，接受修改或pr                 |

### 🐞提供bug反馈或建议

提交问题反馈请说明正在使用的JDK版本呢、icefrog版本和相关依赖库版本。

- [github issue](https://github.com/whaleal/icefrog/issues)


### 🧬贡献代码的步骤
1.在github issues 上找到需要修复的问题，或提出要贡献的特性内容
2. 在github或者Github上fork项目到自己的repo
3. 把fork过去的项目也就是你的项目clone到你的本地
4. 修改代码（记得一定要修改v1-dev分支）并进行相关测试
5. commit后push到自己的库（v1-dev分支）
6. 登录github或Github在你首页可以看到一个 pull request 按钮，点击它，选择自己的dev 分支及本项目的dev 分支，填写一些说明信息，然后提交即可。
7. 等待维护者合并

### 📐PR遵照的原则

icefrog欢迎任何人为icefrog添砖加瓦，贡献代码，不过维护者是一个强迫症患者，为了照顾病人，需要提交的pr（pull request）符合一些规范，规范如下：

1. 注释完备，尤其每个新增的方法应按照Java文档规范标明方法说明、参数说明、返回值说明等信息，必要时请添加单元测试，如果愿意，也可以加上你的大名。
2. icefrog的缩进按照IDEA,IDEA真香，默认（tab）缩进，所以请遵守（不要和我争执空格与tab的问题，这是一个病人的习惯）。
3. 新加的方法不要使用第三方库的方法，icefrog遵循无依赖原则（除非在extra模块中加方法工具）。
4. 请pull request到`v1-dev`分支。icefrog在1.x版本后使用了新的分支：`v1-main`是主分支，表示已经发布中央库的版本，这个分支不允许pr，也不允许修改。

-------------------------------------------------------------------------------


## ⭐欢迎关注

如果你觉得本项目还不错，欢迎持续关注，在此表示感谢^_^。



![whaleal](https://github.com/whaleal/whaleal.github.io/blob/main/images/logo1.png)
