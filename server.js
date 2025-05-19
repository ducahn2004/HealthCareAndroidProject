const express = require('express');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

io.on('connection', (socket) => {
    console.log('Client connected');
    
    // Gửi thông báo mẫu mỗi 10 giây
    setInterval(() => {
        const notification = {
            title: 'HEART RATE ALERT',
            message: 'Heart rate is too high. Need to Emergency!',
        };
        socket.emit('newNotification', notification);
    }, 10000);
});

server.listen(3000, () => {
    console.log('Server running on port 3000');
});