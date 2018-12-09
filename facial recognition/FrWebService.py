import face_recognition
import base64
import base64
import io
import numpy as np
from io import BytesIO
from PIL import Image, ImageDraw

from flask import Flask, jsonify, request, json
app = Flask(__name__)


@app.route('/getencoding', methods=['POST'])
def getEncoding():
    data = request.files['image']
    camImage = face_recognition.load_image_file(data)
    camera_image_encoding = face_recognition.face_encodings(camImage)[0]

    jObj = {}
    jObj['encoding'] = camera_image_encoding.tolist()

    response = app.response_class(
        response=json.dumps(jObj),
        status=200,
        mimetype='application/json'
    )
    return response


@app.route('/compareimages', methods=['POST'])
def compareimages():
    data = request.get_json(force=True)
    first_encoding = np.array(data['first_encoding'])
    second_encoding = np.array(data['second_encoding'])
    results = face_recognition.compare_faces(
        [first_encoding], second_encoding, tolerance=0.3)
    # print(results[0])

    rObj = {'match': str(results[0]).lower()}
    #data_json = json.dumps(rObj)

    # print(data_json)

    response = app.response_class(
        response=json.dumps(rObj),
        status=200,
        mimetype='application/json'
    )

    return response


@app.route('/facialreg', methods=['POST'])
def facialreg():
    data = request.get_json(force=True)
    uuid = data["uuid"]
    sampleEncodeImage = data["sampleimage"].replace("\n", "")
    cameraEncodeImage = data["camimage"].replace("\n", "")

    sampleImage = face_recognition.load_image_file(
        io.BytesIO(base64.b64decode(sampleEncodeImage)))
    sample_image_encoding = face_recognition.face_encodings(sampleImage)[0]

    camImage = face_recognition.load_image_file(
        io.BytesIO(base64.b64decode(cameraEncodeImage)))
    camera_image_encoding = face_recognition.face_encodings(camImage)[0]

    # compare user taken face image to sample image
    # tolerence is how strict
    # lower the stricter
    # current sweet spot = 0.5, changed from 0.4
    results = face_recognition.compare_faces(
        [sample_image_encoding], camera_image_encoding, tolerance=0.5)

    #true = match
    #false = not match
    if True in results:
        regStatus = str("Match")

        # Find all the faces and face encodings in the camera image
        cam_face_locations = face_recognition.face_locations(camImage)
        cam_face_encodings = face_recognition.face_encodings(
            camImage, cam_face_locations)

        # Convert the image to a PIL-format image so that we can draw on top of it with the Pillow library
        pil_image = Image.fromarray(camImage)
        # Create a Pillow ImageDraw Draw instance to draw with
        draw = ImageDraw.Draw(pil_image)

        # Loop through each face found in the unknown image
        for (top, right, bottom, left), face_encoding in zip(cam_face_locations, cam_face_encodings):
            # Draw a box around the face using the Pillow module
            draw.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))

            # Draw a label with a name below the face
            text_width, text_height = draw.textsize(uuid)
            draw.rectangle(((left, bottom - text_height - 10),
                            (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
            draw.text((left + 6, bottom - text_height - 5),
                      uuid, fill=(255, 255, 255, 255))

        # Remove the drawing library from memory as per the Pillow docs
        del draw

        # convert drawn pil image to base64
        buffered = BytesIO()
        pil_image.save(buffered, format="JPEG")
        faceDetectBase64 = str(base64.b64encode(buffered.getvalue()))
        faceDetectBase64 = faceDetectBase64.replace("b'", "")
        faceDetectBase64 = faceDetectBase64.replace("'", "")

    else:
        regStatus = str("Do Not Match")

        # Find all the faces and face encodings in the camera image
        cam_face_locations = face_recognition.face_locations(camImage)
        cam_face_encodings = face_recognition.face_encodings(
            camImage, cam_face_locations)

        # Convert the image to a PIL-format image so that we can draw on top of it with the Pillow library
        pil_image = Image.fromarray(camImage)
        # Create a Pillow ImageDraw Draw instance to draw with
        draw = ImageDraw.Draw(pil_image)

        # Loop through each face found in the unknown image
        for (top, right, bottom, left), face_encoding in zip(cam_face_locations, cam_face_encodings):
            # Draw a box around the face using the Pillow module
            draw.rectangle(((left, top), (right, bottom)), outline=(0, 0, 255))

            # Draw a label with a name below the face
            text_width, text_height = draw.textsize("unknown")
            draw.rectangle(((left, bottom - text_height - 10),
                            (right, bottom)), fill=(0, 0, 255), outline=(0, 0, 255))
            draw.text((left + 6, bottom - text_height - 5),
                      "unknown", fill=(255, 255, 255, 255))

        # Remove the drawing library from memory as per the Pillow docs
        del draw

        # convert drawn pil image to base64
        buffered = BytesIO()
        pil_image.save(buffered, format="JPEG")
        faceDetectBase64 = str(base64.b64encode(buffered.getvalue()))
        faceDetectBase64 = faceDetectBase64.replace("b'", "")
        faceDetectBase64 = faceDetectBase64.replace("'", "")

    return jsonify(
        status=regStatus,
        facedetect=faceDetectBase64
    )


if __name__ == '__main__':
    app.run(debug=True, port=5000, threaded=True, host='0.0.0.0')
