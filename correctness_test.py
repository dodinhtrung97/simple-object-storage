import requests
import time
import os
import hashlib

BASE_URL = 'http://127.0.0.1:8080'
STATUS_OK = requests.codes['ok']
STATUS_BAD_REQUEST = requests.codes['bad']
STATUS_NOT_FOUND = requests.codes['not_found']

def test_create_bucket():
    bucketname = 'test'
    resp = requests.post(BASE_URL + '/' + bucketname + '?create')
    print resp.status_code
    assert resp.status_code == STATUS_OK

def test_drop_bucket():
    bucketname = 'test'
    resp = requests.delete(BASE_URL + '/' + bucketname + '?delete')
    assert resp.status_code == STATUS_OK

def test_list_object():
    bucketname = 'test'
    resp = requests.get(BASE_URL + '/' + bucketname + '?list')
    assert resp.status_code == STATUS_OK

def test_create_upload_ticket():
    bucketname = 'test'
    objectname = 'test.txt'
    resp = requests.post(BASE_URL + '/' + bucketname + '/' + objectname + '?create')
    assert resp.status_code == STATUS_OK

def test_delete_part():
    bucketname = 'test'
    objectname = 'test.txt'
    partNumber = 1
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.delete(url)
    assert resp.status_code == STATUS_OK

def test_delete_part_invalid_bucket_name():
    bucketname = 'test.'
    objectname = 'test.txt'
    partNumber = 1
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.delete(url)
    assert resp.status_code == STATUS_BAD_REQUEST

def test_complete_upload():
    bucketname = 'test'
    objectname = 'test.txt'
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?complete'
    resp = requests.post(url)
    assert resp.status_code == STATUS_OK

def test_create_upload_ticket():
    bucketname = 'test'
    objectname = 'proj1-test.mp4'
    resp = requests.post(BASE_URL + '/' + bucketname + '/' + objectname + '?create')
    assert resp.status_code == STATUS_OK

def md5(file_path):
    hash_md5 = hashlib.md5()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()

def test_upload_part():
    file_size = 0
    try:
        file_size = os.path.getsize('D:\\Mahidol\\proj1-test.mp4')
    except:
        print("file not found")

    bucketname = 'test'
    objectname = 'proj1-test.mp4'
    partNumber = 1
    checksum = md5('D:\\Mahidol\\proj1-test.mp4')
    headers = {'Content-Type': 'application/octet-stream', 'Content-Length': str(file_size), 'Content-MD5': str(checksum)}
    upload_file = {'file': open('D:\\Mahidol\\proj1-test.mp4', 'rb')}
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + str(partNumber)
    resp = requests.put(url, files=upload_file, headers=headers)
    print resp.text
    assert resp.status_code == STATUS_OK

test_create_bucket()
test_create_upload_ticket()
# test_upload_part()