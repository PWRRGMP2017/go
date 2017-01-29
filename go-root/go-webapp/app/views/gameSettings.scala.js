@(playerName: String)

$(function()
{
    // UI

    // States
    var waitingStatus = 'Waiting for invitation.';
    var invitingStatus = 'Waiting for invitation response.';
    var invitedStatus = 'Waiting for the game to start.';
    var state = waitingStatus;

    var gameState = '';

    // Global variables
    var playerName = '@playerName';
    var opponent = '';

    // Some functions
    var disableControls = function(isdisabled)
    {
        $("#inviteButton").prop('disabled', isdisabled);
        $("#playWithBotButton").prop('disabled', isdisabled);
        $("#searchForPlayerButton").prop('disabled', isdisabled);
        $("#boardSizeSelect").prop('disabled', isdisabled);
        $("#komi").prop('disabled', isdisabled);
    }

    var changeStatus = function(newstatus)
    {
        state = newstatus;
        $('#statusLabel').text(newstatus);
    }

    var disableCancel = function(isdisabled)
    {
        $('#cancelButton').prop('disabled', isdisabled);
    }

    var swapSettingsAndGameBoard = function() {
        $('#gameSettings').toggle();
        $('#gameBoard').toggle();
    }

    // Init
    disableControls(false);
    disableCancel(true);
    changeStatus(waitingStatus);

    // Debug
    swapSettingsAndGameBoard();

    // Click events
    $('#inviteButton').click(function()
    {
        opponent = prompt('Enter the player name: ');
        if (opponent != null && opponent !== '')
        {
            changeStatus(invitingStatus);
            disableControls(true);
            disableCancel(false);
            sendInvitation();
        }
        else
        {
            alert('Wrong name!');
        }
    });

    $('#cancelButton').click(function()
    {
        if (state === invitingStatus)
        {
            sendCancelInvitation();
        }
    });

    // WebSocket events
    var socket = new WebSocket("@routes.Application.joinPlayerRoom(playerName).webSocketURL(request)");

    var receiveEvent = function(event)
    {
        var data = JSON.parse(event.data);

        // Handle errors
        if(data.error)
        {
            socket.close();
            $("#onError span").text(data.error);
            $("#onError").slideDown(1000);
            return;
        }

        if (data.type === "error")
        {
            $("#onError span").text(data.message);
            $("#onError").slideDown(1000);
            return;
        }

        // Interpret the messages
        if (data.type === 'invitationResponse')
        {
            if (data.isAccepted)
            {
                alert('Invitation accepted!');
                swapSettingsAndGameBoard();
                changeStatus(waitingStatus);
                disableControls(false);
                disableCancel(true);
            }
            else
            {
                alert('Invitation denied: ' + data.reason);
                changeStatus(waitingStatus);
                disableControls(false);
                disableCancel(true);
            }
            return;
        }

        if (data.type === 'invitation')
        {
            opponent = data.invitingPlayerName;
            var accepted = confirm(
                'You received an invitation from ' + data.invitingPlayerName + '!\n' +
                'Game settings:\n' +
                'Komi = ' + data.komi + '\n' +
                'Board Size = ' + data.boardSize + '\n' +
                'Do you accept the invitation?'
            );

            if (accepted)
            {
                sendInvitationResponse(true, playerName + ' accepted the invitation.');
                changeStatus(invitedStatus);
                disableControls(true);
                disableCancel(true);
            }
            else
            {
                sendInvitationResponse(false, playerName + ' denied the invitation.');
                changeStatus(invitedStatus);
                disableControls(false);
                disableCancel(true);
            }

            
            return;
        }

        if (data.type === 'confirmInvitation')
        {
            alert('The game is about to begin.');
            swapSettingsAndGameBoard();
            changeStatus(waitingStatus);
            disableControls(false);
            disableCancel(true);
            return;
        }

        if (data.type === 'cancelInvitationResponse')
        {
            if (data.success) {
                changeStatus(waitingStatus);
                disableControls(false);
                disableCancel(true);
            }
            else
            {
                alert('Too late to cancel.');
            }
            return;
        }

        if (data.type === 'cancelInvitation')
        {
            alert(opponent + ' canceled the invitation.');
            changeStatus(waitingStatus);
            disableControls(false);
            disableCancel(true);
            return;
        }
    }

    var closeEvent = function(event)
    {
        socket.close();
        $("#onError2 span").text("Server closed the connection. The webpage will be refreshed shortly.");
        $("#onError2").slideDown(1000);
        disableCancel(true);
        disableControls(true);
        setTimeout(function ()
        {
            window.location.href = '@routes.Application.index()';
        }, 5000);
    }

    socket.onmessage = receiveEvent;
    socket.onclose = closeEvent;

    // Communication functions
    var sendInvitation = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'invitation',
            'invitingPlayerName': playerName,
            'invitedPlayerName': opponent,
            'komi': $('#komi').val(),
            'boardSize': $('#boardSizeSelect').val(),
            'isBot': false
        }
        ));
    }

    var sendInvitationResponse = function(accepted, reason)
    {
        socket.send(JSON.stringify(
        {
            'type': 'invitationResponse',
            'isAccepted': accepted,
            'reason': reason
        }
        ));
    }

    var sendCancelInvitation = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'cancelInvitation'
        }
        ));
    }
})
