@(playerName: String)

$(function() {
    var socket = new WebSocket("@routes.Application.joinPlayerRoom(playerName).webSocketURL(request)");

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data);

        // Handle errors
        if(data.error) {
            socket.close();
            $("#onError span").text(data.error);
            $("#onError").slideDown(1000);
            return;
        }

        if (data.type === "error") {
            $("#onError span").text(data.message);
            $("#onError").slideDown(1000);
        }
    }

    var closeEvent = function(event) {
        socket.close();
        $("#onError2 span").text("Server closed the connection. The webpage will be refreshed shortly.");
        $("#onError2").slideDown(1000);
        setTimeout(function () {
            window.location.href = '@routes.Application.index()';
        }, 5000);
    }

    socket.onmessage = receiveEvent;
    socket.onclose = closeEvent;
})
