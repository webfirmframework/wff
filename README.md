# wffweb

Web Firm Framework is a java framework to build web applications. The advantage of using this framework is its ability to generate html and css dynamically. In version-1.0.0, it is providing features to generate html & css dynamically and some other sophisticated features for ui development. It also provides some inbuilt support for optimized html/css output. It supports html5 and css3 for the ui generation.  As it is java framework we don’t have to keep any separate html/css files, instead we are keeping java files for ui (for html/css generation). A simple rest api or even servlet support is enough to create web applications with this framework. The performance rate will be very high with applications developed by this framework.

##### Whom is this framework for?
Developers who want to be a designer to develop very complex ui.


##### What are the advantages of this framework over other ui frameworks?
In other ui development frameworks, the developer may face some helpless situations to satisfy some ui features because of their limited control to the developer, in this framework the developer has full control over html & css generation. In other frameworks, the html to java data structure will be different so the developer has to keep different logic in the code, but here it is same so the developer is able to make a one to one mapping with html to java with less modifications. A html to java conversion (and vice versa) tool is also provided in the webfirm website. In some complex ui development cases, we may have to keep a dedicated designer to develop the design html/css and the designer may be unaware of Java, in such cases we can convert these designed html/css to the corresponding webfirm java code. The designer will be able to understand the output html/css even if he/she doesn’t know the java because of the webfirm java code structure. Here are some sample codes

##### Sample1 :-
~~~
Html html = new Html(null) {
       	 
        	Head head = new Head(this);
       	 
        	Body body = new Body(this) {
           	 
            	Blank blank = new Blank(this, "Hello World");
           	 
        	};
       	 
};

System.out.println(html.toHtmlString()); 
~~~
prints the following output
~~~
<!DOCTYPE html>
<html>
<head>
</head>
<body>
Hello World
</body>
</html>
~~~

##### Sample2 :-
~~~
Div div = new Div(null); 
~~~
or 
~~~
Div div = new Div(null) {
};
~~~
prints :- 
~~~
<div></div>
~~~

##### Sample3 :-
~~~
Div div = new Div(null) {
       	 
        	Div div1 = new Div(this);  
       	 
        	Div div2 = new Div(this);
       	 
};
~~~
prints :- 
~~~
<div>
<div>
</div>
<div>
</div>
</div>
~~~
##### Sample4 :-
~~~
Div div = new Div(null, new Width(50, CssLengthUnit.PX));
~~~
prints :- 
~~~
<div width="50px"></div>
~~~

##### Sample5 :-
~~~
Div div = new Div(null, new Style(new BackgroundColor("green")));
~~~
prints :- 
~~~
<div style="background-color: green;"></div>
~~~

##### Sample6 :-
```
        final Style paragraphStyle = new Style("color:red");

        Html html = new Html(null, new CustomAttribute("some", "val"),
                new Id("htmlId"), new Style("background:white;width:15px")) {

            Div outerDiv = new Div(this, new Id("outerDivId")) {

                Div contentDiv = new Div(this) {

                    H1 h1 = new H1(this) {
                        Blank headerContent = new Blank(this,
                                "Web Firm Framework");
                    };

                    CodeExecutor executor = new CodeExecutor(this) {

                        private int paragraphCount;

                        @Override
                        public void execute(AbstractHtml base) {

                            for (paragraphCount = 1; paragraphCount < 4; paragraphCount++) {
                                P aboutParagraph = new P(base, paragraphStyle) {
                                    Blank paragraphContent = new Blank(this,
                                            "Web Firm Framework Paragraph "
                                                    + paragraphCount);
                                };
                            }

                        }
                    };

                };
            };

            Div hiddenDiv = new Div(this, new Hidden(null));
        };
        
System.out.println(html.toHtmlString());
```
prints

```
<html some="val" id="htmlId" style="background: white; width: 15px;">
   <div id="outerDivId">
      <div>
         <h1>Web Firm Framework</h1>
         <p style="color: red;">Web Firm Framework Paragraph 1</p>
         <p style="color: red;">Web Firm Framework Paragraph 2</p>
         <p style="color: red;">Web Firm Framework Paragraph 3</p>
      </div>
   </div>
   <div hidden></div>
</html>
```
and we can add/change styles later, eg:-
```
paragraphStyle.addCssProperties(new WidthCss(100, CssLengthUnit.PER));

Color color = (Color) paragraphStyle
        .getCssProperty(CssNameConstants.COLOR);
        
color.setCssValue(CssColorName.BROWN.getColorName());

System.out.println(html.toHtmlString());

```
It will add width 100% in aboutParagraph and will change color to brown, its generated html code will be as follows

```
<html some="val" id="htmlId" style="background: white; width: 15px;">
   <div id="outerDivId">
      <div>
         <h1>Web Firm Framework</h1>
         <p style="color: brown; width: 100.0%;">Web Firm Framework Paragraph 1</p>
         <p style="color: brown; width: 100.0%;">Web Firm Framework Paragraph 2</p>
         <p style="color: brown; width: 100.0%;">Web Firm Framework Paragraph 3</p>
      </div>
   </div>
   <div hidden></div>
</html>
```


##### How to resolve dependency in maven?
Add the following code in your pom.xml file. 
```
<repositories>
	<repository>
		<id>wff</id>
		<url>https://github.com/webfirmframework/wff/raw/master</url>
	</repository>
</repositories>

<dependencies>
	<dependency>
		<groupId>com.webfirmframework</groupId>
		<artifactId>wffweb</artifactId>
		<version>1.0.0-SNAPSHOT</version>
	</dependency>
</dependencies>
```

