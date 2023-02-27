# How to Run Blog App
1. Build the project with gradle task bootJar
2. Just from the root directory of the project run command
    ```docker-compose up -d```
3. Then you can test the application using PostMan.


# API endpoints

These endpoints allow you to do the jobs defined for version Version 0.1.0.

## GET
[localhost:8080/bloagapp/users/{username}/blogs]<br/>
[localhost:8080/bloagapp/blogs/tags/{tag}] <br/>

## POST
[localhost:8080/bloagapp/blogs](#post-1billingstart-trialjson) <br/>


## PUT
[localhost:8080/bloagapp/blogs/{id}](#post-1billingstart-trialjson) <br/>
[localhost:8080/bloagapp/blogs/{id}/tags/{tag}](#post-1billingstart-trialjson) <br/>

## DELETE
[localhost:8080/bloagapp/blogs/{id}/tags/{tag}](#post-1billingstart-trialjson) <br/>
___

### GET /users/{username}/blogs
Get summary of blogs of a given username

**Parameters**

|          Name | Required |  Type   |
| -------------:|:--------:|:-------:| 
|     `username` | required | string  |

**Response**

```
{
  [{"title":"Blogpost1","shortSummary":"Lorem ipsum dolor si"}]
}
```
___
### GET /blogs/tags/{tag}
Get all blogs with given tag

**Parameters**

|          Name | Required |  Type   |                               
| -------------:|:--------:|:-------:| 
|     `tag` | required | string  |

**Response**

```
[{"id":1,"title":"Blogpost1","body":"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut al","createdBy":{"id":1,"name":"Ali","surname":"Veli","username":"aliveli"},"blogtags":[{"id":1,"tag":"First Tag"}]}]
```
___
### POST /blogs
Save the blog post

**Parameters**
```
{
    "username" : "aliveli",
    "title" : "new",
    "body" : "new" 
}
```
**Response**

```
{
    "id": -37,
    "title": "new",
    "body": "new",
    "createdBy": {
        "id": 1,
        "name": "Ali",
        "surname": "Veli",
        "username": "aliveli"
    },
    "blogtags": []
}

```
___


### PUT /blogs/{id}
Update title and body of the blog post

**Parameters**

|          Name | Required |  Type   |
| -------------:|:--------:|:-------:| 
|     `id` | required | string  |
```json
{
    "id": 1,
    "title": "newest",
    "body": "newest",
    "createdBy": {
        "id": 1,
        "name": "Ali",
        "surname": "Veli",
        "username": "aliveli"
    },
    "blogtags": []
}
```

**Response**

```
{
    "id": 1,
    "title": "newest",
    "body": "newest",
    "createdBy": {
        "id": 1,
        "name": "Ali",
        "surname": "Veli",
        "username": "aliveli"
    },
    "blogtags": [
        {
            "id": 1,
            "tag": "First Tag"
        }
    ]
}

```
___
### PUT /blogs/{id}/tags/{tag}
Attach tag to the blog post

**Parameters**

|          Name | Required |  Type   |
| -------------:|:--------:|:-------:|
|     `id` | required | string  |
|     `tag`| required | string  |

```json
{
    "id": 1,
    "title": "newest",
    "body": "newest",
    "createdBy": {
        "id": 1,
        "name": "Ali",
        "surname": "Veli",
        "username": "aliveli"
    },
    "blogtags": []
}
```

**Response**

```
200OK

```
___

### DELETE /blogs/{id}/tags/{tag}
Detach tag for blog post

**Parameters**

|          Name | Required |  Type   |
| -------------:|:--------:|:-------:| 
|     `id` | required | string  |
|     `tag`| required | string  |

```json
{
    "id": 1,
    "title": "newest",
    "body": "newest",
    "createdBy": {
        "id": 1,
        "name": "Ali",
        "surname": "Veli",
        "username": "aliveli"
    },
    "blogtags": ["First Tag"]
}

```
**Response**

```
200OK

```
___

