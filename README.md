[![Build Status](https://img.shields.io/badge/build-passing-greensvg?style=flat)](https://app.circleci.com/pipelines/github/webfirmframework/wff?branch=master&filter=all)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/410601e16dc54b0a973c03845ad790c2)](https://www.codacy.com/app/webfirm-framework/wff?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=webfirmframework/wff&amp;utm_campaign=Badge_Grade)
[![Stackoverflow](https://img.shields.io/badge/stackoverflow-wffweb-orange.svg)](https://stackoverflow.com/questions/tagged/wffweb)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.webfirmframework/wffweb/badge.svg)](https://search.maven.org/#artifactdetails%7Ccom.webfirmframework%7Cwffweb%7C12.0.5%7Cjar)
[![javadoc](https://javadoc.io/badge2/com.webfirmframework/wffweb/javadoc.svg)](https://javadoc.io/doc/com.webfirmframework/wffweb)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)


# wffweb
wffweb is one of the modules of webfirmframework. It's an open source java framework for real time application development which can generate html5 and css3 from java code, [read more...](https://webfirmframework.github.io/)


#### [Register in wff hub for template reference and more!](http://hub.webfirmframework.com). It's built by wffweb-12.x.x
#### [check out main features of wffweb](https://www.youtube.com/watch?v=UWoNliHOy6A)
##### [check out wffweb sample projects](https://github.com/webfirmframework/minimal-production-ready-projects)


##### check out [this sample project](https://github.com/webfirmframework/wffweb-demo-deployment)


(For the survival of this framework, some ads are shown in [webfirmframework.github.io](https://webfirmframework.github.io) and [webfirmframework.com](https://webfirmframework.com) web sites. These are temporary ads and will be removed soon. We are really sorry if it causes any inconvenience to your reading.)   

Here are some sample codes

##### Sample1 :-
Since 3.0.9 or later you can use functional style coding as follows. This is the recommended coding style vs anonymous coding style.
~~~
Html rootTag = new Html(null).give(html -> {
       	 
    new Head(html);
       	 
    new Body(html).give(body -> {           	 
        new NoTag(body, "Hello World");           	 
    });
       	 
});
// prepends the doc type <!DOCTYPE html>
rootTag.setPrependDocType(true);
System.out.println(rootTag.toHtmlString(true)); 
~~~

or the same in few lines

~~~
Html html = new Html(null);
new Head(html);
Body body = new Body(html);
new NoTag(body, "Hello World");


// prepends the doc type <!DOCTYPE html>
html.setPrependDocType(true);
System.out.println(html.toHtmlString(true)); 
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
System.out.println(div.toHtmlString()); 
~~~
prints :- 
~~~
<div></div>
~~~

##### Sample3 :-
~~~
Div rootTag = new Div(body).give(div -> {
    new Div(div);
    new Div(div);
});
System.out.println(rootTag.toHtmlString()); 
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
System.out.println(div.toHtmlString()); 
~~~
prints :- 
~~~
<div width="50px"></div>
~~~

##### Sample5 :-
~~~
Div div = new Div(null, new Style(new BackgroundColor("green")));
System.out.println(div.toHtmlString()); 
~~~
prints :- 
~~~
<div style="background-color: green;"></div>
~~~

##### Sample6 :-
```
final Style paragraphStyle = new Style("color:red");
Html rootTag = new Html(null, new CustomAttribute("some", "val"),
        new Id("htmlId"), new Style("background:white;width:15px"))
                .give(html -> {

                    new Div(html, new Id("outerDivId")).give(div -> {

                        int[] paragraphCount = { 0 };

                        new Div(div).give(div2 -> {

                            new H1(div2).give(h -> {
                                new NoTag(h, "Web Firm Framework");
                            });

                            for (paragraphCount[0] = 1; paragraphCount[0] < 4; paragraphCount[0]++) {
                                new P(div2, paragraphStyle).give(p -> {
                                    new NoTag(p,
                                            "Web Firm Framework Paragraph "
                                                    + paragraphCount);
                                });
                            }

                        });
                    });

                    new Div(html, new Hidden());
                });

paragraphStyle.addCssProperty(AlignContent.CENTER);

System.out.println(rootTag.toHtmlString(true));

```
prints

```
<html some="val" id="htmlId" style="background:white;width:15px;">
<div id="outerDivId">
    <div>
        <h1>Web Firm Framework</h1>
        <p style="color:red;align-content:center;">Web Firm Framework Paragraph 1</p>
        <p style="color:red;align-content:center;">Web Firm Framework Paragraph 2</p>
        <p style="color:red;align-content:center;">Web Firm Framework Paragraph 3</p>
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

System.out.println(html.toHtmlString(true));

```
It will add width 100% in aboutParagraph and will change color to brown, its generated html code will be as follows

```
<html some="val" id="htmlId" style="background:white;width:15px;">
<div id="outerDivId">
    <div>
        <h1>Web Firm Framework</h1>
        <p style="color:brown;align-content:center;width:100.0%;">Web Firm Framework Paragraph 1</p>
        <p style="color:brown;align-content:center;width:100.0%;">Web Firm Framework Paragraph 2</p>
        <p style="color:brown;align-content:center;width:100.0%;">Web Firm Framework Paragraph 3</p>
    </div>
</div>
<div hidden></div>
</html>
```


##### Checkout 

[Refer Developers Guide to get started](https://webfirmframework.github.io/developers-guide-wffweb-3/get-started.html)

[How to resolve wffweb dependency in build tools like maven, ivy, Scala SBT, Leiningen, Grape, Gradle Grails or Apache Buildr](https://webfirmframework.github.io/developers-guide-wffweb-3/how-to-resolve-dependency-in-build-tools.html)

[wffweb released versions](https://webfirmframework.github.io/developers-guide-wffweb-3/wffweb-released-versions.html)

[You can request features or report bugs here](https://github.com/webfirmframework/wff/issues)

Feel free to write us @ tech-support@webfirmframework.com for any assistance.
