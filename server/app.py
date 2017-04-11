import argparse
from flask import Flask, render_template, request, jsonify
import MySQLdb
import uuid, random, string
import os
import time
import shutil
import sys
import subprocess


UPLOAD_FOLDER = "path\\toencrypt\\"
UPLOAD_FOLDER_FOR_KEY = "path\\templates\\"
UPLOAD_FOLDER_IN = "path\\toencryptin\\"
ENCRYPTED = "path\\encrypted\\"

ALLOWED_EXTENSIONS = set(['txt', 'png', 'jpg', 'jpeg', 'bmp' ])

global token, filename, filenametoencryptin,user_key

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['UPLOAD_FOLDER_IN'] = UPLOAD_FOLDER_IN


# Open database connection
db = MySQLdb.connect("localhost","root","root","stegano_db" )
#database fields : id, user_key, token, encryptedfilepath, setvaliditytime

@app.route("/toencrypt/key.json")
def index():
    global user_key
    user_key = ''.join([random.choice(string.ascii_uppercase + string.digits) for n in range(6)])
    with open(UPLOAD_FOLDER_FOR_KEY + "key.json", "w") as text_file:
        text_file.write('{"userKey" : ' + '"' + user_key + '"}')
    return render_template("key.json")

@app.route("/getdecrypt/decrypted.json")
def indexof():
    return render_template("decrypted.json")



# uploads_encryptin has the image to be encrypted in .. i.e the cover image
@app.route("/uploads_encryptin", methods = ["POST"])
def uploads_encryptin():
    if request.method == 'POST':
        file = request.files['encrypt_in']
        global filenametoencryptin
        f_name = file.filename.split(".",1)[1]
        filenametoencryptin = ''.join(random.SystemRandom().choice(string.ascii_lowercase + string.ascii_uppercase + string.digits) for _ in range(8)) + '.'+f_name
        # filenametoencryptin = file.filename
        print("filenametoencryptin ------------" + filenametoencryptin)
        file.save(os.path.join(app.config['UPLOAD_FOLDER_IN'], filenametoencryptin))
        return "successfully"
    else:
        return "Y U NO USE POST?"

# uploads_image has the image to be encrypted
@app.route("/uploads_image", methods = ["POST"])
def uploads_image():
    if request.method == 'POST':
        file = request.files['up']
        global filename
        filename = file.filename
        # it saves to_encrypt image
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        return "successfully"
    else:
        return "Y U NO USE POST?"

#uploads text file to be encrypted
@app.route("/uploads_file", methods = ["POST"])
def uploads_file():
    if request.method == 'POST':
        file = request.files['file']
        global filename
        filename = file.filename
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        return "successfully sent file"
    else:
        return "Y U NO USE POST?"


#uplaods text to be encrypted
@app.route("/uploads_text", methods = ["POST"])
def uploads_text():
    if request.method == 'POST':
        print "this is here.."
        textToEncrypt = request.data
        global filename
        filename = "to_encrypt.txt"
        with open(UPLOAD_FOLDER + filename, "w") as text_file:
            text_file.write(textToEncrypt)
        print "Text to encrypt is " + textToEncrypt
        return "successfully sent text"
    else:
        return "Y U NO USE POST?"


'''
Getting the userkey from the decryption page and passing as a parameter
to get the database

'''
@app.route("/uploads_userkey", methods = ["POST"])
def uploads_userkey():
    if request.method == 'POST':
        user_key_to_check = request.data
        print "user key to check " + user_key_to_check
        db = MySQLdb.connect("localhost","root","root","stegano_db" )
        cursor = db.cursor()
        cursor.execute('''SELECT efp,token FROM stegano
                          WHERE userKey="%s"''' % (user_key_to_check))
        val = cursor.fetchone()
        print "file path ", val[0]
        encryptedfilepath = UPLOAD_FOLDER_IN + val[0]
        tokenvalue = val[1]
        print "val is " + str(val)
        db.commit()

        if val:
            result = subprocess.call([sys.executable, 'xterminate.py', '-open', '-m', tokenvalue, '-k', tokenvalue, encryptedfilepath])
            print "result of decryption is " + str(result)
            print "decryption complete"
        else :
            print "nothing in it"

        return "successfully sent text"
    else:
        return "Y U NO USE POST?"




# generates the encrypted image
@app.route("/to_encrypt", methods = ["POST"])
def to_encrypt():
    global filename, token, filenametoencryptin, user_key

    # Generating encryption key (256-bit)
    ekey = str(uuid.uuid4())
    k = []
    for x in ekey.split('-'):
        k.append(x)
        enc_key = ''.join(k)
    token = enc_key

    full_filename = UPLOAD_FOLDER + filename
    full_filename_to_encrypt_in = UPLOAD_FOLDER_IN + filenametoencryptin
    subprocess.call([sys.executable, 'xterminate.py', '-hide', '-m', token, '-k', token, full_filename, full_filename_to_encrypt_in])
    # res = testtoencrypt.encrypt(token, full_filename, full_filename_to_encrypt_in)
    print "token is " + token
    print "user_key is -------------------------- " + user_key
    now = time.strftime('%Y-%m-%d %H:%M:%S')
    cursor = db.cursor()
    print("Connected Successfully to database...")
    #database fields : id, user_key, token, encryptedfilepath, setvaliditytime
    # encryptedfilepath = ENCRYPTED + filenametoencryptin
    encryptedfilepath = full_filename_to_encrypt_in
    cursor.execute('''INSERT INTO stegano(efp, userKey, token, vt)
                      VALUES(%s, %s, %s, %s)''',
                      (encryptedfilepath, user_key, token, now))
    db.commit()
    fileList = os.listdir(UPLOAD_FOLDER)
    for fileName in fileList:
        os.remove(UPLOAD_FOLDER+"/"+fileName)



if __name__ == "__main__":
    app.run(host = "0.0.0.0", port = 5000, debug=True, threaded = True)
