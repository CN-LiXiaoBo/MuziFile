## MuziFile
一个简单的分片上传的小项目,打成Jar包可直接使用，目前只实现了本地与Aliyun的分片文件上传

## 安装教程

**拉取仓库**

```xml
git clone https://github.com/Muzi-Bo/MuziFile.git
```

**打开项目**
使用Maven的mvn打成Jar包

**将Jar包导成maven依赖**

```xml
mvn install:install-file -DgroupId=com.sicnu.muzifile -DartifactId=muzi-file -Dversion=1.0.0 -Dfile=C:\Users\25654\Desktop\jar\muzi-file-0.0.1-SNAPSHOT.jar -Dpackaging=jar

```

-Dfile 修改为你的Jar包的路径

**导入依赖**

```xml
<dependency>
    <groupId>com.sicnu.muzifile</groupId>
    <artifactId>muzi-file</artifactId>
    <version>1.0.0</version>
</dependency>

```

## 项目配置

**配置文件存储方式**

0 - 本地保存
1 - AliyunOSS保存

```xml
muzifile.storage-type = 0
```
**配置文件夹保存前缀**

```xml
muzifile.bucket-name=muzifile
```

**配置文件本地保存路径**

```xml
muzifile.local-storage-path=/muzifile
```


如果选择**storageType为1(AliyunOSS)** 继续配置

```xml
#阿里云oss基本配置
muzifile.aliyun.oss.endpoint=
muzifile.aliyun.oss.access-key=
muzifile.aliyun.oss.secret-key=
muzifile.aliyun.oss.bucket=

```

**Redis的配置**

```xml
#配置Redis
spring.redis.host=192.168.56.10
spring.redis.port=6379
```

## 项目使用
```java
  //注入工厂获取对应的uploader
	@Autowired
	private MuziFileFactory muziFileFactory;

	@PostMapping("/test")
	public List<UploadFileResult> test(HttpServletRequest httpServletRequest,UploadFile uploadFile){
		System.out.println(muziFileProperties);
		Uploader uploader = muziFileFactory.getUploader();
		List<UploadFileResult> upload = uploader.upload(httpServletRequest, uploadFile);
		return upload;
	}
```
