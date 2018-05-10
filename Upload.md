# 发布新版本

### 生成jar的命令
```
./gradlew makeJar
```

### 上传
bintray.com 文件上传页面


Target Repository Path

```
/com/dyhdyh/android/support/appcompat-v7-jar/26.1.0/
```

maven-metadata.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<metadata>
  <groupId>com.dyhdyh.android.support</groupId>
  <artifactId>appcompat-v7-jar</artifactId>
  <version>26.1.0</version>
  <versioning>
    <latest>26.1.0</latest>
    <release>26.1.0</release>
    <versions>
      <version>26.1.0</version>
    </versions>
    <lastUpdated>20180510094226</lastUpdated>
  </versioning>
</metadata>

```
上传`maven-metadata.xml`和`appcompat-v7-26.1.0.jar`