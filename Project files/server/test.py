import cv2
import os
import sys
import numpy as np
import train as fd
import json 

imagePath = sys.argv[1]		
image = cv2.imread(imagePath)


# image = cv2.imread(path) 	#For PIP package
face_det,gray = fd.faceDetection(image)

#For Training
# faces,faceID = fd.labels_for_training_data('Train')
# model = fd.train_classifier(faces,faceID)
# model.save('trainData.yml')

# Load names from the JSON file
with open('savedfaces.json', 'r') as file:
    name = json.load(file)


model=cv2.face.LBPHFaceRecognizer_create()
model.read('trainData.yml')	#Loads the previously trained data for Face Recognition.
for face in face_det:
    (x,y,w,h) = face
    roi_gray = gray[y:y+h,x:x+h]
    label,confidence = model.predict(roi_gray)	#predicting the label of given image
    fd.draw_rect(image,face)
    # print(label)
    # predicted_name = name[str(label)]
    # print(predicted_name)
    # print(label)
    # print(confidence)
    print(confidence)
    if(confidence<90):
        predicted_name = name[str(label)]
        print(predicted_name)
        print(label)
    else: 
        print("False")

    #     continue
    # fd.put_text(image,predicted_name,x,y)


