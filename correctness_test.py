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

test_create_bucket()
# test_drop_bucket()