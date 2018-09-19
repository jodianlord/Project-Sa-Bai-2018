from flask import Flask
from flask import request
import face_recognition
app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'

@app.route('/checkFaces', methods=['POST'])
def checkFaces():
    imagedata = request.files
    image = face_recognition.load_image_file(imagedata)
    return 'Hi bitch!'

if __name__ == '__main__':
    app.run()

