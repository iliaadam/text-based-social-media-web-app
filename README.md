# text-based-social-media-web-app

# Text-Based Social Media Web App

This web application is built using Spring Boot, IntelliJ IDEA 2023.2.2, and Java 17.

## Java and JDK Versions

- Java Version: 17
- JDK Version: 17

## Database Tables

The application uses the following database tables:

1. `users`: Stores user information, including usernames and passwords.
2. `followers`: Keeps track of user followers and followees.
3. `posts`: Stores user posts and associated content.
4. `comments`: Contains user comments on posts.

## About the Code

The code for this web application implements a text-based social media platform. It includes features such as user registration, login, following/unfollowing other users, creating and commenting on posts, and displaying a user's posts and their followers' posts.

The application is built using Spring Boot, providing a robust and scalable foundation for web development. It utilizes various Spring components such as controllers, repositories, and services to manage user data and interactions. The application also integrates with a database to persist user-related information and posts.

Feel free to explore the codebase to learn more about how the application works and how it leverages the Spring Boot framework for building web applications.

## Prerequisites to run the project

First, clone this repository to your local machine using the following command:
git clone https://github.com/your-username/your-repository.git

To run this project you need to install the postgresql and setup the database.

## Database Configuration

1. **Install PostgreSQL:** Download and install PostgreSQL from the official website if you haven't already. [PostgreSQL Database](https://www.postgresql.org/download/)

2. **Create the Database:**
   Open the pgAdmin 4, you either can use the postgres database which is build in (this is what i am using) or create you own by right click on Databases and click create.
   Bellow is the code you need to create all the tables:
   
   ## Table users:
   
     -- Table: public.users
     CREATE TABLE IF NOT EXISTS public.users
     (
        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
        username character varying(255) COLLATE pg_catalog."default",
        password character varying(255) COLLATE pg_catalog."default",
        premium boolean,
        CONSTRAINT users_pkey PRIMARY KEY (id)
     )

   ## Table posts:

    -- Table: public.posts
    CREATE TABLE IF NOT EXISTS public.posts
    (
        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
        user_id integer NOT NULL,
        content character varying(1000) COLLATE pg_catalog."default",
        CONSTRAINT posts_pkey PRIMARY KEY (id),
        CONSTRAINT posts_user_id_fkey FOREIGN KEY (user_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )

   ## Table followers:

    -- Table: public.followers
    CREATE TABLE IF NOT EXISTS public.followers
    (
        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
        follower_id integer NOT NULL,
        followee_id integer NOT NULL,
        CONSTRAINT followers_pkey PRIMARY KEY (id),
        CONSTRAINT followee_user_id_fkey FOREIGN KEY (followee_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT follower_user_id_fkey FOREIGN KEY (follower_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
   
   ## Table comments

    -- Table: public.comments
    CREATE TABLE IF NOT EXISTS public.comments
    (
        id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
        user_id integer NOT NULL,
        post_id integer NOT NULL,
        content character varying(255) COLLATE pg_catalog."default",
        CONSTRAINT comments_pkey PRIMARY KEY (id),
        CONSTRAINT comments_post_id_fkey FOREIGN KEY (post_id)
            REFERENCES public.posts (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION,
        CONSTRAINT comments_user_id_fkey FOREIGN KEY (user_id)
            REFERENCES public.users (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
    )
   
4. **Setup the Java:** In the java open the application.properties and change the 3 first lines as follows:
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
   spring.datasource.username=your_database_username
   spring.datasource.password=your_database_password

   replace 'your_database_name', 'your_database_username', 'your_database_password' with the corrects.


## Now you are ready to run this project:
  Run the DemoApplication class, it takes about 10 seconds to run so wait, after open in your browser the folowing url: http://localhost:8080/, and thats it.


   


   

## THE CODE IS IN THE MASTER BRANCH CURRENTLY, I WILL TRY TO FIX IT !!!
