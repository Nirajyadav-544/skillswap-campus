const { Client } = require("@stomp/stompjs");
const WebSocket = require("ws");

const ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlblR5cGUiOiJBQ0NFU1MiLCJzdWIiOiJuaXJhamt1bWFyeTUxM0BnbWFpbC5jb20iLCJpYXQiOjE3OTA2Mjk4OTAsImV4cCI6MTc5MDYzMDc5MH0.umkzszHqb29XIIZOvCgdnRqr4v3UtwhbraxURCYx6Pg";

const client = new Client({
    webSocketFactory: () =>
        new WebSocket("ws://localhost:8080/ws"),

    connectHeaders: {
        Authorization: `Bearer ${ACCESS_TOKEN}`
    },

    debug: (message) => {
        console.log("[STOMP]", message);
    },

    reconnectDelay: 0,

    onConnect: () => {
        console.log("\n=================================");
        console.log("WEBSOCKET CONNECTED SUCCESSFULLY");
        console.log("=================================\n");

        client.subscribe(
            "/topic/conversations/3",
            (message) => {
                console.log("MESSAGE RECEIVED:");
                console.log(message.body);
            }
        );

        console.log(
            "Subscribed to /topic/conversations/3"
        );
    },

    onStompError: (frame) => {
        console.error("\nSTOMP ERROR:");
        console.error(frame.headers);
        console.error(frame.body);
    },

    onWebSocketError: (error) => {
        console.error("\nWEBSOCKET ERROR:");
        console.error(error);
    },

    onWebSocketClose: (event) => {
        console.log("\nWEBSOCKET CLOSED:");
        console.log(event);
    }
});

client.activate();

console.log("Connecting to ws://localhost:8080/ws ...");