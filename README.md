# android-support-jar

### gralde引入
这样引入只会引入java类，不会引用资源

```
implementation 'com.dyhdyh.android.support:appcompat-v7-jar:26.1.0-beta'
```

### RePlugin插件引入appcompat
在插件程序以`compileOnly`引入 

```
compileOnly 'com.dyhdyh.android.support:appcompat-v7-jar:26.1.0-beta'
```