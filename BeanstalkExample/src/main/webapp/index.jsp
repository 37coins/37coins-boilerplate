<%@ page import="java.util.Date"%>
<%--,java.util.*,org.apache.commons.codec.binary.*, java.net.*, org.json.simple.*" %> --%>

<!DOCTYPE html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]> <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]> <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:og="http://ogp.me/ns#"
	xmlns:fb="https://www.facebook.com/2008/fbml">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta property="og:image"
	content="//<%=System.getProperty("PARAM1")%>/img/sns/babytree.jpg" />
<title>Tree Planet Game - Spirits of the Forest</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">
<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

<link rel="stylesheet"
	href="//<%=System.getProperty("PARAM1")%>/css/normalize.css">
<link rel="stylesheet"
	href="//<%=System.getProperty("PARAM1")%>/css/main.css">

<%-- <%
String fbSecretKey = "efqec6fdedd17a64055712dcc7d81f58";
String fbAppId = "116041890091";
String fbCanvasPage = "http://apps.facebook.com/stupidgame/";
String fbCanvasUrl = "http://stupidgame.com:8090/stupidgame/";
String accessToken;
List<JSONObject> data = new ArrayList<JSONObject>();
   if(request.getParameter("signed_request") != null) {
    //it is important to enable url-safe mode for Base64 encoder 
    Base64 base64 = new Base64(true);
    //split request into signature and data
    String[] signedRequest = request.getParameter("signed_request").split("\\.");
    //parse signature
    //String sig = new String(base64.decode(signedRequest[0].getBytes("UTF-8")));
    //parse data and convert to json object
    //data.add((JSONObject)JSONValue.parse(new String(base64.decode(signedRequest[1].getBytes("UTF-8")))));
    //check if user authorized the app
    for (int i = 0 ; i < signedRequest.length; i++){
    	data.add((JSONObject)JSONValue.parse(new String(base64.decode(signedRequest[i].getBytes("UTF-8")))));
    }
   }	
%> --%>


<script type="text/javascript">
		window.options = {
			cdnPath: '<%=System.getProperty("PARAM1")%>',
			fbAppId: '<%=System.getProperty("PARAM2")%>',
			signedRequest: '<%=request.getParameter("signed_request")%>',
			oaSubDomain: '<%=(System.getProperty("PARAM3")!=null)?System.getProperty("PARAM3").split("\\.")[0]:null%>',
			oaProviders:['facebook','twitter'],
			oaCssThemeUri: document.location.protocol + '//oneallcdn.com/css/api/socialize/themes/buildin/signin/large-v1.css',
			oaGridSizeX: '2',
			oaGridSizeY: '1',
			oaCallbackUri: document.location.protocol + '//<%=System.getProperty("PARAM5")%>/commrest/session?redirect='+document.location.protocol + '//' + document.location.host,
			gaUA: '<%=System.getProperty("PARAM4")%>',
			i18nResPath: '//<%=System.getProperty("PARAM1")%>/locales/__lng__/__ns__.json',
			serverTime: '<%=new java.util.Date().getTime()%>',
			commRest: '<%=System.getProperty("PARAM5")%>'
	};
</script>
<script data-main="//<%=System.getProperty("PARAM1")%>/main"
	src="//<%=System.getProperty("PARAM1")%>/lib/require-jquery.js"></script>
</head>
<body>
	<!--[if lt IE 8]>
		<p class="chromeframe">You are using an outdated browser. <a href="http://browsehappy.com/">Upgrade your browser today</a> or <a href="http://www.google.com/chromeframe/?redirect=true">install Google Chrome Frame</a> to better experience this site.</p>
		<![endif]-->
	<div id="container"></div>

</body>
</html>