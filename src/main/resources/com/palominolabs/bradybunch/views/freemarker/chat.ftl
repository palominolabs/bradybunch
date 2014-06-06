<#-- @ftlvariable name="" type="com.example.views.ChatView" -->
<!DOCTYPE html>

<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="/assets/js/jquery.atmosphere.js"></script>
<script type="text/javascript" src="/assets/js/application.js"></script>

<html>

<head>
    <meta charset="utf-8">
    <title>Atmosphere Chat</title>

    <style>
        * {
            font-family: tahoma;
            font-size: 12px;
            padding: 0px;
            margin: 0px;
        }

        p {
            line-height: 18px;
        }

        div {
            width: 500px;
            margin-left: auto;
            margin-right: auto;
        }

        #content {
            padding: 5px;
            background: #ddd;
            border-radius: 5px;
            border: 1px solid #CCC;
            margin-top: 10px;
        }

        #header {
            padding: 5px;
            background: #f5deb3;
            border-radius: 5px;
            border: 1px solid #CCC;
            margin-top: 10px;
        }

        #input {
            border-radius: 2px;
            border: 1px solid #ccc;
            margin-top: 10px;
            padding: 5px;
            width: 400px;
        }

        #status {
            width: 88px;
            display: block;
            float: left;
            margin-top: 15px;
        }
    </style>
</head>
<body>

<h1>Hello, ${person.email?html}!</h1>

<div id="content"></div>
<div>
    <span id="status">Connecting...</span>
    <input type="text" id="input" disabled="disabled"/>
</div>

</body>
</html>
