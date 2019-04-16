var engine = require('engine.io-client');

const opts = {
    path:  '/rest-api/sock'
}
const socket = engine('http://localhost:8081', opts);
socket.on('open', function () {
    var echoMessage = "Hi Server, I am client";
    socket.on('message', function (message) {
        console.log('received: ', message)
    });
    socket.send(echoMessage);
});
socket.on('error', function (err) {
    console.log('err', err);
});
