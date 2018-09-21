import requests
import time

BASE_URL = 'http://127.0.0.1:8080'
STATUS_OK = requests.codes['ok']
STATUS_BAD_REQUEST = requests.codes['bad']
STATUS_NOT_FOUND = requests.codes['not_found']

def test_create_bucket():
    bucketname = 'test'
    resp = requests.post(BASE_URL + '/' + bucketname + '?create')
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

def test_upload_part():
    bucketname = 'test'
    objectname = 'test.txt'
    partNumber = 1
    headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f922'
    files = {'file': open('~/Desktop/test.txt', 'rb')}
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.put(url, files=files, headers=headers)
    assert resp.status_code == STATUS_OK

def test_upload_invalid_md5():
    bucketname = 'test'
    objectname = 'test.txt'
    partNumber = 1
    headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
    files = {'file': open('~/Desktop/test.txt', 'rb')}
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.put(url, files=files, headers=headers)
    assert resp.status_code == STATUS_BAD_REQUEST

def test_upload_invalid_part_number():
    bucketname = 'test'
    objectname = 'test.txt'
    partNumber = 0
    headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
    files = {'file': open('~/Desktop/test.txt', 'rb')}
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.put(url, files=files, headers=headers)
    assert resp.status_code == STATUS_BAD_REQUEST

def test_upload_invalid_object_name():
    bucketname = 'test'
    objectname = 'test.txt..'
    partNumber = 0
    headers = {'Content-Type': 'application/octet-stream', 'Content-Length': 16, 'Content-MD5': 'a431efdb90680b8d31d6d5574b02f92'
    files = {'file': open('~/Desktop/test.txt', 'rb')}
    url = BASE_URL + '/' + bucketname + '/' + objectname + '?partNumber=' + partNumber
    resp = requests.put(url, files=files, headers=headers)
    assert resp.status_code == STATUS_BAD_REQUEST

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