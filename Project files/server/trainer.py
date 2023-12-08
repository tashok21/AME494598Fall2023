import cv2
import os
import sys
import numpy as np
import train as fd
import json


imagePath = sys.argv[1]
personname = sys.argv[2]

with open('savedfaces.json', 'r') as file:
    name = json.load(file)

nextId = name["nextid"]
name[nextId] = personname
name["nextid"] = int(nextId) + 1


faces,faceID = fd.labels_for_training_data(int(nextId),imagePath)
model = fd.train_classifier(faces,faceID)
model.save('trainData.yml')

with open("savedfaces.json", 'w') as json_file:
    json.dump(name, json_file, indent=4)
import subprocess

# Define the bash command as a string
command = "rm -rf trainfolder"

# Run the command and capture the output
output = subprocess.check_output(command, shell=True, text=True)
print('Success')