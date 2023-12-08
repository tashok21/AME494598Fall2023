import cv2
import os
import sys
import numpy as np

#This function is for Haar-cascade: A Face detection algorithm.
def faceDetection(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)  # Image grayscaling

    # Correct path to haarcascade XML file
    cascPath = cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
    faceCascade = cv2.CascadeClassifier(cascPath)

    faces = faceCascade.detectMultiScale(gray, scaleFactor=1.3, minNeighbors=5)    
    return faces, gray

#This function returns labels in accordance to .yml file syntax. 
# def labels_for_training_data(directory):
#     faces=[]
#     faceID=[]

#     for path,subdirnames,filenames in os.walk(directory):
#         for filename in filenames:
#             if filename.startswith("."):
#                 print("Skipping system file")	#Skipping files that startwith .
#                 continue

#             id=os.path.basename(path)	#fetching subdirectory names
#             img_path=os.path.join(path,filename)	#Joining image path to subdirectory
#             print("img_path:",img_path)
#             print("id:",id)
#             image=cv2.imread(img_path)	#loading each image one by one
#             if image is None:
#                 print("Image not loaded properly")
#                 continue
#             faces_rect,gray=faceDetection(image)	#Calling faceDetection function to return faces detected in particular image
#             if len(faces_rect)!=1:
#                continue 	#Each class with images are being fed to classifier
#             (x,y,w,h) = faces_rect[0]
#             roi_gray = gray[y:y+w,x:x+h]	#cropping region of interest 
#             faces.append(roi_gray)
#             faceID.append(int(id))
#     return faces,faceID
def labels_for_training_data(current_id, directory):
    faces = []
    faceID = []
    label_map = {}  # Dictionary to map names to integers

    for path, subdirnames, filenames in os.walk(directory):
        for filename in filenames:
            img_path = os.path.join(path, filename)  # Joining image path to subdirectory
            image = cv2.imread(img_path)  # Loading each image one by one

            faces_rect, gray = faceDetection(image)  # Calling faceDetection function
            if len(faces_rect) != 1:
                continue  # Skip images that don't have exactly one face

            (x, y, w, h) = faces_rect[0]
            roi_gray = gray[y:y+w, x:x+h]  # Cropping region of interest
            faces.append(roi_gray)
            faceID.append(current_id)

    return faces, faceID


#This function trains haar classifier and takes faces,faceID returned by previous function as its arguments
def train_classifier(faces,faceID):
#    print(help(cv2.face))
    model = cv2.face.LBPHFaceRecognizer_create()
#    model = cv2.face.EigenFaceRecognizer_create()
    model.train(faces,np.array(faceID))
    return model

#This function draws bounding boxes around detected face in image
def draw_rect(image,face):
    (x,y,w,h)=face
    cv2.rectangle(image,(x,y),(x+w,y+h),(0,255,0),thickness=2)

#This function writes name of person for detected label
def put_text(image,text,x,y):
    cv2.putText(image,text,(x,y),cv2.FONT_HERSHEY_SIMPLEX,0.8,(0,0,255),2)

def put_text(image, text, x, y):
    cv2.putText(image, text, (x, y), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
def display_training_images_with_detected_faces(directory):
    for path, subdirnames, filenames in os.walk(directory):
        for filename in filenames:
            if filename.startswith("."):
                continue  # Skip system files

            img_path = os.path.join(path, filename)
            image = cv2.imread(img_path)
            if image is None:
                print(f"Image not loaded properly: {img_path}")
                continue

            faces, _ = faceDetection(image)
            for (x, y, w, h) in faces:
                cv2.rectangle(image, (x, y), (x+w, y+h), (0, 255, 0), 2)  # Draw rectangle around face

            cv2.imshow("Training Image", image)
            cv2.waitKey(1000)  # Wait for 1000 ms

    cv2.destroyAllWindows()

# Call this function to display your training images
#display_training_images_with_detected_faces(training_data_dir)

# Main program
# if __name__ == "_main_":

 # Directory containing training data
# training_data_dir = "C:/Users/Trisha Ashok/Desktop/0 IOT/face_Rec/Face_rec_LBPH-master/Face_rec_LBPH-master/newdata"

#     # Prepare training data
# print("Preparing data...")
# faces, faceID, label_map = labels_for_training_data(training_data_dir)
# print("Data prepared")

#     # Train the classifier and save
# print("Training model...")
# face_recognizer = train_classifier(faces, faceID)
# face_recognizer.write('trainingData.yml')
# print("Model trained and saved")
# #display_training_images_with_detected_faces(training_data_dir)
#     # Load a test image
# test_img = cv2.imread("C:/Users/Trisha Ashok/Desktop/0 IOT/face_Rec/Face_rec_LBPH-master/Face_rec_LBPH-master/newdata/Trisha/lol5.jpg")

#     # Perform face detection
# faces_detected, gray_img = faceDetection(test_img)
# print ("Loaded test image")
#     # Recognize faces in the test image
# # for face in faces_detected:
# #     (x, y, w, h) = face
# #     roi_gray = gray_img[y:y+h, x:x+w]
# #     label, confidence = face_recognizer.predict(roi_gray)
# #     print("Confidence:", confidence)
# #     print("Label:", label)

# #     draw_rect(test_img, face)
# #     predicted_name = str(label)  # or map label to person's name if you have that information
# #     put_text(test_img, predicted_name, x, y)
# print (faces_detected)

# id_to_name_map = {v: k for k, v in label_map.items()}
# for face in faces_detected:
#     print ("In Loop")
#     (x, y, w, h) = face
#     roi_gray = gray_img[y:y+h, x:x+w]
#     label, confidence = face_recognizer.predict(roi_gray)
#     print("Confidence:", confidence)
#     print("Label:", label)

#     draw_rect(test_img, face)

#         # Use the reverse mapping to get the class name
#     predicted_name = id_to_name_map.get(label, "Unknown")
#     put_text(test_img, predicted_name,x,y)
#     # Display the output
# #cv2.imshow("Face Detector", test_img)
# #cv2.waitKey(0)
# #cv2.destroyAllWindows()