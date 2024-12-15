# pip install pytest requests
# pytest -v src/test/test_client.py

import time

import pytest
import requests

url = "http://localhost:8081/"
sample_posts = [{"content": f"Test post {i}"} for i in range(20)]


@pytest.fixture(scope="module")
def create_posts():
    return []


@pytest.fixture(scope="module")
def user():
    return {"login": f"testuser{time.time()}", "password": "12345"}


@pytest.fixture(scope="module")
def user2():
    return {"login": f"testuser_2_{time.time()}", "password": "12345"}


@pytest.fixture(scope="module")
def auth_token(user):
    requests.post(url + "register", json=user)
    response = requests.post(url + "login", json=user)
    token = response.json()["token"]
    return {"Authorization": f"Bearer {token}"}


@pytest.fixture(scope="module")
def auth_token_2(user2):
    requests.post(url + "register", json=user2)
    response = requests.post(url + "login", json=user2)
    token = response.json()["token"]
    return {"Authorization": f"Bearer {token}"}


def test_register_user(user):
    response = requests.post(url + "register", json=user)
    assert response.status_code >= 200 and response.status_code < 300


def test_login_user(user):
    response = requests.post(url + "login", json=user)
    assert response.status_code >= 200 and response.status_code < 300


def test_register_same_name_user(user):
    response = requests.post(url + "register", json=user)
    assert response.status_code == 400


def test_create_posts(create_posts, auth_token):
    for post in sample_posts:
        response = requests.put(url + "posts", json=post, headers=auth_token)
        assert response.status_code >= 200 and response.status_code < 300
        created_post = response.json()
        create_posts.append(created_post)
    assert len(create_posts) == len(sample_posts)


def test_get_one_post(create_posts, auth_token):
    post_id = create_posts[5]["id"]
    response = requests.get(url + f"posts/{post_id}", headers=auth_token)

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json() == create_posts[5]
    assert response.json()["content"] == "Test post 5"


def test_patch_post(create_posts, auth_token):
    new_post = {"content": "Test post 5 updated"}

    response = requests.patch(
        url + f"posts/{create_posts[5]['id']}", json=new_post, headers=auth_token
    )

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json()["id"] == create_posts[5]["id"]
    assert response.json()["content"] == "Test post 5 updated"
    assert response.json()["createdAt"] == create_posts[5]["createdAt"]
    assert response.json()["updatedAt"] != create_posts[5]["updatedAt"]


def test_get_post_after_patching(create_posts, auth_token):
    post_id = create_posts[5]["id"]
    response = requests.get(url + f"posts/{post_id}", headers=auth_token)

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json()["id"] == post_id
    assert response.json()["content"] == "Test post 5 updated"


def test_failing_patch_foreign_post(create_posts, auth_token_2):
    post_id = create_posts[5]["id"]
    response = requests.patch(
        url + f"posts/{post_id}",
        json={"content": "Test post 5 updated WRONG"},
        headers=auth_token_2,
    )

    assert response.status_code == 403


def test_get_posts_paginated(create_posts, auth_token):
    response = requests.get(url + "posts?offset=0&limit=5", headers=auth_token)
    assert response.status_code >= 200 and response.status_code < 300
    assert len(response.json()) == 5
    last = response.json()[4]["id"]

    response = requests.get(url + f"posts?offset={4}&limit=5", headers=auth_token)
    assert response.status_code >= 200 and response.status_code < 300
    assert len(response.json()) == 5
    assert response.json()[0]["id"] == last


def test_delete_post(create_posts, auth_token):
    post_id = create_posts[6]["id"]
    response = requests.delete(url + f"posts/{post_id}", headers=auth_token)
    assert response.status_code >= 200 and response.status_code < 300


def test_get_post_after_deleting(create_posts, auth_token):
    post_id = create_posts[6]["id"]
    response = requests.get(url + f"posts/{post_id}", headers=auth_token)
    assert response.status_code == 404


def test_failing_delete_foreign_post(create_posts, auth_token_2):
    post_id = create_posts[3]["id"]
    response = requests.delete(url + f"posts/{post_id}", headers=auth_token_2)
    assert response.status_code == 403


if __name__ == "__main__":
    pytest.main(args=["-v"])
