var MS = require("mongoskin");
var express = require("express");
var app = express();
app.use(express.json());
var bodyParser = require('body-parser');
var errorHandler = require('errorhandler');
var methodOverride = require('method-override');
var exec = require('child_process').exec;

const util = require('util');

// Convert exec into a promise-compatible function
const execPromise = util.promisify(exec);

var hostname = process.env.HOSTNAME || 'localhost';
var port = 1234;
var VALUEt = 0;
var VALUEh = 0;
var VALUEtime = 0;
var personname = ""

var admin = require("firebase-admin");

var serviceAccount = require('./ame598notifications-firebase-adminsdk-gptep-373ed43936.json');

admin.initializeApp({
  	credential: admin.credential.cert(serviceAccount),
	databaseURL: "https://prismappfcm.firebaseio.com"
});


const fs = require('fs');
const path = require('path');
const registrationToken = 'd_h6wVGUQryqe7FAIY7Va7:APA91bEi6UJAOQh1-h_mFm7XocwtU9vutKxcSmZXH23aCBHayZ2Op23PwOleEffyjRup9mx_xz8ICGxLqshX9TVtio8r-nj7JJP7DTdwrjwGvqKhr-seHBW7oIqYiCStu7hKMGT569jR';


var db = MS.db("mongodb://localhost:27017/sensorData")
const mongoose = require('mongoose');
const { stdout } = require("process");
// Connect to MongoDB
mongoose.connect('mongodb://localhost:27017/imagedb', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
});



const imageSchema = new mongoose.Schema({
  imageId : String,
  data: String, // Store image data as binary data
  contentType: String, // Specify the content type (e.g., 'image/jpeg', 'image/png')
});


const dataSchema = new mongoose.Schema({
  name : String,
  action: String, // Store image data as binary data
  Date: String, // Specify the content type (e.g., 'image/jpeg', 'image/png')
});

// Create a model based on the schema
const ImageModel = mongoose.model('Image', imageSchema);
const nameModel = mongoose.model('Data', dataSchema);

// Function to add an image to MongoDB
async function addImageToMongoDB(_imageId, imageData, contentType) {
  try {
    // Create a new document
    const newImage = new ImageModel({ imageId : _imageId, data: imageData, contentType });

    // Save the document to the database
    await newImage.save();

    console.log('Image added to MongoDB successfully');
  } catch (error) {
    console.error('Error adding image to MongoDB:', error);
  }
}

async function addDataToMongoDB(_name, _action, _date) {
  try {
    // Create a new document
    const nameData = new nameModel({ name : _name, action: _action, date: _date });

    // Save the document to the database
    await nameData.save();

    console.log('Image added to MongoDB successfully');
  } catch (error) {
    console.error('Error adding image to MongoDB:', error);
  }
}

function base64_encode(file) {
  // read binary data
  var bitmap = fs.readFileSync(file);
  // convert binary data to base64 encoded string
  return new Buffer(bitmap).toString('base64');
}
// Usage example: Read the image file and call addImageToMongoDB with the image data and content type

var topic = 'general';

var message = {
  data: {
    score: '850',
    time: '2:45'
  },
  token: registrationToken
};

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// function callTestPython(imagepath) {
//   command = "python3 test.py ".concat(imagepath)
//   var outs = "true"
//   exec(command,
//     function (error, stdout, stderr) {
//         outs = stdout
//         if (error !== null) {
//              console.log('exec error: ' + error);
//         }
//         return outs
//     }
//     );
// }

async function callTestPython(imagepath) {
  const command = `python3 test.py ${imagepath}`;
  
  try {
      const { stdout } = await execPromise(command);
      
      return stdout;  // This will be the resolved value of the promise
  } catch (error) {
      console.error('Exec error:', error);
      return null; // Handle errors, such as returning null or a default value
  }
}

async function callTrainPython(imagepath, name) {
  const command = `python3 trainer.py ${imagepath} ${name}`;  
  try {
      const { stdout } = await execPromise(command);
      
      return stdout;  // This will be the resolved value of the promise
  } catch (error) {
      console.error('Exec error:', error);
      return null; // Handle errors, such as returning null or a default value
  }
}

async function callPrepareData(imagepath) {
  foldername = "trainfolder"
  const command = `python3 flipandsave.py ${imagepath} ${foldername}`;  
  try {
      const { stdout } = await execPromise(command);
      
      return stdout;  // This will be the resolved value of the promise
  } catch (error) {
      console.error('Exec error:', error);
      return null; // Handle errors, such as returning null or a default value
  }
}


async function alertAdmin() {
  admin.messaging().send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
}  

app.get('/api/load', async function (req, res){
    try {
      var i = await callTestPython('check.jpg')
      console.log(i)
      
        //var base64str = base64_encode('dvboi.png');
        //addImageToMongoDB('firstimage',base64str, 'String'); // Replace with the correct content type of your image
        res.status(201).json({ message: 'Image loaded successfully' });

    } catch (error) {
      console.error(error);
      res.status(500).json({ message: 'An error occurred while loading the image' });
    }
  });

async function findImageById(imageId) {
  try {
    const image = await ImageModel.findOne({ imageId: imageId });
    if (image) {
      console.log('Image found:', image);
      return image;
    } else {
      console.log('No image found with the given imageId.');
      return null;
    }
  } catch (error) {
    console.error('Error while finding the image:', error);
    throw error;
  }
}

async function addImageAndWaitForTraingApproval(imageId) {
  try {
    alertAdmin()
  } catch (error) {
    console.error('Error while finding the image:', error);
    throw error;
  }
}


async function checkIfImageExists(req) {
  try {
    const image = await ImageModel.findOne({ imageId: imageId });
    if (image) {
      console.log('Image found:', image);
      return image;
    } else {
      console.log('No image found with the given imageId.');
      return null;
    }
  } catch (error) {
    console.error('Error while finding the image:', error);
    throw error;
  }
}


app.post('/api/checkthisface', async function (req, res){
  try {
    //console.log(req)
    personname = req.body.name
    const now = new Date();
    const timeString = now.toISOString();
    addDataToMongoDB(personname, "REQUESTED", timeString )
    var buffer = Buffer.from(req.body.image, 'base64');
    fs.writeFileSync('check.jpg', buffer);
    var result = await callTestPython('check.jpg')
    console.log(result)
    if(result.includes("False")){
      addImageAndWaitForTraingApproval('check.jpg')
      res.status(201).json({ "status": "Requested" });
      
    }else{
      const now = new Date();
      const timeString = now.toISOString();
      addDataToMongoDB(personname, "APPROVED", timeString )
      res.status(201).json({ "status": "approved" });
    }
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'An error occurred while loading the image' });
  }
});



app.get('/api/getNewFace', function (req, res){
  try {
    data =  base64_encode('check.jpg');
    res.status(201).json({ imagedata: data });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'An error occurred while loading the image' });
  }
});

app.get('/api/sendnotif', function (req, res){
  try {
    alertAdmin()
    res.status(201).json({ imagedata: "data" });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'An error occurred while loading the image' });
  }
});



app.get('/api/approveface', async function (req, res){
  try {
    var result = await callPrepareData('check.jpg')
    var result2 = await callTrainPython('trainfolder', personname)
    const now = new Date();
    const timeString = now.toISOString();
    addDataToMongoDB(personname, "ADDED", timeString )
    console.log(result2)
    res.status(201).json({ status: 'done' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Could not approve' });
  }
});

app.get('/api/denyface', function (req, res){
  try {
    res.status(201).json({ status: 'done' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Could not deny' });
  }
});

app.get('/api/approveonceface', function (req, res){
  try {
    res.status(201).json({ status: 'done' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Could not approve' });
  }
});

  
  
// fs.readFile('dvboi.png', (err, data) => {
//   if (err) {
//     console.error('Error reading image file:', err);
//     return;
//   }
  
//   addImageToMongoDB(data, 'image/jpeg'); // Replace with the correct content type of your image
// });

// try {
//   db.collection('products').insertOne( { item: "card", qty: 15 } );
// } catch (e) {
//   console.log(e);
// };


// app.get("/", function (req, res) {
//     res.redirect("/index.html");
// });

// app.get('/api/load', function (req, res){
//   try {
//     const filePath = 'dvboi.png';
//     fs.readFile(filePath, 'utf8', (err, imageData) => {
//       if (err) {
//         console.error(`Error reading file: ${err}`);
//         res.status(500).json({ message: 'An error occurred while loading the image' });
//       }
//       const st = "hello tester";
//       const result =  db.collection('images').insertOne({ imageData });
//       res.status(201).json({ message: 'Image loaded successfully', insertedId: result.insertedId });
//       console.log(`File content:\n${imageData}`);
//     });
//   } catch (error) {
//     console.error(error);
//     res.status(500).json({ message: 'An error occurred while loading the image' });
//   }
// });


// app.get("/getLatest", function (req, res) {
//   db.collection("dataWeather").find({}).sort({time:-1}).limit(10).toArray(function(err, result){
//     res.send(JSON.stringify(result));
//   });
// });

// app.get("/getData", function (req, res) {
//   var from = parseInt(req.query.from);
//   var to = parseInt(req.query.to);
//   db.collection("dataWeather").find({time:{$gt:from, $lt:to}}).sort({time:-1}).toArray(function(err, result){
//     res.send(JSON.stringify(result));
//   });
// });


// app.get("/getValue", function (req, res) {
//   //res.writeHead(200, {'Content-Type': 'text/plain'});
//   res.send(VALUEt.toString() + " " + VALUEh + " " + VALUEtime + "\r");
// });

// app.get("/setValue", function (req, res) {
//   VALUEt = parseFloat(req.query.t);
//   VALUEh = parseFloat(req.query.h);
//   VALUEtime = new Date().getTime();
// 	var dataObj = {
// 		t: VALUEt,
// 		h: VALUEh,
// 		time: VALUEtime
// 	}
// 	db.collection("dataWeather").insert(dataObj, function(err,result){
// 		console.log("added data: " + JSON.stringify(dataObj));
// 	});
//   res.send(VALUEtime.toString());
// });


app.use(methodOverride());
app.use(bodyParser());
app.use(express.static(__dirname + '/public'));
app.use(errorHandler());

console.log("Simple static server listening at http://" + hostname + ":" + port);
app.listen(port);
