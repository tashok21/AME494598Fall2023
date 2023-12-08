var MS = require("mongoskin");
var express = require("express");
var app = express();
var bodyParser = require('body-parser');
var errorHandler = require('errorhandler');
var methodOverride = require('method-override');
var hostname = process.env.HOSTNAME || 'localhost';
var port = 1234;
var VALUEt = 0;
var VALUEh = 0;
var VALUEtime = 0;




`const fs = require('fs');
const path = require('path');


var db = MS.db("mongodb://localhost:27017/sensorData")
const mongoose = require('mongoose');
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

// Create a model based on the schema
const ImageModel = mongoose.model('Image', imageSchema);

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




app.get('/api/load', function (req, res){
    try {
      
        var base64str = base64_encode('dvboi.png');
        addImageToMongoDB('firstimage',base64str, 'String'); // Replace with the correct content type of your image
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


async function checkIfImageExists(req) {
  try {
    const image = await ImageModel.findOne({ imageId: imageId });
    if (image) {
      console.log('Image found:', image);
      // Convert base64 string to binary data
      var buffer = Buffer.from(image.data, 'base64');

      // Write file to disk
      fs.writeFileSync('${imageId}.jpg', buffer);
      const { exec } = require('child_process');
      exec('cat *.py bad_file | wc -l', (err, stdout, stderr) => {
      if (err) {
     // node couldn't execute the command
       return;
    }

  // the entire stdout and stderr (buffered)
  console.log(`stdout: ${stdout}`);
  console.log(`stderr: ${stderr}`);
});
      console.log('Image saved to disk successfully');
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


app.get('/api/checkthisface', function (req, res){
  try {
    checkIfImageExists('firstimage')
  .then(image => {
    if (image) {
      console.log('something something');
      res.status(201).json({ "Done": "True" });
    }
  })
  .catch(error => {
    console.error('Error during image search:', "error");
    res.status(500).json({ message: 'An error2 occurred while loading the image' });
    // Handle the error appropriately
  });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'An error occurred while loading the image' });
  }
});



app.get('/api/getNewFace', function (req, res){
  try {
    findImageById('firstimage')
  .then(image => {
    if (image) {
      res.status(201).json({ imagedata: image.data });
    }
  })
  .catch(error => {
    console.error('Error during image search:', error);
    res.status(500).json({ message: 'An error2 occurred while loading the image' });
    // Handle the error appropriately
  });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'An error occurred while loading the image' });
  }
});

app.get('/api/approveface', function (req, res){
  try {
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

  
  



app.use(methodOverride());
app.use(bodyParser());
app.use(express.static(__dirname + '/public'));
app.use(errorHandler());

console.log("Simple static server listening at http://" + hostname + ":" + port);
app.listen(port);
