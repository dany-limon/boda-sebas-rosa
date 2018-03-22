import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';

var firebase = require("firebase/app");
require("firebase/auth");
require("firebase/database");

var config = {
  apiKey: "AIzaSyDj4gyDvcHiXdiGZFO3SYYS0lZYHHDR37k",
  authDomain: "boda-sebas-rosa.firebaseapp.com",
  databaseURL: "https://boda-sebas-rosa.firebaseio.com",
  projectId: "boda-sebas-rosa",
  storageBucket: "boda-sebas-rosa.appspot.com",
  messagingSenderId: "777556442432"
};
firebase.initializeApp(config);

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
