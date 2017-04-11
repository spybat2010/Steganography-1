"""
Code used from https://github.com/fgrimme/Matroschka
"""


try:
    import Image
except:
    from PIL import Image

import argparse
import hashlib
import hmac
import os
import steganohide as stg
import xtea
import sys

def create_hmac(mac_pass, msg_bytes):
    
    return hmac.new(
        mac_pass, msg_bytes, digestmod=hashlib.sha256).digest()


def get_msg(img):
    
    i = Image.open('%s.ste' % img)
    secret = stg.extract_msg(i)
    mac = secret.split('--:--')[0]
    # print 'HMAC hex is: \n%s\n' % mac.encode('hex')
    data = secret.split('--:--')[1]
    print 'The hidden message is: \n%s\n' % data
    check_hmac(mac)
    i.show()


def check_hmac(mac, data):

    h_mac = hmac.new(args['m'], bytes(data), digestmod=hashlib.sha256).digest()
    print 'HMAC validation: \n%s\n' % str(h_mac == mac)

def hash_128_bit_pass(passwd):
    """
    Create a hash of the given password using the
    SHA-256 digest algorithm.
    """
    h = hashlib.sha256()
    h.update(passwd)
    return h.hexdigest()[:16]


def crypt(key, data, iv):
    
    return xtea.crypt(key, data, iv)


def read_image(image_path):
    =
    print "image path is " + image_path
    image_name = os.path.basename(image_path)
    full_path = "C:/Users/kunal/Workspace/PycharmProjects/steganoisro/toencryptin/" + image_name
    if not os.path.exists(full_path):
        raise IOError('File does not exist: %s' % image_path)
    else:
        return Image.open(full_path)


def read_text(text_path):
    
    if not os.path.exists(text_path):
            raise IOError('File does not exist: %s' % text_path)
    return open(text_path).read()


def encrypt(data_type):
    print "encryption called ... "
    igest algorithm and the supllied image or text data
    h_mac = create_hmac(args['m'], bytes(data))

    secret = '%s--:--%s' % (h_mac, data)
    key = hash_128_bit_pass(args['k'])

    iv = os.urandom(8)
    encrypted_secret = crypt(key, secret, iv)

    matroschka = stg.hide_msg(image, '%s--:--%s--:--%s' % (data_type, iv, encrypted_secret))

    print "length of the arguement is ", len(args['k'])
    matroschka.save(args['image'])
    print "message saved at path " + str(args['image'])

    print "successfully encrypted your secret message"


def decrypt():
    filepath = "C:/Users/kunal/Workspace/PycharmProjects/steganoisro/toencryptin/"
    bname = os.path.basename(args['image'])
    print " file to decrypt is " + str(filepath + bname)
    image = Image.open(filepath + bname)
    matroschka = stg.extract_msg(image)
    data_type, iv, encrypted_secret = matroschka.split('--:--')
    key = hash_128_bit_pass(args['k'])
    decrypted_secret = crypt(key, encrypted_secret, iv)
    mac, data = decrypted_secret.split('--:--')

    if data_type == 'image':
        ipath = "C:\Users\kunal\Workspace\PycharmProjects\steganoisro\decrypted\secret.png"
        print "the secret image is stored under: " + ipath
        fh = open(ipath, "wb")
        fh.write(data.decode('base64'))
        fh.close()
        return ipath
        # Image.open(ipath).show()
    else:
        with open("C:\Users\kunal\Workspace\PycharmProjects\steganoisro\\templates\decrypted.json", "w") as text_file:
            text_file.write('{"data" : ' + '"' + data + '"}')
        return
        # print 'The hidden message is: \n%s\n' % data

    print 'HMAC hex is: \n%s\n' % mac.encode('hex')
    check_hmac(mac, data)


if __name__ == '__main__':
    # Add the command-line arguments
    parser = argparse.ArgumentParser(description='Description of your program')
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('-hide', help='encrypt', action='store_true')
    group.add_argument('-open', help='decrypt', action='store_true')
    parser.add_argument(
        '-m', metavar='macpasswd', help='macpassword', required=True)
    parser.add_argument('-k', metavar='passwd', help='password', required=True)
    parser.add_argument('data', nargs='?')
    parser.add_argument('image')

    args = vars(parser.parse_args())

    # Check if the required args are supplied
    # If not print user feedback and exit
    if args['data']:
        if args['data'].endswith('png') or args['data'].endswith('jpg'):
            import base64
            data_type = 'image'
            with open(args['data'], "rb") as imageFile:
                data = base64.b64encode(imageFile.read())
        elif args['data'].endswith('txt'):
            # print "Text file to be encrypted"
            data_type = 'text'
            data = read_text(args['data'])
        else:
            print "need secret message either as .txt or .png file"
            sys.exit(0)

    if args['image']:
        # image = read_image("C:\Users\kunal\Workspace\PycharmProjects\steganoisro\encrypted\encrypted.jpg")
        image = read_image(args['image'])
    else:
        print "need image to embed data"
        sys.exit(0)

    # encrypt the secret message
    if args['hide']:
        # print "data type is " + data_type
        encrypt(data_type)

    # decrypt the secret message
    if args['open']:
        decrypt()
