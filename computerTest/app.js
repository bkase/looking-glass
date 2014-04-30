
window.onload = function(){
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
    //connect();
  });

  var takePhotoSpec = { 
    username: 'evan', 
    namespace: 'evan.' + "JSInterface.getDevice()" + '.takePhoto', 
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

  session.call(takePhotoId, {}, function(err, output){
    if (err)
      console.log(err);
    else
      console.log(output);
  });
}
