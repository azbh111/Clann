# Clann

Miencraft Bukkit插件,我所有插件都依赖它

------

## 提供一个插件框架

>本插件也是基于此框架开发的
>
>基于此框架可快速进行插件开发

- 自动生成配置文件
- 扫描插件jar文件所有类--->静态块会被执行
- 自动加载配置文件
- 自动注册插件Bean类
- 提供Module模块样板,可基于样板进行模块,所有模块会自动实例化
- 为每个模块提供单独的配置文件、数据文件接口,并会自动加载配置、自动加载数据
- 扫描注解,根据注解优先级自动实例化指定类

------

## 提供大量工具类,大大简化插件开发过程

- **BeanUtils**

  >提供接口,在Bean和JSON之间进行转换,大大简化插件数据存储过程

- **CommandUtils**

  >提供接口,让异步线程执行无法异步执行的指令

- **DataUtils**:

  - 二进制数据与十六进制字符串相互转换
  - AES加密与解密
  - 对象序列化与反序列化
  - GZIP与UNGZIP
  - MD5摘要计算

- **FileUtils**

  - 将数据直接写入文件
  - 从文件直接读取所有数据
  - 创建文件、文件夹
  - 遍历目录内所有文件
  - 文件ZIP压缩与解压
  - 遍历删除目录
  - 根据随机UUID对文件进行存档

- **HttpClient**

  >提供最基本的Get与Post请求

- **JSONUtils**

  >将JSON与File进行包装,提供接口直接从文件存取JSON

- **ObscureUtils**

  基于ASMShooterData-1.7.10.xml文件,建立属性、方法的混淆映射

  提供接口对Minecraft 1.7.10属性、方法进行反射查询,传入非混淆名,根据混淆映射,自动获取混淆后对应的属性和方法

- **PackageScanner**

  >提供包扫描接口,对所有插件、指定插件、指定包提供类扫描功能

- **PermissionUtils**

  > 对Vault插件的权限接口进行封装

- **ReflectUtils**

  > 基于ObscureUtils,对反射操作进行封装

- **StringUtils**

  - 字符串编码类型检测
  - 半角、全角转换

------

## 为其他插件提供大量API

- **ItemAPI**

  - 对**物品**属性进行快速操作
  - 对**物品**的NBT进行肆意修改
  - **物品**、**NBTTagCompound**、**JSON**三者之间任意转换,简化存储过程

- **LanguageAPI**

  > 在服务端建立Mod的语言映射,并提供查询接口

- **NBTAPI**

  > 对Minecraft已混淆的NBT类进行封装

- **PlayerAPI**

  > 封装部分ForgeAPI

- **ServerTick**

  - 记录服务器时钟
  - 提供一些便捷操作

- **SqlServer **

  > Log插件的依赖

- **YmlConfiguration**

  > 对Bukkit Yml配置文件类进行封装

------

## 提供模块,增强其他插件功能

- **AKF**模块
  - 对玩家一些列操作进行编号,形成一个操作序列
  - 基于KMS模式匹配算法,快速判断玩家是否处于AFK状态
- **Command**模块
  - 通过注解,提供便捷的指令编程接口
  - 指令执行过程中,可通过CEException抛出异常,异常信息将会返回给指令执行者