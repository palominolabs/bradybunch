<!DOCTYPE html>
<html>
<head>
    <title>Brady Bunch</title>
    <link href="/assets/css/application.css" media="all" rel="stylesheet" />

    <script src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.6.0/moment.min.js"></script>
    <script src="//crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/md5.js"></script>
    <script src="//code.jquery.com/jquery-2.1.1.min.js"></script>

    <script type="text/javascript" src="/assets/js/jquery.atmosphere.js"></script>
    <script src="/assets/js/bradybunch.js"></script>
    <script src="/assets/js/snapshot_queue.js"></script>
</head>

<body>

<!-- TODO vars for URI, version -->
<div id="brady-screen"
     data-default-image="/assets/logo.png"
     data-version="7"
     data-users="${people?html}"
>
    <div class="brady-row">
        <div class="brady" square-id="0" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="1" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="2" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
    </div>

    <div class="brady-row">
        <div class="brady" square-id="3" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="4" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="4" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
    </div>

    <div class="brady-row">
        <div class="brady" square-id="6" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="7" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
        <div class="brady" square-id="8" data-member-email="">
            <a class="call-overlay"></a>
            <img src="/assets/logo.png" alt=""/>
            <div class="controls">
                <span class="name"></span>
            </div>
        </div>
    </div>

    <canvas style="display:none;"></canvas>

    <video style="display:none;" autoplay></video>
</div>

<div id="video-required-mask"><h1>You must allow video camera access before continuing.</h1></div>

<script>
    $(document).on('ready', function () {
        var brady = BradyBunchRails.Brady,
                email = '${currentUser.email?html}',
                name = '${currentUser.name?html}',
                bradyContainer = $('#brady-screen'),
                atmosphere = $.atmosphere,
                websocketUrl = 'chat/palomino',
                videoRequiredMask = $('#video-required-mask'),
                version = bradyContainer.data('version');

        videoRequiredMask.show();

        $(brady).on('videostart', function () {
            videoRequiredMask.hide();
            bradyContainer.show();
        });

        $(brady).on('forcereload', function () {
            document.location.href=document.location.href;
        });

        brady.initialize($, bradyContainer, websocketUrl, atmosphere, email, name, bradyContainer.data('default-image'), version);

        brady.start();
    });
</script>

</body>
</html>
