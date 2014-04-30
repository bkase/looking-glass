
window.onload = function(){
  console.log('loaded');
  JSInterface.loaded();
}

window.callWithDeviceName = function(devName) {
  console.log('start');
  window.devName = devName;
  connect();
}

function connect(){
  Cloasis.hostport = ['evanshapi.ro', 32200];
  Cloasis.registerUser('evan', 'arst', function(err, session){
    console.log('ru');
    if (err) {
      console.log(err);
      session.loginUser('evan', 'arst', function(err){
        if (err)
          throw err;
        onLogin(session);
      })
    } else {
      onLogin(session);
    }
  });
}

function onLogin(session) {
  session.on('close', function(){
    console.log('close');
    //connect();
  });

  session.search("takePhoto", function(err, output){
    if (err) {
      console.log(err);
    } else {
      for (var i = 0; i < output.length; i++) {
        if (output[i].namespace.indexOf(window.devName) !== -1) {
          console.log("Calling with "+ JSON.stringify(output[i]));
          session.call(output[i], {}, function(err, output){
            if (err) {
              console.log(err);
            } else {
              console.log(output);
              JSInterface.renderResponse(output.img, output.timestamp);
            }
          });
        }
      }
    }
  });
}

