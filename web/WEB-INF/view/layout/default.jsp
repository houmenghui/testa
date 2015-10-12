<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/7/10
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html language="en" ng-app="transCat">
<head>
    <title></title>

    <!--[if lt IE 9]>
    <script src="http://apps.bdimg.com/libs/html5shiv/3.7/html5shiv.min.js"></script>
    <![endif]-->
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="/DemoTest/js/angular/angular.min.js" ></script>
    <script src="/DemoTest/js/angular/angular-route.js" ></script>
    <script src="/DemoTest/js/controller.js" ></script>
    <script src="/DemoTest/js/app.js" ></script>
    <%--  <script src="/DemoTest/js/bootstrap/js/bootstrap.min.js" ></script>
        <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js" ></script>


        <link href="/DemoTest/js/bootstrap/css/bootstrap.min.css">--%>

    <link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">

    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>




</head>
<body>
<nav class="navbar navbar-default" role="navigation">
    <div class="navbar-header">
        <a class="navbar-brand" href="#">移联支付</a>
    </div>
    <div>
        <ul class="nav navbar-nav">
            <li><a href="#">首页</a></li>
            <li><a href="../transInfo/default#/transList">交易查询</a></li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    交易查询
                    <b class="caret"></b>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="#">交易查询</a></li>
                    <li><a href="#">交易查询</a></li>
                    <li><a href="#">交易查询</a></li>

                </ul>
            </li>
        </ul>
    </div>
</nav>

<div ng-view>


    </div>
</body>
</html>
