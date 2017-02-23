一 View类的四个构造函数
- **四个构造方法**
```java
 构造1：public View(Context context);
 构造2：public View(Context context, @Nullable AttributeSet attrs)
 构造3：public View(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
 构造4：public View(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
```
先说一下View的四个构造方法，，第二个方法，其中attrs参数就是我们在XML中定义控件的属性（包含自定义的属性），其实第二个构造函数也是调用第三个构造函数，这里我在第三个参数传入R.attr.customViewStyle，第三个方法的第三个参数defStyleAttr的意义是从APP或者Activity的Theme中设置的该控件的属性的默认值，如下：
这是我再attrs文件中定义的属性，也就是上面构造方法中传入的参数

构造函数1

when creating a view from code.
构造函数1是提供给我们在代码中生成控件使用的

构造函数2

This is called when a view is being constructed from an XML file, supplying attributes that were specified in the XML file.
构造函数2是在XML布局文件中插入控件使用的，且用this()调用第构造函数3，构造函数3用this()调用构造函数4。

构造函数3和构造函数4和主题有关

defStyleAttr

An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Can be 0 to not look for defaults.
比如:
```xml
<item name="Android:textViewStyle">@style/CustomTextStyle</item>
```
defStyleRes

A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme. Can be 0 to not look for defaults.
比如
```xml
<item name="android:textColor">@android:color/white</item>
```

属性优先级

xml定义 > xml的style定义 > defStyleAttr > defStyleRes > theme

对属性优先级举例解释：

```xml
<attr name="customViewStyle" format="reference" />
```
然后我们需要在App 或者Activity的Theme中设置它的值
```xml
<style name="AppTheme" parent="AppBaseTheme">  
       <!-- All customizations that are NOT specific to a particular API-level can go here. -->  
       <item name="customViewStyle">@style/custom_view_style</item>  
</style>
```
其中的custom_view_style
```
<style name="custom_view_style">  
       <item name="circleWidth">8dp</item>  
</style>
```
这样当你的XML文件中没有给该控件的circleWidth定义值的时候，默认值就是8dp。
注意：
```java
TypedArray array=context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);  
```
该方法的第四个参数defStyleRes，可以直接传入自定义的style,如果defStyleAttr为0,defStyleRes才会起作用。
android控件获取属性值的优先顺序：
1.在XMl文件中直接定义；
2.在XMl文件引用的style;
3.就是从如上所说的defStyleAttr中取值；
4.从defStyleRes取值；
5.从Activity或者Application的Theme中取值；