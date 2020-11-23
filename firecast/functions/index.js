// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//     console.log("Hello World!")
//  response.send("Hello from this firecast!");
// });
'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();
exports.sendMessageNotification = functions.database.ref('/Notifications/{user_id}/{notification_id}')
  .onWrite((change, context) => {
    const data_context = context.params;
    const user_id = data_context.user_id;
    const notification_id = data_context.notification_id;

    const fromUser = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');
    return fromUser.then(fromUserResult => {
      const type = fromUserResult.val().type;
      const title = fromUserResult.val().title;
      const message = fromUserResult.val().message;
      const receiverId = fromUserResult.val().receiverId;

      // console.log("type", type);
      // console.log("title", title);
      // console.log("message", message);
      // console.log("receiverId", receiverId);

      let data = {
        receiverId: receiverId,
        type: type,
        message: message,
        title: title,
        icon: "default",
        sound: "default",
        click_action: "com.example.virtualmeetingapp.activites.SplashScreenActivity"
      };

      if (type === "appointment") {
        data.appointmentId = fromUserResult.val().appointmentId;
      } else if (type === "calling") {
        data.appointmentId = fromUserResult.val().appointmentId;
        data.callerId = fromUserResult.val().callerId;
        data.videoCall = fromUserResult.val().videoCall;

        // console.log("in calling ==> ");
        // console.log("appointmentId", fromUserResult.val().appointmentId);
        // console.log("callerId", fromUserResult.val().callerId);
        // console.log("videoCall", fromUserResult.val().videoCall);
      }

      // const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
      const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
      return Promise.all([deviceToken]).then(result => {
        const token_id = result[0].val();
        const payload = {
          data: data
        };
        /*
         * Using admin.messaging() we are sending the payload notification to the token_id of
         * the device we retreived.
         */
        var adaRef = admin.database().ref(`Notifications/${user_id}`);
        adaRef.remove();
        return admin.messaging().sendToDevice(token_id, payload);
      });
    });
  });