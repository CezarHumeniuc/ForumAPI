A simple rest api for a forum.

Endpoints: 
                
                User
  GET:
	
 
 - "/api/users/{id}" returns user by id
	
 - "/api/users" (param : username) - returns user by username / if username not provided returns all users
       
  POST: 
	
 - "/api/users/signup" - (body: username, email, password) - adds user to db
  
  PATCH:
	
 - "/api/users/{id}" - (body : username, emai, password; header : user_id) - modifies existing user
 - "/api/users/make-admin/{id}" - make a user admin
 - "/api/users/ban-user/{id}" (header - user_id) - ban a user

  DELETE - "/api/users/{id}" (header: user_id) - remove a user
 
                Topics

  GET 
	- "/api/topics/{id}" - returns topic by id
  - "/api/topics" (param : title) returns topic by title / if title not provided returns all topics
  - "/api/topics/sort" (param : sortBy, sortDirection) - returns sorted topics by views / likes / date / last-updated / comments

  POST 
	- "/api/topics/create-Topic" (body: title, content; header: user_id) adds topic to db
  - "/api/topics/like/{id}" (header: user_id) increments likes on a topic
 	- "/api/topics/dislike/{id}" (header: user_id) increments dislike on topic

  DELETE 
	- "/api/topics/{id}" (header: user_id) - removes topic from db

  PATCH 
	- "/api/topics/{id)" (body: title, content; header: user_id) updates topic title and/or content

              --Comments--

  GET 
	- "/api/topics/{topicId}/comments" - returns comments from a specified topic
  - "/api/topics/comments/{id}"  - returns comment by id

  POST 
	- "/api/topics/{topicId}/add-comment" (body: content; header: user_id) - adds comment to db
	- "/api/topics/comments/like/{id}" (header: user_id) - increments like on comment
  - "/api/topics/comments/dislike/{id}" (header: user_id) - increments dislikes on comment

  DELETE 
	- "/api/topics/comments/{id}" (header: user_id) - removes comment from db
  
  PATCH  
	- "/api/topics/comments/{id}" (body: content; header: user_id) updates a commen
