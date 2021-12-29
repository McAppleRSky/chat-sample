<html>
<head>
    <meta charset="UTF-8"/>
    <title>L4.1: Jetty WebSocket Chat</title>
    <script type="text/javascript">
        var ws;

        function init() {
            ws = new WebSocket("ws://localhost:8080/chat");
            ws.onopen = function (event) {

            }
            ws.onmessage = function (event) {
                var $textarea = document.getElementById("messages");
                $textarea.value = $textarea.value + event.data + "\n";
            }
            ws.onclose = function (event) {

            }
        };

        function sendMessage() {
            var messageField = document.getElementById("message");
            var userName = '${login}';
              // document.getElementById("username");
            let outgoingMessage = userName + ":" + messageField.value;
            ws.send(outgoingMessage);
            messageField.value = '';
        }
    </script>
</head>
<body onload="init();">
<div id="body">
    <div id="menu">
        <p class="welcome">
            Welcome, ${login}
            <!-- <input id="username" value="anonymous"/> -->
        </p>

        <div style="clear: both"></div>
    </div>

    <div id="chatbox">
        <textarea id="messages" rows="20" cols="50" readonly="readonly"></textarea>
    </div>

    <form name="message" action="">
        <input name="usermsg" type="text" id="message" size="40"/>
        <input type="button" name="submit" value="Send..." onclick="sendMessage();"/>
    </form>
</div>
</body>
</html>
