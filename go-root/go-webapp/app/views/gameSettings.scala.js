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
    var playerColor = '';
    var gameInfo = {};

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

    var initializeGameBoard = function() {
        $('#resignButton').prop('disabled', false);
        $('#passButton').prop('disabled', true);
        $('#acceptButton').prop('disabled', true);
        $('#resumeButton').prop('disabled', true);

        if (playerColor === 'black') {
            $('#blackPlayerName').html(playerName + ' (Black)');
            $('#whitePlayerName').html(opponent + ' (White)');
        } else {
            $('#blackPlayerName').html(playerName + ' (White)');
            $('#whitePlayerName').html(opponent + ' (Black)');
        }

        // Generate empty board
        var table = '';
        table += '<table class="borderless">';
        for (var i = 0; i < gameInfo.boardSize; ++i)
        {
            table += '<tr>';
            for (var j = 0; j < gameInfo.boardSize; ++j)
            {
                var cross = '';
                if (i == 0 && j == 0)
                {
                    cross = 'cross-top-left';
                }
                else if (i == gameInfo.boardSize-1 && j == 0)
                {
                    cross = 'cross-bottom-left';
                }
                else if (i == gameInfo.boardSize-1 && j == gameInfo.boardSize-1)
                {
                    cross = 'cross-bottom-right';
                }
                else if (i == 0 && j == gameInfo.boardSize-1)
                {
                    cross = 'cross-top-right';
                }
                else if (i == 0)
                {
                    cross = 'cross-top-center';
                }
                else if (i == gameInfo.boardSize-1)
                {
                    cross = 'cross-bottom-center';
                }
                else if (j == 0)
                {
                    cross = 'cross-center-left';
                }
                else if (j == gameInfo.boardSize-1)
                {
                    cross = 'cross-center-right';
                }
                else
                {
                    cross = 'cross-center-center';
                }

                table += '<td class="background-grid '+cross+'">';
                table += '<div id="index'+i+'-'+j+'" class="background-stone"></div>';
                table += '</td>';
            }
            table += '</tr>';
        }
        table += '</table>';
        $('#boardArea').empty().append(table);
    }

    var updateBoard = function(boardData) {
        if (boardData.state === 'BLACKMOVE')
        {
            if (playerColor === 'black')
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', false);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
            else
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
        }
        else if (boardData.state === 'WHITEMOVE')
        {
            if (playerColor === 'black')
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
            else
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', false);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
        }
        else if (boardData.state === 'BLACKMOVETERRITORY')
        {
            if (playerColor === 'black')
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', false);
                $('#resumeButton').prop('disabled', false);
            }
            else
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
        }
        else if (boardData.state === 'WHITEMOVETERRITORY')
        {
            if (playerColor === 'black')
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', true);
                $('#resumeButton').prop('disabled', true);
            }
            else
            {
                $('#resignButton').prop('disabled', false);
                $('#passButton').prop('disabled', true);
                $('#acceptButton').prop('disabled', false);
                $('#resumeButton').prop('disabled', false);
            }
        }
        else
        {
            $('#resignButton').prop('disabled', true);
            $('#passButton').prop('disabled', true);
            $('#acceptButton').prop('disabled', true);
            $('#resumeButton').prop('disabled', true);
        }

        $('#statsArea').html(boardData.stats);

        for (var i = 0; i < boardData.board.length; ++i)
        {
            for (var j = 0; j < boardData.board[i].length; ++j)
            {
                var button = $('#index'+i+'-'+j);
                var data = boardData.board[i][j];
                button.attr('class', 'background-stone');
                var classToAdd = '';
                if (data.field === 'EMPTY')
                {
                    classToAdd = playerColor+'-empty';
                }
                else if (data.field === 'BLACKSTONE')
                {
                    classToAdd = 'black-stone';
                }
                else if (data.field === 'WHITESTONE')
                {
                    classToAdd = 'white-stone';
                }
                else if (data.field === 'WHITETERRITORY')
                {
                    classToAdd = 'white-territory';
                }
                else if (data.field === 'BLACKTERRITORY')
                {
                    classToAdd = 'black-territory';
                }
                else if (data.field === 'NONETERRITORY')
                {
                    classToAdd = playerColor+'-no-territory';
                }
                else if (data.field === 'DEADWHITE')
                {
                    classToAdd = 'white-stone-dead';
                }
                else if (data.field === 'DEADBLACK')
                {
                    classToAdd = 'black-stone-dead';
                }

                button.addClass(classToAdd);

                if (data.possible)
                {
                    button.addClass(classToAdd+'-hover');
                    button.click((function(x,y) {
                        return function() {
                            sendMove(x, y);
                            disableBoard();
                        }
                    })(i,j));
                }
            }
        }
    }

    var disableBoard = function() {
        $('#resignButton').prop('disabled', true);
        $('#passButton').prop('disabled', true);
        $('#acceptButton').prop('disabled', true);
        $('#resumeButton').prop('disabled', true);
        for (var i = 0; i < gameInfo.boardSize; ++i)
        {
            for (var j = 0; j < gameInfo.boardSize; ++j)
            {
                var button = $('#index'+i+'-'+j);
                button.off('click');
            }
        }
    }

    // Init
    disableControls(false);
    disableCancel(true);
    changeStatus(waitingStatus);

    // Debug
    // swapSettingsAndGameBoard();
    // gameInfo = {
    //     boardSize: 19
    // }
    // playerColor = 'black';
    // initializeGameBoard();

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

    $('#resignButton').click(function()
    {
        sendResign();
        disableBoard();
    });

    $('#passButton').click(function()
    {
        sendPass();
        disableBoard();
    });

    $('#acceptButton').click(function()
    {
        sendAccept();
        disableBoard();
    });

    $('#resumeButton').click(function()
    {
        sendResume();
        disableBoard();
    });

    // WebSocket events
    var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;
    var socket = new WS("@routes.Application.joinPlayerRoom(playerName).webSocketURL(request)");

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
                playerColor = 'black';
                swapSettingsAndGameBoard();
                initializeGameBoard();
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
            gameInfo = {
                komi: data.komi,
                boardSize: data.boardSize,
                isBot: false
            };
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
            playerColor = 'white';
            swapSettingsAndGameBoard();
            initializeGameBoard();
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

        if (data.type === 'updatedBoard')
        {
            updateBoard(data);
            return;
        }

        if (data.type === 'gameEnded')
        {
            alert(data.stats);
            swapSettingsAndGameBoard();
            changeStatus(waitingStatus);
            disableControls(false);
            disableCancel(true);
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
        gameInfo.komi = $('#komi').val();
        gameInfo.boardSize = $('#boardSizeSelect').val();
        gameInfo.isBot = false;

        socket.send(JSON.stringify(
        {
            'type': 'invitation',
            'invitingPlayerName': playerName,
            'invitedPlayerName': opponent,
            'komi': gameInfo.komi,
            'boardSize': gameInfo.boardSize,
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

    var sendResign = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'resign'
        }
        ));
    }

    var sendPass = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'pass'
        }
        ));
    }

    var sendAccept = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'acceptTerritory'
        }
        ));
    }

    var sendResume = function()
    {
        socket.send(JSON.stringify(
        {
            'type': 'resumeGame'
        }
        ));
    }

    var sendMove = function(x,y)
    {
        socket.send(JSON.stringify(
        {
            'type': 'move',
            'x': x,
            'y': y
        }
        ));
    }

    var sendRefreshBoard = function()
    {
        socket.send(JSON.stringify({ 'type': 'refreshBoard' }));
    }
})
