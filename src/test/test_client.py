# pip install pytest requests
# pytest -v test_client.py

import pytest
import requests

url = "http://localhost:8081/"
sample_posts = [{"content": f"Test post {i}"} for i in range(20)]


@pytest.fixture(scope="module")
def create_posts():
    return []


def test_create_posts(create_posts):
    for post in sample_posts:
        response = requests.put(url + "posts", json=post)
        assert response.status_code >= 200 and response.status_code < 300
        created_post = response.json()
        create_posts.append(created_post)
    assert len(create_posts) == len(sample_posts)


def test_get_one_post(create_posts):
    post_id = create_posts[5]["id"]
    response = requests.get(url + f"posts/{post_id}")

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json() == create_posts[5]
    assert response.json()["content"] == "Test post 5"


def test_patch_post(create_posts):
    new_post = {"content": "Test post 5 updated", "id": create_posts[5]["id"]}

    response = requests.patch(url + f"posts/{new_post['id']}", json=new_post)

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json()["id"] == create_posts[5]["id"]
    assert response.json()["content"] == "Test post 5 updated"
    assert response.json()["createdAt"] == create_posts[5]["createdAt"]
    assert response.json()["updatedAt"] != create_posts[5]["updatedAt"]


def test_get_post_after_patching(create_posts):
    post_id = create_posts[5]["id"]
    response = requests.get(url + f"posts/{post_id}")

    assert response.status_code >= 200 and response.status_code < 300
    assert response.json()["id"] == post_id
    assert response.json()["content"] == "Test post 5 updated"

def test_get_posts_paginated(create_posts):
    response = requests.get(url + "posts?offset=0&limit=5")
    assert response.status_code >= 200 and response.status_code < 300
    assert len(response.json()) == 5
    last = response.json()[4]["id"]

    response = requests.get(url + f"posts?offset={4}&limit=5")
    assert response.status_code >= 200 and response.status_code < 300
    assert len(response.json()) == 5
    assert response.json()[0]["id"] == last

def test_delete_post(create_posts):
    post_id = create_posts[6]["id"]
    response = requests.delete(url + f"posts/{post_id}")
    assert response.status_code >= 200 and response.status_code < 300

def test_get_post_after_deleting(create_posts):
    post_id = create_posts[6]["id"]
    response = requests.get(url + f"posts/{post_id}")
    assert response.status_code == 404


if __name__ == "__main__":
    pytest.main(args=["-v"])