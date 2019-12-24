from flask import Flask, request
from flask_cors import CORS
import boto3

app = Flask(__name__)
CORS(app)


@app.route("/")
def home():
    return "Mobile Computing Server"


@app.route("/upload", methods=["POST"])
def save():
    print('hit me just now-----')
    file = request.files['uploaded_file']

    s3 = boto3.client('s3', aws_access_key_id="AKIAIPJJM7E2IT75BMUA",
                      aws_secret_access_key="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")
    file.save(file.filename)
    s3.upload_file(file.filename, 'group-16-video-files', file.filename)
    return "Success OK"


if __name__ == "__main__":
    app.run()
