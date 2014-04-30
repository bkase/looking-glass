
window.onload = function(){
  console.log('start');
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

  var takePhotoSpec = { 
    username: 'evan', 
    namespace: 'evan.' + JSInterface.getDevice() + '.takePhoto', 
    name: "takePhoto", 
    version: 0.1,
    description: "takes a photo",
    examples: [ ],
    inputSpec: { },
    outputSpec: { "img": "String", "timestamp": "Number" }
  };

  var takePhotoId = {
    name: takePhotoSpec.name, 
    namespace: takePhotoSpec.namespace,
    version: takePhotoSpec.version
  };

  session.register(takePhotoSpec, function(err){
    if (err){
      console.log(JSON.stringify(err));
    }
    session.deactivate(takePhotoId, function(err){
      if (err)
        console.log(JSON.stringify(err));
      session.activate({ fn: takePhoto, apiIdentifier: takePhotoId }, function(err){
        if (err){
          console.log(JSON.stringify(err));
        }
        console.log('ready with ' + JSON.stringify(takePhotoId));
      });
    });
  });
}

window.onGotImage = function(callbackId, base64){
  console.log('got image!', base64.length);
  callbacks[callbackId]({ "img": base64, timestamp: Date.now() });
  delete callbacks[callbackId];
}

var callbackId = 0;
var callbacks = {};

function takePhoto(input, done){
  callbacks[callbackId] = done;
  JSInterface.takePhoto(callbackId++);
}

