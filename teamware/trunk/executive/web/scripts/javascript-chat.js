
function init() {
  dwr.engine.setActiveReverseAjax(true);
}

function sendMessage(username) {
  var text = dwr.util.getValue("text");
  dwr.util.setValue("text", "");
  var date = new Date();
  if(!username) username = "anonymous";
  var msg = "<i>" + date.toLocaleTimeString() + "</i> - " + "<b>" + username + "</b>: " + text
  JavascriptChat.addMessage(msg);
}

function receiveMessages(messages) {
  var chatlog = "";
  for (var data in messages) {
    if(messages[data].text){
       chatlog = "<div>" + messages[data].text + "</div>" + chatlog;
    }
  }
  dwr.util.setValue("chatlog", chatlog, { escapeHtml:false });
}
