BradyBunchRails = {};
BradyBunchRails.Brady = {};

BradyBunchRails.Brady.$ = undefined;
BradyBunchRails.Brady._container = undefined;
BradyBunchRails.Brady._atmosphere = undefined;
BradyBunchRails.Brady._email = undefined;
BradyBunchRails.Brady._name = undefined;
BradyBunchRails.Brady._switchSourceButton = undefined;
BradyBunchRails.Brady._videoEl = undefined;
BradyBunchRails.Brady._canvasEl = undefined;
BradyBunchRails.Brady._ctx = undefined;
BradyBunchRails.Brady._localMediaStream = undefined;
BradyBunchRails.Brady._defaultImage = '/assets/images/logo.png';
BradyBunchRails.Brady._videoSourceIds = [];
BradyBunchRails.Brady._currentVideoSourceIndex = [];
BradyBunchRails.Brady._roomData = {};
BradyBunchRails.Brady._snapshotQueues = {};
BradyBunchRails.Brady._mySnapshot = undefined;
BradyBunchRails.Brady._reapIntervalId = undefined;
BradyBunchRails.Brady._snapIntervalId = undefined;
BradyBunchRails.Brady._squareAssignments = [];
BradyBunchRails.Brady._users = {};
BradyBunchRails.Brady._messageBuffer = "";

BradyBunchRails.Brady.initialize = function ($, bradyContainer, websocketUrl, email, name) {
    var brady = BradyBunchRails.Brady,
        users = JSON.parse($('#brady-screen').attr('data-users'));

    brady.$ = $;
    brady._container = $(bradyContainer);

    navigator.getUserMedia = ( navigator.getUserMedia ||
        navigator.webkitGetUserMedia ||
        navigator.mozGetUserMedia ||
        navigator.msGetUserMedia);

    BradyBunchRails.Brady._videoEl = brady._container.find('video')[0];
    BradyBunchRails.Brady._canvasEl = brady._container.find('canvas')[0];
    BradyBunchRails.Brady._websocketUrl = websocketUrl;
    BradyBunchRails.Brady._email = email;
    BradyBunchRails.Brady._name = name;
    BradyBunchRails.Brady._ctx = BradyBunchRails.Brady._canvasEl.getContext('2d');

    // Set users from what the server provides
    users.forEach(function(user) {
        brady._squareAssignments.push(user.email);
        brady._users[user.email] = user.name;
    });
    brady._squareAssignments.sort();
    brady.assignSquares();

    brady.subscribe();

    brady._reapIntervalId = setInterval(brady.reap, 11000);
};

BradyBunchRails.Brady.subscribe = function () {
    var brady = BradyBunchRails.Brady;

    console.log("Subscribing websocket");
    BradyBunchRails.Brady._atmosphere = $.atmosphere;

    brady._channel = brady._atmosphere.subscribe({
        url: brady._websocketUrl,
        contentType: 'application/json',
        logLevel: 'debug',
        transport: 'websocket',
        reconnectInterval: 30000,
        connectTimeout: 10000,
        onOpen: function(msg) {
            $(window).on('beforeunload', brady.leave);
        },
        onReconnect: function() {
            console.log("Reconnected");
        },
        onError: function() {
            console.log("WebSocket error");
        },
        onMessage: brady.handleMessage
    });
};

BradyBunchRails.Brady.resubscribe = function() {
    var brady = BradyBunchRails.Brady;

    // Unsubscribing hangs because it tries to cleanly tear down the connection
    // Forcibly delete it instead :-/
    console.log("Unsubscribing websocket");
    delete BradyBunchRails.Brady._atmosphere;

    brady.subscribe();
};

BradyBunchRails.Brady.handleMessage = function(msg) {
    // Split of the message size
    var brady = BradyBunchRails.Brady,
        body = msg.responseBody.split('|')[1],
        message;

    if (brady._messageBuffer === undefined) {
        // New message
        try {
            message = $.parseJSON(body);
        } catch (e) {
            // Not a full message
            if (e.name === "SyntaxError" && e.message === "Unexpected end of input") {
                // Store it in the buffer
                brady._messageBuffer = body;
                return;
            } else {
                // Unexpected error
                console.log("Error parsing JSON; clearing buffer: ", e);
                console.log("Buffer was empty");
                console.log("New message: " + body);
                brady._messageBuffer = undefined;
                return;
            }
        }
    } else {
        // Continuation; add to the buffer
        brady._messageBuffer += body;

        // See if it parses now
        try {
            message = $.parseJSON(brady._messageBuffer);
            // Successfully parsed, clear the buffer
            brady._messageBuffer = undefined;
        } catch (e) {
            if (e.name === "SyntaxError" && e.message === "Unexpected end of input") {
                // Still not a full message
                return;
            } else {
                // Unexpected error
                console.log("Error parsing JSON; clearing buffer: ", e);
                console.log("Buffer was: " + brady._messageBuffer);
                brady._messageBuffer = undefined;
                return;
            }
        }
    }

    if (message.type === 'update_room') {
        brady.handleRoomUpdate(message)
    } else if (message.type === 'leave') {
        brady.handleLeave(message);
    }
};

BradyBunchRails.Brady.start = function () {
    if (typeof MediaStreamTrack === 'undefined') {
        alert('This browser does not support MediaStreamTrack.\n\nTry Chrome.');
    } else {
        MediaStreamTrack.getSources(BradyBunchRails.Brady.processSources);
    }
};

BradyBunchRails.Brady.stop = function () {
    var brady = BradyBunchRails.Brady;

    clearInterval(brady._reapIntervalId);
    clearInterval(brady._snapIntervalId);
    brady._localMediaStream.stop();
    brady.leave();
    brady._snapshotQueues = {};
    brady._roomData = {};
};

BradyBunchRails.Brady.leave = function () {
    var brady = BradyBunchRails.Brady,
        data = {
            type: 'leave',
            email: brady._email,
            snapshot: null
        };
    brady._channel.push($.stringifyJSON(data));
};

BradyBunchRails.Brady.startVideo = function () {
    var brady = BradyBunchRails.Brady;

    navigator.getUserMedia({
        video: {
            mandatory: {
                minHeight: 100,
                minWidth: 200
            },
            optional: [
                {sourceId: brady._videoSourceIds[brady._currentVideoSourceIndex]}
            ]
        }
    }, function (stream) {
        brady._videoEl.src = window.URL.createObjectURL(stream);
        brady._localMediaStream = stream;
        setTimeout(brady.snap, 500);
        brady._snapIntervalId = setInterval(brady.snap, 10000);
        brady.$(brady).trigger('videostart');
    }, function () {
    });
};

BradyBunchRails.Brady.processSources = function (sourceInfos) {
    var brady = BradyBunchRails.Brady,
        sourceInfo;

    for (var i = 0; i != sourceInfos.length; ++i) {
        sourceInfo = sourceInfos[i];
        if (sourceInfo.kind === 'video') {
            brady._videoSourceIds.push(sourceInfo.id);
        }
    }

    brady.startVideo();
};

BradyBunchRails.Brady.takeSnapshot = function () {
    var brady = BradyBunchRails.Brady;

    if (brady._localMediaStream) {
        brady._ctx.drawImage(brady._videoEl, 0, 0, 300, 150);
        return brady._canvasEl.toDataURL('image/jpeg');
    }
};

BradyBunchRails.Brady.snap = function () {
    var brady = BradyBunchRails.Brady;
    console.log("Taking snapshot..." + new Date().toUTCString());

    brady._mySnapshot = brady.takeSnapshot();

    var data = {
        type: 'update_room',
        email: brady._email,
        snapshot: brady._mySnapshot
    };
    brady._channel.push($.stringifyJSON(data));
};

BradyBunchRails.Brady.handleRoomUpdate = function (data) {
    var brady = BradyBunchRails.Brady,
        email = data.email,
        squareId = brady._squareAssignments.indexOf(email),
        square;
    console.log(email + ' sent us an update');

    // Assign this new person a square
    if (squareId == -1) {
        console.log(email + " is someone we don't know");
        brady._squareAssignments.push(email);
        brady._squareAssignments.sort();
        squareId = brady._squareAssignments.indexOf(email);
    }

    square = brady._container.find('.brady[square-id="' + squareId + '"]').first();

    // Initialize their snapshot queue
    if (brady._snapshotQueues[email] == null) {
        console.log(email + " is new, making snapshot queue");
        brady._snapshotQueues[email] = new BradyBunchRails.SnapshotQueue();
    }

    brady._snapshotQueues[email].add({
        snapshot: data.snapshot,
        md5: CryptoJS.MD5(data.snapshot).toString()
    });
    if (brady.snapshotQueueIsConstant(brady._snapshotQueues[email])) {
        console.log(email + " became idle");
        square.addClass('idle');
    } else {
        square.removeClass('idle');
    }

    brady.updateSquare(square, email, data.snapshot);
    brady.addHappyDance(square, email);
    brady.recordUpdate(data);
};

// Play through the most recent snapshots on hover
BradyBunchRails.Brady.addHappyDance = function(square, email) {
    var intervalId;

    square.mouseenter(function () {
        var brady = BradyBunchRails.Brady,
            square = brady.$(this),
            image = square.find('img'),
            snapshotQueue = brady._snapshotQueues[email],
            length = snapshotQueue.currentLength(),
            i = length - 1;

        if (length > 0) {
            image.attr('src', snapshotQueue.get(i).snapshot);
            intervalId = setInterval(function () {
                i = i - 1;
                if (i < 0) {
                    i = length - 1;
                }
                image.attr('src', snapshotQueue.get(i).snapshot);
            }, 250);
        }
    });

    square.mouseleave(function () {
        var brady = BradyBunchRails.Brady,
            square = brady.$(this),
            snapshotQueue = brady._snapshotQueues[email],
            image = square.find('img');

        clearInterval(intervalId);
        image.attr('src', snapshotQueue.get(0).snapshot)
    });
};

BradyBunchRails.Brady.removeHappyDance = function(square) {
    square.unbind('mouseenter');
    square.unbind('mouseleave');
};

BradyBunchRails.Brady.handleLeave = function (data) {
    var brady = BradyBunchRails.Brady,
        email = data.email,
        squareId = brady._squareAssignments.indexOf(email),
        square = brady._container.find('.brady[square-id="' + squareId + '"]').first();
    console.log(email + " left; cleaning up after them");

    // TODO kind of hackey
    if (email === brady._email) {
        console.log("Looks like we left!");
        brady._messageBuffer = undefined;
        brady.resubscribe();
        return;
    }

    brady.updateSquare(square, '', brady._defaultImage);
    brady.removeHappyDance(square);

    delete brady._snapshotQueues[email];
    delete brady._roomData[email];

    for (var memberEmail in brady._roomData) {
        if (brady._roomData.hasOwnProperty(memberEmail)) {
            brady.handleRoomUpdate(brady._roomData[memberEmail]);
        }
    }
};

BradyBunchRails.Brady.assignSquares = function () {
    var brady = BradyBunchRails.Brady,
        squares = brady._container.find('.brady');

    for (var i = 0; i < brady._squareAssignments.length; i++) {
        var square = brady.$(squares[i]),
            email = brady._squareAssignments[i],
            name = brady._users[email];

        if (email === brady._email) {
            square.addClass('my-brady');
            square.find('.call-overlay').remove();

            brady._switchSourceButton = brady._container.find('#camera-source');
            if (brady._videoSourceIds.length > 1) {
                square.append('<button type="button" id="camera-source" class="btn btn-default btn-sm">' +
                    '    <span class="glyphicon glyphicon-camera"></span>' +
                    '    <span class="glyphicon glyphicon-refresh"></span>' +
                    '</button>');
                brady._currentVideoSourceIndex = 0;
                brady._switchSourceButton.on('click', function (event) {
                    brady._currentVideoSourceIndex = (brady._currentVideoSourceIndex + 1) % brady._videoSourceIds.length;
                    brady.startVideo();
                });
            }
        }

        square.attr('data-member-email', email);
        square.find('.name').html(name);
        square.toggleClass('occupied', !!email);
    }
};

BradyBunchRails.Brady.updateSquare = function (square, email, src) {
    var overlay = square.find('.call-overlay'),
        facetimeUrl = '';

    square.find('img').attr('src', src);
    square.toggleClass('occupied', !!email);
    if (email) {
        facetimeUrl = 'facetime://' + email;
    }
    if (overlay) {
        overlay.attr('href', facetimeUrl);
    }
};

BradyBunchRails.Brady.recordUpdate = function (data) {
    var brady = BradyBunchRails.Brady,
        email = data.email;

    data.updated_at = moment();

    brady._roomData[email] = data;
};

BradyBunchRails.Brady.reap = function () {
    var brady = BradyBunchRails.Brady;

    for (var memberEmail in brady._roomData) {
        if (brady._roomData.hasOwnProperty(memberEmail)) {
            if (brady._roomData[memberEmail].updated_at.isBefore(moment().subtract(30, 'seconds'))) {
                brady.handleLeave(brady._roomData[memberEmail]);
            }
        }
    }
};

BradyBunchRails.Brady.snapshotQueueIsConstant = function (queue) {
    var isFull = queue.isFull(),
        first = queue.get(0);

    if (isFull && first && first.md5) {
        // If the queue is full and there are md5's, compare them all
        return queue.every(function (entry) {
            return entry.md5 === first.md5;
        });
    } else if (isFull) {
        // If the queue is full but there aren't md5's, something went wrong
        return true;
    } else {
        // If the queue is not full, it is not constant
        return false;
    }
};
