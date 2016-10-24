(function() {
  var loginPane = document.getElementById("login-pane");
  var connectedPane = document.getElementById("connected");
  var loginForm = document.getElementById("login-form");
  var usernameField = document.getElementById("username-field");

  var chatMsgList = document.getElementById("chat");
  var msgForm = document.getElementById("msg-form");
  var msgInput = document.getElementById("msg-box");
  var clearLogBtn = document.getElementById("clear-log");

  var socket = null;

  var clearChat = function() {
    while (chatMsgList.children.length > 0) {
      chatMsgList.removeChild(chatMsgList.lastChild);
    }
  };

  var connect = function(name) {
    var protocol = document.location.protocol === "https:" ? "wss:" : "ws:";
    var host = document.location.host;
    socket = new WebSocket(protocol + "//" + host + "/chat?name=" + name);
    socket.onopen = function() {
      console.log("Connection established");
      socket.onmessage = function(event) {
        var msg = event.data;
        var elem = document.createElement("li");
        elem.innerText = msg;
        chatMsgList.appendChild(elem);
      };
      connectedPane.className = "";
      loginPane.className = "hidden";
    };
    socket.onclose = disconnect;
  };

  var disconnect = function() {
    socket = null;
    loginPane.className = "";
    connectedPane.className = "hidden";
    alert("Disconnected from server");
  };

  clearLogBtn.addEventListener("click", clearChat);

  msgForm.addEventListener("submit", function(event) {
    event.preventDefault();
    if (msgInput.value.trim() !== "") {
      socket.send(msgInput.value.trimRight());
      msgInput.value = "";
    }
  });

  loginForm.addEventListener("submit", function(event) {
    event.preventDefault();
    if (usernameField.value.trim() !== "") {
      connect(usernameField.value.trim());
    }
  });

})();
